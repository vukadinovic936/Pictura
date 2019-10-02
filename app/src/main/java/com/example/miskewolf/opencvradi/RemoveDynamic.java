package com.example.miskewolf.opencvradi;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import steelkiwi.com.library.DotsLoaderView;
// Asked this question on StackOverflow and with a help of users suceeded in resolving it https://stackoverflow.com/questions/47937180/while-finding-a-difference-between-2-pictures-opencv-difference-is-bigger-than-i
public class RemoveDynamic extends AppCompatActivity {
    private static String TAG = "MainActivity";
    private static final int PICK_IMAGE=100;
    private static final int TAKE_IMAGE=200;
    Button choose;
    Button dif;
    ImageView img;
    Button photo;
    RadioButton merge;
    RadioButton ShowDiff;
    RadioButton CutDiff;
    long[] tempobjadr;
    ContentValues values;
    ImageView imgV;

    AnimationDrawable anim;
    FrameLayout progressBarHolder;
    Uri imageUri;
    Mat pic1;
    int IdNum=0;
    int rot;
    int br=1;
    DotsLoaderView dotsLoader;
    Toolbar mToolbar;
    BitmapDrawable d=null;
    Bitmap thumbnail;
    ArrayList<Mat> slike = new ArrayList<Mat>();
    static {
        System.loadLibrary("MyFaceLibs");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_dynamic);
        /*copyAssets();*/
        imgV=(ImageView)findViewById(R.id.animat);
        imgV.setBackgroundResource(R.drawable.anim_loading);
        progressBarHolder=(FrameLayout) findViewById(R.id.progressBarHolder);
        anim=(AnimationDrawable)imgV.getBackground();
        dotsLoader=(DotsLoaderView)findViewById(R.id.loader);
        mToolbar = (Toolbar) findViewById(R.id.toolbar1);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        CutDiff=(RadioButton)findViewById(R.id.CutDiff);
        ShowDiff=(RadioButton)findViewById(R.id.ShowDiff);
        merge=(RadioButton)findViewById(R.id.MergeAll);

        photo=(Button)findViewById(R.id.photo);
        img=(ImageView)findViewById(R.id.imageView2);
        choose=(Button)findViewById(R.id.chose);
        dif=(Button)findViewById(R.id.dif);
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                save(bitmap);
            }
        });
        findViewById(R.id.put).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadingAnim().execute();
            }
        });
        merge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IdNum=1;
            }
        });
        CutDiff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               IdNum=2;
            }
        });
        ShowDiff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IdNum=3;
            }
        });

        dif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pic1 = new Mat();

                BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap = RotateBitmap(bitmap, rot);
                Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Utils.bitmapToMat(bmp32, pic1);
                slike.add(pic1);
                Button btn = (Button) findViewById(R.id.put);
                btn.setText(getString(R.string.do_actionn)+slike.size()+getString(R.string.images));
                Toast.makeText(RemoveDynamic.this,getString(R.string.element_added)+slike.size(), Toast.LENGTH_LONG).show();
                int elems=  slike.size();
                tempobjadr = new long[elems];
                for (int i=0;i<elems;i++)
                {
                    Mat tempaddr=slike.get(i);
                    tempobjadr[i]=  tempaddr.getNativeObjAddr();
                }


                br++;

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
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==PICK_IMAGE){
            imageUri= data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            img.setImageURI(imageUri);
            rot=getCameraPhotoOrientation(RemoveDynamic.this,imageUri,filePath);
            img.setRotation(getCameraPhotoOrientation(RemoveDynamic .this,imageUri,filePath));




        }else if(resultCode==RESULT_OK && requestCode==TAKE_IMAGE){
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

                        rot=getCameraPhotoOrientation(RemoveDynamic.this,imageUri,filePath);
                        img.setRotation(getCameraPhotoOrientation(RemoveDynamic.this,imageUri,filePath));

                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }
        }
    }
    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
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

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    private void OpenGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
    void addInList(ArrayList<Mat> mat,Mat m){
        mat.add(m);
    }
    void save(Bitmap bitmap){
        Random r = new Random();
        int randomNumber = r.nextInt(1000);
        MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                String.format("%s_%d.png", "Pictura++", randomNumber),
                "Pictura++"

        );

        Toast.makeText(getApplicationContext(),getString(R.string.img_saved),Toast.LENGTH_LONG).show();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
    public void Close(){
        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawers();
}
    public void Open(){
        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.openDrawer(Gravity.START);
    }
    private class LoadingAnim extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarHolder.setVisibility(View.VISIBLE);
            anim.start();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            img.setImageDrawable(d);
            img.setRotation(0);
            progressBarHolder.setVisibility(View.GONE);
            anim.stop();
        }
        @Override
        protected Void doInBackground(Void... params) {
            switch (IdNum){
                case 0:
                    Open();
                    break;
                case 1:
                    if(slike.size()>2) {
                        try {
                            //TODO UCITAVANJE
                            OpencvNativeClass.detectHuman(tempobjadr);
                            //TODO KRAJ UCITAVANJA
                            Bitmap bm = Bitmap.createBitmap(slike.get(0).cols(), slike.get(0).rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(slike.get(0), bm);
                            d = new BitmapDrawable(getResources(), bm);


                        } catch (Exception e) {
                            Toast.makeText ( RemoveDynamic.this ,"Can't transform, sorry" ,Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case 2:
                    if(slike.size()>1) {
                        try {
                            //TODO UCITAVANJE
                            OpencvNativeClass.DeleteDiffInverse(tempobjadr);
                            //TODO KRAJ UCITAVANJA
                            Bitmap bm = Bitmap.createBitmap(slike.get(0).cols(), slike.get(0).rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(slike.get(0), bm);
                            d = new BitmapDrawable(getResources(), bm);

                        } catch (Exception e) {
                            Toast.makeText ( RemoveDynamic.this ,"Can't transform, sorry" ,Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                case 3:
                    if(slike.size()>1) {
                        try {
                            //TODO UCITAVANJE
                            OpencvNativeClass.DeleteDiff(tempobjadr);
                            //TODO KRAJ UCITAVANJA
                            Bitmap bm = Bitmap.createBitmap(slike.get(0).cols(), slike.get(0).rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(slike.get(0), bm);
                            d = new BitmapDrawable(getResources(), bm);

                        } catch (Exception e) {
                            Toast.makeText ( RemoveDynamic.this ,"Can't transform, sorry" ,Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                case 4:

                    break;
                default:
                    Open();

            }
            return null;
        }

    }

}
