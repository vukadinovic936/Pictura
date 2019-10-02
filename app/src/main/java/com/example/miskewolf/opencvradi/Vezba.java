package com.example.miskewolf.opencvradi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.miskewolf.opencvradi.CameraSourcePreview;
import com.example.miskewolf.opencvradi.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import steelkiwi.com.library.DotsLoaderView;

//OpenCv face filters were too slow on Camera (probably due to slow face recognition through frames) so I had to use this way
public class Vezba extends AppCompatActivity {
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    int Munem = 0;
    ImageView imgV;
    AnimationDrawable anim;
    FrameLayout progressBarHolder;
    Button button;
    DotsLoaderView dotsLoader;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_vezba);
        imgV=(ImageView)findViewById(R.id.animat);
        imgV.setBackgroundResource(R.drawable.anim_loading);
        progressBarHolder=(FrameLayout) findViewById(R.id.progressBarHolder);
        anim=(AnimationDrawable)imgV.getBackground();
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyTask().execute();
            }
        });


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
        findViewById(R.id.promeni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Munem++;
                Munem = Munem % 8;
                try {
                    switch (Munem) {

                        case 0:
                            FaceGraphic.bitmap = BitmapFactory.decodeResource(Vezba.this.getResources(), R.drawable.death);
                            break;
                        case 1:
                            FaceGraphic.bitmap = BitmapFactory.decodeResource(Vezba.this.getResources(), R.drawable.joker);
                            break;
                        case 2:
                            FaceGraphic.bitmap = BitmapFactory.decodeResource(Vezba.this.getResources(), R.drawable.poke);
                            break;
                        case 3:
                            FaceGraphic.bitmap = BitmapFactory.decodeResource(Vezba.this.getResources(), R.drawable.fredy);
                            break;
                        case 4:
                            FaceGraphic.bitmap = BitmapFactory.decodeResource(Vezba.this.getResources(), R.drawable.skull);
                            break;
                        case 5:
                            FaceGraphic.bitmap = BitmapFactory.decodeResource(Vezba.this.getResources(), R.drawable.batman);
                            break;
                        case 6:
                            FaceGraphic.bitmap = BitmapFactory.decodeResource(Vezba.this.getResources(), R.drawable.harley);
                            break;
                        case 7:
                            FaceGraphic.bitmap = BitmapFactory.decodeResource(Vezba.this.getResources(), R.drawable.bart);
                            break;
                        default:
                            FaceGraphic.bitmap = BitmapFactory.decodeResource(Vezba.this.getResources(), R.drawable.death);
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {

            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

    }


    @Override
    protected void onResume() {
        super.onResume();
            progressBarHolder.setVisibility(View.GONE);
            anim.stop();
            startCameraSource();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    private void startCameraSource() {

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }


        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);

            mFaceGraphic.updateFace(face);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    void save(Bitmap bitmap) {
        MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "Filter",
                "Pictura++"

        );
        Toast.makeText(getApplicationContext(), getString(R.string.img_saved), Toast.LENGTH_LONG).show();
    }

    public Bitmap getScreenShot() {
        Bitmap bitmap;
        View view = findViewById(R.id.faceOverlay);// get ur root view id
        view.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        Bitmap screenshot = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenshot);
        view.draw(canvas);
        return screenshot;
    }

    public void saveBitmapImage(Bitmap sourceBitmap) {
        boolean imageSaved = false;
        if (sourceBitmap != null && !sourceBitmap.isRecycled()) {
            File storagePath = null;
            storagePath = new File(Environment.getExternalStorageDirectory() + "/Temporary");
            if (!storagePath.exists()) {
                storagePath.mkdirs();
            }
            FileOutputStream out = null;
            Random r = new Random();
            int randomNumber = r.nextInt(1000);
            File imageFile = new File(storagePath, "temp_1.png");
            if (imageFile.exists()) {
                boolean isDeleted = imageFile.delete();
                if (isDeleted) {
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                out = new FileOutputStream(imageFile);
                imageSaved = sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
            } finally {
                if (out != null) {
                }
            }

        }

    }
    public void saveBitmapImage1(Bitmap sourceBitmap) {
        boolean imageSaved = false;
        if (sourceBitmap != null && !sourceBitmap.isRecycled()) {
            File storagePath = null;
            storagePath = new File(Environment.getExternalStorageDirectory() + "/Temporary");
            if (!storagePath.exists()) {
                storagePath.mkdirs();
            }
            FileOutputStream out = null;
            Random r = new Random();
            int randomNumber = r.nextInt(1000);
            File imageFile = new File(storagePath, "temp_2.png");
            if (imageFile.exists()) {
                boolean isDeleted = imageFile.delete();
                if (isDeleted) {
                    try {
                        imageFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                out = new FileOutputStream(imageFile);
                imageSaved = sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
            } finally {
                if (out != null) {

                }
            }

        }

    }



    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarHolder.setVisibility(View.VISIBLE);
            anim.start();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... params) {
            mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Matrix m = new Matrix();
                    m.preScale(-1, 1);

                    Bitmap dst = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
                    Bitmap bitmap = getScreenShot();
                    dst=RotateBitmap(dst, 90);
                    saveBitmapImage1(dst);
                    saveBitmapImage(bitmap);
                    Log.d("BITMAP", bmp.getWidth() + "x" + bmp.getHeight());
                    Intent i = new Intent(Vezba.this, Preview.class);

                    startActivity(i);
                }
            });
            return null;
        }
    }
}