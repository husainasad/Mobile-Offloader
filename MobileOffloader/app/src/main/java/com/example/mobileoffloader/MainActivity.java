package com.example.mobileoffloader;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//  References
//  https://www.android.com/
//  https://developer.android.com/guide
//  https://developer.android.com/training/camera/photobasics
public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button openCam;
    private Button nextPage;
    private String currentPhotoPath;
    private static String imageName;
    private static Bitmap imageBitmap;
    private static byte[] byteArray;
    private static String imageb64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imgCam);
        openCam = findViewById(R.id.btnCam);
        nextPage=findViewById(R.id.nxtPgBtn);
        nextPage.setVisibility(View.GONE);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
        }
        nextPage.setOnClickListener(view -> onNextPageClick());
        openCam.setOnClickListener(view -> onTakePictureClick());
    }

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        File f = new File(currentPhotoPath);
                        imageView.setImageURI(Uri.fromFile(f));
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri contentUri = Uri.fromFile(f);
                        mediaScanIntent.setData(contentUri);
                        sendBroadcast(mediaScanIntent);
                        imageName = f.getName();
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentUri);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.PNG, 25, stream);
                            byteArray = stream.toByteArray();
                            imageb64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            nextPage.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                            nextPage.setVisibility(View.GONE);
                        }
                    }
                }
            });


    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File folderDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", folderDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onNextPageClick(){
        Intent intent = new Intent(MainActivity.this, UploadActivity.class);
        startActivity(intent);
    }

    public void onTakePictureClick() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraActivityResultLauncher.launch(takePictureIntent);
        }
    }

    public static String getImageName() {
        return imageName;
    }

    public static String getImageBase(){
        return imageb64;
    }

    public static Bitmap getImageBitmap(){
        return imageBitmap;
    }

    public static byte[] getImageByteArray(){
        return byteArray;
    }

}