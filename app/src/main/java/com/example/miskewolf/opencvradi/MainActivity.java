package com.example.miskewolf.opencvradi;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.DynamicLayout;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.net.URI;
import java.net.URL;
import java.util.Locale;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class MainActivity extends AbsRunTimePermission /*implements CameraBridgeViewBase.CvCameraViewListener2*/ {
    private static final int REQUEST_PERMISSION=10;
    Button op1,op2,op3,op4;
    private SparseIntArray mErrorString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs1 = getSharedPreferences("LANGUAGE", MODE_PRIVATE);
        int language = prefs1.getInt("lang", 0);

        if(language==0){
            Configuration config = getBaseContext().getResources().getConfiguration();
            String lang = "en";
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }else{
            Configuration config = getBaseContext().getResources().getConfiguration();
            String lang = "sr";
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
        setContentView(R.layout.activity_main);
        StickySwitch stick=(StickySwitch)findViewById(R.id.sticky);
        if(language==0){
            stick.setDirection(StickySwitch.Direction.LEFT);
        }else{
            stick.setDirection(StickySwitch.Direction.RIGHT);
        }
        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=IFdYfId9G4k&t=5s"));
                startActivity(browserIntent);

            }
        });
        backcolor();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean tacnost = prefs.getBoolean("locked", false);

        stick.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(StickySwitch.Direction direction, String s) {
                if(s.equals("ENG")){
                    Configuration config = getBaseContext().getResources().getConfiguration();
                    SharedPreferences.Editor editor = getSharedPreferences("LANGUAGE", MODE_PRIVATE).edit();
                    editor.putInt("lang", 0);
                    editor.apply();
                    String lang = "en";
                    Locale locale = new Locale(lang);
                    Locale.setDefault(locale);
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                    Intent intent= new Intent (MainActivity.this, MainActivity.class);
                    startActivity(intent);

                }else if(s.equals("SRB")){
                    Configuration config = getBaseContext().getResources().getConfiguration();
                    SharedPreferences.Editor editor = getSharedPreferences("LANGUAGE", MODE_PRIVATE).edit();
                    editor.putInt("lang", 1);
                    editor.apply();
                    String lang = "sr";
                    Locale locale = new Locale(lang);
                    Locale.setDefault(locale);
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                    Intent intent= new Intent (MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });
        if (tacnost) {


        }else{
            copyAssets();
            prefs.edit().putBoolean("locked", true).apply();
        }
        mErrorString=new SparseIntArray();
        requestAppPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        R.string.msg,REQUEST_PERMISSION);

        op1=(Button)findViewById(R.id.button2);
        op2=(Button)findViewById(R.id.button3);
        op3=(Button)findViewById(R.id.button4);
        op4=(Button)findViewById(R.id.button5);
        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor((ImageView)findViewById(R.id.ImageView2),(TextView)findViewById(R.id.textView1),getResources().getColor(R.color.MyOrange));
                Intent intent= new Intent (MainActivity.this, AutoDetection.class);
                startActivity(intent);

            }
        });
        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor((ImageView)findViewById(R.id.ImageView3),(TextView)findViewById(R.id.textView2),getResources().getColor(R.color.MyOrange));
                Intent intent= new Intent (MainActivity.this, RemoveDynamic.class);
                startActivity(intent);

            }
        });
        op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor((ImageView)findViewById(R.id.ImageView4),(TextView)findViewById(R.id.textView4),getResources().getColor(R.color.MyOrange));
                Intent intent= new Intent (MainActivity.this, SplashActivity.class);
                startActivity(intent);

            }
        });
        op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeColor((ImageView)findViewById(R.id.ImageView5),(TextView)findViewById(R.id.textView5),getResources().getColor(R.color.MyOrange));
                Intent intent = new Intent(MainActivity.this,Vezba.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onPermissioGranted(int requestCode) {
        Toast.makeText(getApplicationContext(), R.string.perm_granted,Toast.LENGTH_LONG).show();
    }
    private void copyAssets() {
        try {
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
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                } finally {
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
        }catch (Exception e){}
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    private void changeColor(ImageView okvir, TextView tx, int color){
        okvir.setColorFilter(color);
        tx.setTextColor(color);
    }
    @Override
    protected void onStart()
    {
        // TODO Auto-generated method stub
        super.onStart();
        backcolor();
    }


    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        backcolor();
    }
    void backcolor(){
        changeColor((ImageView)findViewById(R.id.ImageView2),(TextView)findViewById(R.id.textView1),Color.WHITE);
        changeColor((ImageView)findViewById(R.id.ImageView3),(TextView)findViewById(R.id.textView2),Color.WHITE);
        changeColor((ImageView)findViewById(R.id.ImageView4),(TextView)findViewById(R.id.textView4),Color.WHITE);
        changeColor((ImageView)findViewById(R.id.ImageView5),(TextView)findViewById(R.id.textView5),Color.WHITE);
    }



}
