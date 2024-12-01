package com.example.mlapp.helpers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mlapp.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageHelperActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_FOR_STORAGE = 101;
    private static final int PERMISSION_REQUEST_CODE_FOR_CAMERA = 102;
    private static final int PICK_IMAGE_REQUEST_CODE = 1000;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 1001;

    private ImageView inputImage;
    private TextView outputText;
    File photoFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image_helper);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputImage = findViewById(R.id.imageView);
        outputText = findViewById(R.id.textView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES)
            == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE_FOR_STORAGE);
            }
            if(checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE_FOR_CAMERA);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(ImageHelperActivity.class.getSimpleName(),
                "grand result for "+permissions[101]+
                " is "+ grantResults[101]);
    }

    public void onStartCamera(View view){
        photoFile = createPhotoFile();
        if (photoFile != null) {
            Uri fileUri = FileProvider.getUriForFile(ImageHelperActivity.this, "com.iago.fileprovider", photoFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
        }else {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show();
        }

    }

    public File createPhotoFile(){
        File photoFileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"ML-IMAGE_HELPER");
        if(!photoFileDir.exists()){
            photoFileDir.mkdir();
        }
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(photoFileDir.getPath() + File.separator + name);
        return file;
    }

    public void onPickImage(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == PICK_IMAGE_REQUEST_CODE){

                try {
                    Uri uri = data.getData();
                    Bitmap bitmap = loadFromUri(uri);
                    inputImage.setImageBitmap(bitmap);
                    runClassification(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                inputImage.setImageBitmap(bitmap);
                runClassification(bitmap);
            }
        }
    }


    public Bitmap loadFromUri(Uri uri) throws IOException {
        Bitmap bitmap = null;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1){
            // new method for loading bitmap
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),uri);
            bitmap = ImageDecoder.decodeBitmap(source);
        }else {
            // old method for loading bitmap
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
        }
        return bitmap;
    }
    protected void runClassification(Bitmap bitmap){

    }
    public TextView getOutputText(){
        return outputText;
    }
    public ImageView getInputImage(){
        return inputImage;
    }
}