package com.example.solution_color;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class MainActivity extends AppCompatActivity  {

    private int numPictures;
    private String imagePath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView background;

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

    private void reset(ImageView myImage){
        Camera_Helpers.delSavedImage(imagePath);
        myImage.setImageResource(R.drawable. gutters );
        myImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        myImage.setScaleType(ImageView.ScaleType.FIT_XY);

    }

    private File createPictureFile() throws IOException{
        String fileName = "Pic" + numPictures;
        numPictures++;
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File picture = File.createTempFile(fileName, ".jpg", directory);

        imagePath = picture.getAbsolutePath();
        return picture;
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data){



    }



}

