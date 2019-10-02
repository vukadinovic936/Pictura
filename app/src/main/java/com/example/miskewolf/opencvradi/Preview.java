package com.example.miskewolf.opencvradi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class Preview extends AppCompatActivity {
    ImageView cameraImage,frame;
    Button goback,save;
    Bitmap back,front;
    RelativeLayout up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        cameraImage= (ImageView) findViewById(R.id.cameraImage);
        frame= (ImageView) findViewById(R.id.frame);
        goback= (Button) findViewById(R.id.back1);
        save= (Button) findViewById(R.id.save1);
        up= (RelativeLayout) findViewById(R.id.up);
        Bitmap back = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +"/Temporary/temp_1.png");
        Bitmap front = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +"/Temporary/temp_2.png");

        cameraImage.setImageBitmap(back);
        frame.setImageBitmap(front);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap image=getScreenShot();
                save(image);
                File dir = new File(Environment.getExternalStorageDirectory() +"/Temporary");

                if (dir.isDirectory())
                {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++)
                    {
                        new File(dir, children[i]).delete();
                    }
                }

                Toast.makeText(Preview.this, R.string.img_saved, Toast.LENGTH_SHORT).show();

            }
        });
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public Bitmap getScreenShot( ) {
        Bitmap bitmap;
        View view = findViewById(R.id.up);// get ur root view id
        view.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        Bitmap screenshot = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenshot);
        view.draw(canvas);
        return screenshot;
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
        Toast.makeText(getApplicationContext(),R.string.img_saved,Toast.LENGTH_LONG).show();
    }
}
