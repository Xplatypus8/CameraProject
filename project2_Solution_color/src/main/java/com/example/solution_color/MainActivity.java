package com.example.solution_color;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.library.bitmap_utilities.BitMap_Helpers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {

    private int percentage;
    private String imagePath = "";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView background;
    private static final int TAKE_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setAlpha((float).7);
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
        percentage = 50;
        Bitmap bmp = BitMap_Helpers.copyBitmap(background.getDrawable());
        bmp = BitMap_Helpers.thresholdBmp(bmp, percentage);
        Drawable d = new BitmapDrawable(getResources(), bmp);
        background.setImageDrawable(d);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                createImageFile();
                Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show();
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

}
