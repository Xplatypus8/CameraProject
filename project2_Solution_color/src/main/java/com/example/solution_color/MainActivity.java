package com.example.solution_color;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;


import com.library.bitmap_utilities.BitMap_Helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {

    private int saturation;
    private int percentage;
    private boolean isBW;
    private File file;
    private String imagePath = "";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView background;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setAlpha((float) .7);
        setSupportActionBar(myToolbar);
        background = (ImageView)findViewById(R.id.imageView);



    }

    //private void setSupportActionBar(Toolbar myToolbar) {
    //}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            default:
                break;
        }
        return true;
    }

    public void reset(MenuItem item){


        Camera_Helpers.delSavedImage(imagePath);
        background.setImageResource(R.drawable. gutters );
        background.setScaleType(ImageView.ScaleType.FIT_CENTER);
        background.setScaleType(ImageView.ScaleType.FIT_XY);

    }


    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    public void blackAndWhite(MenuItem item){
        percentage = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("sketchLevel","50"));
        Bitmap bmp = BitMap_Helpers.copyBitmap(background.getDrawable());
        bmp = BitMap_Helpers.thresholdBmp(bmp, percentage);
        Drawable d = new BitmapDrawable(getResources(), bmp);
        background.setImageDrawable(d);
        isBW = true;
    }

    public void colorize(MenuItem item){
        saturation = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("saturation","50"));
        Bitmap tmp = BitMap_Helpers.copyBitmap(background.getDrawable());
        Bitmap color = BitMap_Helpers.colorBmp(tmp, saturation);
        Drawable back = new BitmapDrawable((getResources()), color);
        background.setImageDrawable(back);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                createImageFile();
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                background.setImageBitmap(imageBitmap);
                Camera_Helpers.loadAndScaleImage(imagePath, 100, 100);
                File file = new File(imagePath);
                boolean delete = file.delete();
            }
            catch (Exception e){

            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imagePath = image.getAbsolutePath();
        return image;
    }


    public void share(MenuItem item){
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "temp.jpg");


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        Uri uri = Uri.fromFile(file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share image using"));
        String shareBody = "I made this";
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Picture");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }



}
