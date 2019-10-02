
package com.example.miskewolf.opencvradi;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
// required knowlegde for JavaCameraView & OpenCV & usage of OpenCV gained here https://www.youtube.com/channel/UCvRTnofGf_Svg2Q-94KUm8g/videos
// resources from which I learned about Eigenfaces --> https://docs.opencv.org/2.4/modules/contrib/doc/facerec/facerec_tutorial.html , https://www.youtube.com/watch?v=B3XHEkLxjJE, https://www.youtube.com/watch?v=WfrHM24aFIE,https://github.com/Lauszus/FaceRecognitionLib
// Help source: http://stackoverflow.com/questions/15762905/how-can-i-display-a-list-view-in-an-android-alert-dialog
// Help source: http://stackoverflow.com/a/7636468/2175837
public class Recognition extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = Recognition.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CODE = 0;
    private ArrayList<Mat> images;
    private ArrayList<String> imagesLabels;
    private String[] uniqueLabels;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba, mGray;
    private DrawerLayout popRelative;
    private Toast mToast;
    private Button takepic;
    private boolean useEigenfaces;
    private Toolbar mToolbar;
    private SeekBarArrows mThresholdFace, mThresholdDistance;
    private float faceThreshold, distanceThreshold;
    private int maximumImages;
    private SharedPreferences prefs;
    private TinyDB tinydb;
    private static final int PICK_IMAGE = 100;
    private String msg="Try again";
    private TextView popupText;
    private boolean fakt=true;
    private OpencvNativeClass.TrainFacesTask mTrainFacesTask;

    private void showToast(String message, int duration) {
        if (duration != Toast.LENGTH_SHORT && duration != Toast.LENGTH_LONG)
            throw new IllegalArgumentException();
        if (mToast != null && mToast.getView().isShown())
            mToast.cancel(); // Close the toast if it is already open
        mToast = Toast.makeText(this, message, duration);
        mToast.show();
    }

    private void addLabel(String string) {
        String label = string.substring(0, 1).toUpperCase(Locale.US) + string.substring(1).trim().toLowerCase(Locale.US); // Ime je uvek uppercase i lowercase
        imagesLabels.add(label); // Dodaj label
        Log.i(TAG, "Label: " + label);

        trainFaces(); // Kada zavrsimo sa label/dodaj lica
    }

    /**
     * Treniraj lica uz pomoc slika(storage)
     *
     * @return false if the task running(..)
     */
    private boolean trainFaces() {
        if (images.isEmpty())
            return true; // Moguce da je prazna

        if (mTrainFacesTask != null && mTrainFacesTask.getStatus() != AsyncTask.Status.FINISHED) {
            Log.i(TAG, "mTrainFacesTask is still running");
            return false;
        }

        Mat imagesMatrix = new Mat((int) images.get(0).total(), images.size(), images.get(0).type());
        for (int i = 0; i < images.size(); i++)
            images.get(i).copyTo(imagesMatrix.col(i));

        Log.i(TAG, "Images height: " + imagesMatrix.height() + " Width: " + imagesMatrix.width() + " total: " + imagesMatrix.total());


        if (useEigenfaces) {
            Log.i(TAG, "Training Eigenfaces");
            showToast(getString(R.string.traininggg) + getResources().getString(R.string.eigenfaces), Toast.LENGTH_SHORT);

            mTrainFacesTask = new OpencvNativeClass.TrainFacesTask(imagesMatrix, trainFacesTaskCallback);
        }
        mTrainFacesTask.execute();

        return true;
    }

    private OpencvNativeClass.TrainFacesTask.Callback trainFacesTaskCallback = new OpencvNativeClass.TrainFacesTask.Callback() {
        @Override
        public void onTrainFacesComplete(boolean result) {
            if (result) {
                showToast(getString(R.string.training_complete), Toast.LENGTH_SHORT);
                takepic.setVisibility(View.VISIBLE);
            }
            else
                showToast(getString(R.string.training_failed), Toast.LENGTH_LONG);
        }
    };

    private void showLabelsDialog() {
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.pop_up, null);
        popRelative=(DrawerLayout)findViewById(R.id.drawer_layout);
        takepic.setClickable(false);
        popupWindow = new PopupWindow(container, popRelative.getWidth()*10/11, popRelative.getHeight()/6, true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(false);
        fakt=false;
        popupText=(TextView) popupWindow.getContentView().findViewById(R.id.crazy_pop_up);
        popupText.setText(msg);
        popupWindow.showAtLocation(popRelative, Gravity.NO_GRAVITY,  popRelative.getWidth()*1/22, popRelative.getHeight()*5/12);

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(false);
                popupWindow.dismiss();
                takepic.setClickable(true);
                Build();
                return false;
            }
        });

    }

    public void Build(){
        Set<String> uniqueLabelsSet = new HashSet<>(imagesLabels);
        if (!uniqueLabelsSet.isEmpty()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Recognition.this);
            builder.setTitle("Select label:");
            builder.setMessage("In order to improve future recognitions, please select a person from the list or add new face");
            builder.setPositiveButton(R.string.new_face, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    showEnterLabelDialog();
                }
            });
            builder.setNegativeButton(R.string.cancelll, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    fakt=false;
                    images.remove(images.size() - 1);
                }
            });
            builder.setCancelable(false);

            String[] uniqueLabels = uniqueLabelsSet.toArray(new String[uniqueLabelsSet.size()]);
            Arrays.sort(uniqueLabels);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Recognition.this, android.R.layout.simple_list_item_1, uniqueLabels) {
                @Override
                public @NonNull
                View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    if (getResources().getBoolean(R.bool.isTablet))
                        textView.setTextSize(20);
                    else
                        textView.setTextSize(18);
                    return textView;
                }
            };
            ListView mListView = new ListView(Recognition.this);
            mListView.setAdapter(arrayAdapter);
            builder.setView(mListView);

            final AlertDialog dialog = builder.show();

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    fakt=false;
                    dialog.dismiss();
                    addLabel(arrayAdapter.getItem(position));
                }
            });
        } else
            showEnterLabelDialog();
    }

    private void showEnterLabelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Recognition.this);
        builder.setTitle("Please enter your name:");

        final EditText input = new EditText(Recognition.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Submit", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                images.remove(images.size() - 1);
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button mButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String string = input.getText().toString().trim();
                        if (!string.isEmpty()) {

                            dialog.dismiss();
                            addLabel(string);
                        }
                    }
                });
            }
        });


        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_recognition);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar); // Sets the Toolbar to act as the ActionBar for this Activity window

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        useEigenfaces = true;


        tinydb = new TinyDB(this);

        mThresholdFace = (SeekBarArrows) findViewById(R.id.threshold_face);
        mThresholdFace.setOnSeekBarArrowsChangeListener(new SeekBarArrows.OnSeekBarArrowsChangeListener() {
            @Override
            public void onProgressChanged(float progress) {

                faceThreshold = progress;
            }
        });
        faceThreshold = mThresholdFace.getProgress();

        mThresholdDistance = (SeekBarArrows) findViewById(R.id.threshold_distance);
        mThresholdDistance.setOnSeekBarArrowsChangeListener(new SeekBarArrows.OnSeekBarArrowsChangeListener() {
            @Override
            public void onProgressChanged(float progress) {
                Log.i(TAG, "Distance threshold: " + mThresholdDistance.progressToString(progress));
                distanceThreshold = progress;
            }
        });
        distanceThreshold = mThresholdDistance.getProgress();


        maximumImages = 25;


        takepic= (Button) findViewById(R.id.take_picture_button);
        takepic.setOnClickListener(new View.OnClickListener() {
            OpencvNativeClass.MeasureDistTask mMeasureDistTask;

            @Override
            public void onClick(View v) {

                if (mMeasureDistTask != null && mMeasureDistTask.getStatus() != AsyncTask.Status.FINISHED) {
                    Log.i(TAG, "mMeasureDistTask is still running");
                    showToast(getString(R.string.processing_old), Toast.LENGTH_SHORT);
                    return;
                }
                if (mTrainFacesTask != null && mTrainFacesTask.getStatus() != AsyncTask.Status.FINISHED) {
                    Log.i(TAG, "mTrainFacesTask is still running");
                    showToast(getString(R.string.running), Toast.LENGTH_SHORT);
                    return;
                }

                Log.i(TAG, "Gray height: " + mGray.height() + " Width: " + mGray.width() + " total: " + mGray.total());
                if (mGray.total() == 0)
                    return;
                Size imageSize = new Size(200, 200.0f / ((float) mGray.width() / (float) mGray.height()));
                Imgproc.resize(mGray, mGray, imageSize);
                Log.i(TAG, "Small gray height: " + mGray.height() + " Width: " + mGray.width() + " total: " + mGray.total());


                Mat image = mGray.reshape(0, (int) mGray.total());
                Log.i(TAG, "Vector height: " + image.height() + " Width: " + image.width() + " total: " + image.total());
                images.add(image); // Add current image to the array

                if (images.size() > 25) {
                    images.remove(0);
                    imagesLabels.remove(0);
                    Log.i(TAG, "The number of images is limited to: " + images.size());
                }

                // Calculate normalized Euclidean distance
                mMeasureDistTask = new OpencvNativeClass.MeasureDistTask(useEigenfaces, measureDistTaskCallback);
                mMeasureDistTask.execute(image);


            }
        });

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_java_surface_view);
        mOpenCvCameraView.setCameraIndex(prefs.getInt("mCameraIndex", CameraBridgeViewBase.CAMERA_ID_FRONT));
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    private OpencvNativeClass.MeasureDistTask.Callback measureDistTaskCallback = new OpencvNativeClass.MeasureDistTask.Callback() {
        @Override
        public void onMeasureDistComplete(Bundle bundle) {
            if (bundle == null) {
                showToast(getString(R.string.failed_to_measure), Toast.LENGTH_LONG);
                return;
            }

            float minDist = bundle.getFloat(OpencvNativeClass.MeasureDistTask.MIN_DIST_FLOAT);
            if (minDist != -1) {
                int minIndex = bundle.getInt(OpencvNativeClass.MeasureDistTask.MIN_DIST_INDEX_INT);
                float faceDist = bundle.getFloat(OpencvNativeClass.MeasureDistTask.DIST_FACE_FLOAT);
                if (imagesLabels.size() > minIndex) { // Just to be sure
                    Log.i(TAG, "dist[" + minIndex + "]: " + minDist + ", face dist: " + faceDist + ", label: " + imagesLabels.get(minIndex));

                    String minDistString = String.format(Locale.US, "%.4f", minDist);
                    String faceDistString = String.format(Locale.US, "%.4f", faceDist);

                    if (faceDist < faceThreshold && minDist < distanceThreshold) { // 1. Near face space and near a face class


                        showToast(getString(R.string.face_detected) +" "+ imagesLabels.get(minIndex), Toast.LENGTH_LONG);
                        msg=getString(R.string.face_detected)+ " "+ imagesLabels.get(minIndex);
                    }else if (faceDist < faceThreshold) {// 2. Near face space but not near a known face class
                        msg=getString(R.string.unknown_face_result);
                        showToast(getString(R.string.unknown_face_result), Toast.LENGTH_LONG);
                    }else if (minDist < distanceThreshold) { // 3. Distant from face space and near a face class
                        msg=getString(R.string.false_recognition);
                        showToast(getString(R.string.false_recognition), Toast.LENGTH_LONG);
                    }else { // 4. Distant from face space and not near a known face class.
                        msg = getString(R.string.img_not_a_face);
                        showToast(getString(R.string.img_not_a_face), Toast.LENGTH_LONG);
                    }
                    Log.d("TAKOVO","VECE OD MIN INDEX min index je "+minIndex+" a size labes "+imagesLabels.size());
                }else{
                    Log.d("TAKOVO","NIJE VECE OD MIN INDEX min index je "+minIndex+" a size labes "+imagesLabels.size());
                }
            } else {
                Log.d("TAKOVO","ODE U ELSE ..");
                Log.w(TAG, "Array is null");
                Log.d("Tagovanje", "minDist= %d" + minDist);
                if (useEigenfaces || imagesLabels == null || imagesLabels.size() > 1) {
                    msg=getString(R.string.keep_training);
                    showToast(getString(R.string.keep_training), Toast.LENGTH_SHORT);
                }else {
                    msg=getString(R.string.two_different);
                    showToast(getString(R.string.two_different), Toast.LENGTH_SHORT);
                }
            }
            showLabelsDialog();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadOpenCV();
                } else {
                    showToast(getString(R.string.perm_required), Toast.LENGTH_LONG);
                    finish();
                }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onStart() {
        super.onStart();
        float progress = prefs.getFloat("faceThreshold", -1);
        if (progress != -1)
            mThresholdFace.setProgress(progress);
        progress = prefs.getFloat("distanceThreshold", -1);
        if (progress != -1)
            mThresholdDistance.setProgress(progress);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(!takepic.isClickable())
            images.remove(images.size() - 1);
        Editor editor = prefs.edit();
        editor.putFloat("faceThreshold", faceThreshold);
        editor.putFloat("distanceThreshold", distanceThreshold);
        editor.putInt("maximumImages", maximumImages);
        editor.putBoolean("useEigenfaces", useEigenfaces);
        editor.putInt("mCameraIndex", mOpenCvCameraView.mCameraIndex);
        editor.apply();

        if (images != null && imagesLabels != null ) {
            Log.d("MOJTAG","RAD");
            tinydb.putListString("imagesLabels", imagesLabels);
            tinydb.putListMat("images", images);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
        else
            loadOpenCV();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    OpencvNativeClass.loadNativeLibraries();
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();


                    DatabaseReference myRef1=database.getReference("images");
                    myRef1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value="";
                            for(int i=0;i<dataSnapshot.getChildrenCount();i++) {
                                if(!dataSnapshot.child("" + i).getValue(String.class).equals(""))
                                    value += dataSnapshot.child("" + i).getValue(String.class) + "‚‗‚";

                            }

                            if(value==null) value="";

                            Log.d("MOJTAG", "Value is FUCK: " + value);

                            ArrayList<String> imgaa = new ArrayList<String>(Arrays.asList(TextUtils.split(value, "‚‗‚")));
                            ArrayList<String> objStrings = imgaa;
                            ArrayList<Mat> objects = new ArrayList<Mat>();

                            for (String jObjString : objStrings) {
                                if(!jObjString.equals("")) {
                                    byte[] data = Base64.decode(jObjString, Base64.DEFAULT);
                                    Mat mat = new Mat(data.length, 1, CvType.CV_8U);
                                    mat.put(0, 0, data);
                                    objects.add(mat);
                                }
                            }
                            images = objects;

                            FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                            DatabaseReference myRef=database1.getReference("imagesLabels");
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String value =  dataSnapshot.getValue(String.class);
                                    if(value==null) value="";

                                    Log.d("MOJTAG", "Value is: " + value);
                                    imagesLabels = new ArrayList<String>(Arrays.asList(TextUtils.split(value, "‚‗‚")));

                                    Log.i(TAG, "Number of images: " + images.size() + ". Number of labels: " + imagesLabels.size());
                                    if (!images.isEmpty()) {
                                        trainFaces();
                                        Log.i(TAG, "Images height: " + images.get(0).height() + " Width: " + images.get(0).width() + " total: " + images.get(0).total());
                                    }
                                    Log.i(TAG, "Labels: " + imagesLabels);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    Log.w("MOJTAG", "Failed to read value.", error.toException());
                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void loadOpenCV() {
        if (!OpenCVLoader.initDebug(true)) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(fakt==false)
        images.remove(images.size() - 1);

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }


    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat mGrayTmp = inputFrame.gray();
        Mat mRgbaTmp = inputFrame.rgba();
        int orientation = mOpenCvCameraView.getScreenOrientation();
        if (mOpenCvCameraView.isEmulator())
            Core.flip(mRgbaTmp, mRgbaTmp, 1);
        else {
            switch (orientation) { // RGB image
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT)
                        Core.flip(mRgbaTmp, mRgbaTmp, 0);
                    else
                        Core.flip(mRgbaTmp, mRgbaTmp, -1);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT)
                        Core.flip(mRgbaTmp, mRgbaTmp, 1);
                    break;
            }
            switch (orientation) {
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    Core.transpose(mGrayTmp, mGrayTmp);
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT)
                        Core.flip(mGrayTmp, mGrayTmp, -1);
                    else
                        Core.flip(mGrayTmp, mGrayTmp, 1);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    Core.transpose(mGrayTmp, mGrayTmp);
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK)
                        Core.flip(mGrayTmp, mGrayTmp, 0);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT)
                        Core.flip(mGrayTmp, mGrayTmp, 1);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    Core.flip(mGrayTmp, mGrayTmp, 0);
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK)
                        Core.flip(mGrayTmp, mGrayTmp, 1);
                    break;
            }
        }

        mGray = mGrayTmp;
        mRgba = mRgbaTmp;

        return mRgba;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void SaveImage(Mat mat) {
        Mat mIntermediateMat = new Mat();

        if (mat.channels() == 1)
            Imgproc.cvtColor(mat, mIntermediateMat, Imgproc.COLOR_GRAY2BGR);
        else
            Imgproc.cvtColor(mat, mIntermediateMat, Imgproc.COLOR_RGBA2BGR);

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TAG); // Save pictures in Pictures directory
        path.mkdir();
        String fileName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(new Date()) + ".png";
        File file = new File(path, fileName);

        boolean bool = Highgui.imwrite(file.toString(), mIntermediateMat);

        if (bool)
            Log.i(TAG, "SUCCESS writing image to external storage");
        else
            Log.e(TAG, "Failed writing image to external storage");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_face_recognition_app, menu);
        MenuItem menuItem = menu.findItem(R.id.flip_camera);
        if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT)
            menuItem.setIcon(R.drawable.ic_camera_rear_white_24dp);
        else
            menuItem.setIcon(R.drawable.ic_camera_front_white_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.flip_camera:
                mOpenCvCameraView.flipCamera();

                View v = mToolbar.findViewById(R.id.flip_camera);
                ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotationY", v.getRotationY() + 180.0f);
                animator.setDuration(500);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        supportInvalidateOptionsMenu();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void OpenGallery() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = Uri.parse("content://media/external/images/media/4615");
            Log.d("Pathh"," "+ imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        Log.d("radiii", "s " + imagePath);
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.d("RotateImage", "Exif orientation: " + orientation);
            Log.d("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("neradd", "cc");
        }
        return rotate;
    }
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
