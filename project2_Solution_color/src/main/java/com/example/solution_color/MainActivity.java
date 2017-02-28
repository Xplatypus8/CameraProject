package com.example.solution_color;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Android Project 2 By Kyle Hoobler and Taylor Dent
 *
 */
public class MainActivity extends AppCompatActivity{

    //Custom share phrase
    private String phrase;
    private String subject;
    //saturation levels
    private int saturation;
    //Black and white settings
    private int percentage;
    //out file
    private File file;
    private String imagePath = "";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView background;


    /**
     * Starts the application and performs default tasks
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //locks screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Loads XML
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tool bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setAlpha((float) .7);
        setSupportActionBar(myToolbar);
        //Sets background to default image/picture
        background = (ImageView)findViewById(R.id.imageView);

    }


    /**
     * Creates menu
     *
     * @param menu options
     * @return if menu launched
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Checks if options selected from list
     * @param item item in toolbar
     * @return return whether valid operation
     */
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

    /**
     * Resets the background to default state
     * @param item
     */
    public void reset(MenuItem item){

        //supposed to delete image
        Camera_Helpers.delSavedImage(imagePath);
        //sets to default background
        background.setImageResource(R.drawable. gutters );
        //Scales back to normal in case of error
        background.setScaleType(ImageView.ScaleType.FIT_CENTER);
        background.setScaleType(ImageView.ScaleType.FIT_XY);

    }

    /**
     * Launches picture intent
     *
     * @param view
     */
    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    /**
     * Changes the picture to blacka and white, calls helper method bitmap helpers
     * @param item
     */
    public void blackAndWhite(MenuItem item){
        //Gets value from prefernces
        percentage = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("sketchLevel", "50"));
        //Checks if valid percentage
        if(percentage > 100){
            percentage = 100;
        }
        else if(percentage < 0 ){
            percentage = 0;
        }
        //Copies the background
        Bitmap bmp = BitMap_Helpers.copyBitmap(background.getDrawable());
        //Changes to B/W
        bmp = BitMap_Helpers.thresholdBmp(bmp, percentage);
        //Sets background
        Drawable d = new BitmapDrawable(getResources(), bmp);
        background.setImageDrawable(d);
    }

    /**
     * Adds saturation to the image on screen
     * @param item
     */
    public void colorize(MenuItem item){
        //Saturation levels
        saturation = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("saturation","50"));
        //Checks if valid saturation
        if(saturation > 255){
            saturation = 255;
        }
        else if(saturation < 0 ){
            saturation = 0;
        }

        //Copies background
        Bitmap tmp = BitMap_Helpers.copyBitmap(background.getDrawable());
        //adds the threshold
        Bitmap thresh = BitMap_Helpers.thresholdBmp(tmp, saturation);
        Bitmap color = BitMap_Helpers.colorBmp(tmp, saturation);
        BitMap_Helpers.merge(color,thresh);

        //Sets it to background
        Drawable back = new BitmapDrawable((getResources()), color);
        background.setImageDrawable(back);

    }

    /**
     * Result of Camera intent launch
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try{
                createImageFile();
            }
            catch (IOException e){

            }
            //Sets background to new image
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                background.setImageBitmap(imageBitmap);
                Camera_Helpers.loadAndScaleImage(imagePath, 100, 100);

        }
    }

    /**
     * Share menu
     * TODO: Get Image sharing working
     * @param item
     */
    public void share(MenuItem item){

        //Create file path and get the picture(not working)
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), imagePath);
        //Get the Message from prefrences
        phrase = PreferenceManager.getDefaultSharedPreferences(this).getString("shareMessage","I made this.");
        subject = PreferenceManager.getDefaultSharedPreferences(this).getString("shareTitle", "This is the subject of the message you would like to share");
        //Make new intent
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");

        //Get file path
        Uri uri = Uri.fromFile(file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        //Share text/Subject line
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, phrase);
        startActivity(shareIntent);


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
