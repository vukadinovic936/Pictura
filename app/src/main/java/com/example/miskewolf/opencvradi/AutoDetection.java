package com.example.miskewolf.opencvradi;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;

import org.opencv.android.Utils;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Random;

import steelkiwi.com.library.DotsLoaderView;
/**
 * Created by MiskeWolf
 */
public class AutoDetection extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private static final int PICK_IMAGE = 100;
    private static final int TAKE_IMAGE = 200;
    int idNum = 0;
    RadioButton detect;
    RadioButton zombie;
    Button choose;
    AsyncTask<String, String, String> dAsync;
    RadioButton anon;
    RadioButton Avatar;
    RadioButton strangeMask;
    BitmapDrawable savesave;
    ImageView img;
    Button photo;
    Button remBack;
    ContentValues values;
    Uri imageUri;
    int rot;
    BitmapDrawable d = null;
    Bitmap thumbnail;
    Bitmap draw;
    BitmapDrawable start;
    private Toolbar mToolbar;
    Bitmap original;
    ImageView imgV;

    AnimationDrawable anim;
    FrameLayout progressBarHolder;


    static {
        System.loadLibrary("MyFaceLibs");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_detection);
        mToolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(mToolbar); // Sets the Toolbar to act as the ActionBar for this Activity window
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //TODO MAKE FIRST AND SEC
        imgV=(ImageView)findViewById(R.id.animat);
        imgV.setBackgroundResource(R.drawable.anim_loading);
        progressBarHolder=(FrameLayout) findViewById(R.id.progressBarHolder);
        anim=(AnimationDrawable)imgV.getBackground();
        anon = (RadioButton) findViewById(R.id.Anonymous);
        //  remBack=(Button)findViewById(R.id.background);
        Avatar = (RadioButton) findViewById(R.id.Avatar);
        zombie = (RadioButton) findViewById(R.id.ZombieFace);
        strangeMask = (RadioButton) findViewById(R.id.StrangeMask);
        Toast.makeText(this, R.string.slide_left_to_right, Toast.LENGTH_LONG).show();
        photo = (Button) findViewById(R.id.photo);
        img = (ImageView) findViewById(R.id.imageView2);
        start = (BitmapDrawable) img.getDrawable();
        detect = (RadioButton) findViewById(R.id.detect);
        choose = (Button) findViewById(R.id.chose);

        /*remBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mat mat = new Mat();
                BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap = RotateBitmap(bitmap, rot);
                Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Utils.bitmapToMat(bmp32, mat);

                OpencvNativeClass.RemoveBackground(mat.getNativeObjAddr());
                Bitmap bm = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, bm);
                d = new BitmapDrawable(getResources(), bm);
                img.setImageDrawable(d);
                img.setRotation(0);
            }
        });*/
        strangeMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idNum = 1;
                Close();

                Log.d("IDNUM","id"+idNum);

            }
        });
        zombie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idNum = 2;
                Close();

            }
        });
        anon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idNum = 3;
                Close();


            }
        });
        Avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idNum = 4;
                Close();



            }
        });
        findViewById(R.id.fire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idNum = 5;
                Close();


            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idNum = 6;
                Close();

            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
        findViewById(R.id.put).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                if(idNum==0)
                    Open();
                else
                new LoadAnim().execute();
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                save(bitmap);
            }
        });


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_IMAGE);
            }
        });


    }

    long CollFun(int id) {
        Mat pic1 = new Mat();
        draw = BitmapFactory.decodeResource(this.getResources(), id);
        Bitmap bmp32 = draw.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, pic1);
        return pic1.getNativeObjAddr();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            img.setImageURI(imageUri);

            rot = getCameraPhotoOrientation(AutoDetection.this, imageUri, filePath);
            Log.d("rottt", " " + rot);
            img.setRotation(getCameraPhotoOrientation(AutoDetection.this, imageUri, filePath));
            BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            bitmap = RotateBitmap(bitmap, rot);
            original = bitmap;

        } else if (resultCode == RESULT_OK && requestCode == TAKE_IMAGE) {
            if (requestCode == TAKE_IMAGE)
                if (resultCode == Activity.RESULT_OK) {

                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);

                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        img.setImageBitmap(thumbnail);

                        rot = getCameraPhotoOrientation(AutoDetection.this, imageUri, filePath);
                        img.setRotation(getCameraPhotoOrientation(AutoDetection.this, imageUri, filePath));

                    } catch (Exception e) {
                        e.printStackTrace();


                    }

                }
        }
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;

        try {
            Log.d("radiii", "s " + imagePath);
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

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private void OpenGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private BitmapDrawable Filteri(int id, float alfa, float beta) {


        if (original != null) {
            if (!img.getDrawable().equals(d)) {
                Mat mat = new Mat();
                BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap = RotateBitmap(bitmap, rot);
                Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Utils.bitmapToMat(bmp32, mat);
                OpencvNativeClass.StrangeMask(mat.getNativeObjAddr(), CollFun(id), alfa, beta);
                Bitmap bm = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, bm);
                d = new BitmapDrawable(getResources(), bm);
                return d;

            }
        }
        return start;
    }

    void save(Bitmap bitmap) {
        Random r = new Random();
        int randomNumber = r.nextInt(1000);
        MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                String.format("%s_%d.png", "Pictura++", randomNumber),
                "Pictura++"

        );

        Toast.makeText(getApplicationContext(), "PICTURE SAVED!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    public void Close() {
        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawers();
    }

    public void Open() {
        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.openDrawer(Gravity.START);
    }

    void clear() {
        img.setImageBitmap(original);
        img.setRotation(0);
        rot=0;
        d = null;
    }

    private class LoadAnim extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarHolder.setVisibility(View.VISIBLE);
            anim.start();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
                img.setImageDrawable(savesave);
                img.setRotation(0);
                progressBarHolder.setVisibility(View.GONE);
                anim.stop();
        }

        @Override
        protected Void doInBackground(Void... params) {
           if (idNum == 1) {
                savesave=Filteri(R.drawable.mask, 1.0f, 1.0f);
            } else if (idNum == 2) {
                savesave=Filteri(R.drawable.zombie, 1.0f, 0.5f);
            } else if (idNum == 3) {
                savesave=Filteri(R.drawable.anonymous, 1.0f, 1.0f);
            } else if (idNum == 4) {
                savesave=Filteri(R.drawable.avatar, 1.0f, 1.0f);
            } else if (idNum == 5) {
                savesave=Filteri(R.drawable.fire, 1.0f, 1.0f);
            } else if (idNum == 6) {

                if (original != null) {


                    if (!img.getDrawable().equals(d)) {

                        Mat mat = new Mat();
                        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        original = bitmap;
                        bitmap = RotateBitmap(bitmap, rot);
                        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Utils.bitmapToMat(bmp32, mat);

                        OpencvNativeClass.faceDetection(mat.getNativeObjAddr());
                        Bitmap bm = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(mat, bm);
                        d = new BitmapDrawable(getResources(), bm);
                        savesave=d;

                    }
                }
            }
            return null;
        }
    }
}
