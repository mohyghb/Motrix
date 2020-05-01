package com.mohyaghoub.calculator;


import android.content.Intent;

import android.graphics.Bitmap;

import android.graphics.Matrix;

import android.net.Uri;
import android.provider.MediaStore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.SparseArray;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.vision.Frame;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.IOException;


public class CameraActivity extends AppCompatActivity {


    private final int GALLARY_CODE = 201;



    private String result;
    private Uri uri;
    private Button Gallery,Gallery2,readText;
    private Bitmap mBitmap;
    private CropImageView cropImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initObjects();
    }

    public void initObjects()
    {
        this.Gallery = findViewById(R.id.openGallery);
        this.Gallery2 = findViewById(R.id.openGalleryTwo);
        this.readText = findViewById(R.id.OCR);
        this.cropImageView = findViewById(R.id.cropImageView);


        this.Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        this.Gallery2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        this.readText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextFromImage(cropImageView.getCroppedImage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rotateimageview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.rotate)
        {
            cropImageView.rotateImage(90);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openGallery()
    {
        Intent GalIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent,"Select Image from Gallery"),GALLARY_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLARY_CODE)
        {
            if(data != null)
            {
                uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    mBitmap = bitmap;
                    this.Gallery.setVisibility(View.INVISIBLE);
                    this.Gallery2.setVisibility(View.VISIBLE);
                    this.readText.setVisibility(View.VISIBLE);
                    cropImageView.setImageUriAsync(uri);
                    cropImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public void getTextFromImage(Bitmap bitmap)
    {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational())
        {
            Toast.makeText(this,"Could not get the text, sorry",Toast.LENGTH_LONG).show();
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(frame);

            StringBuilder sb = new StringBuilder();

            for(int i = 0;i<items.size();i++)
            {
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append(" ");
            }

            result = sb.toString();

            Intent data = new Intent();
            data.putExtra("data",result);
            data.setData(Uri.parse(result));
            setResult(RESULT_OK,data);
            finish();
        }

    }





}
