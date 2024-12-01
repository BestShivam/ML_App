package com.example.mlapp.images;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mlapp.helpers.ImageHelperActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;

import java.util.List;

public class FlowerIdentificationActivity extends ImageHelperActivity {

    ImageLabeler imageLabeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalModel localModel = new LocalModel.Builder().setAbsoluteFilePath("model_flowers.tflite").build();
        CustomImageLabelerOptions options = new CustomImageLabelerOptions.Builder(localModel)
                .setConfidenceThreshold(0.7f)
                .setMaxResultCount(5)
                .build();
        imageLabeler = ImageLabeling.getClient(options);
    }

    @Override
    protected void runClassification(Bitmap bitmap) {
        super.runClassification(bitmap);
        if (bitmap == null) {
            getOutputText().setText("Invalid image");
            return;
        }
        InputImage inputImage = InputImage.fromBitmap(bitmap,0);
            Log.e("PPPP", "ggggggg "+inputImage);
        imageLabeler.process(inputImage).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
            @Override
            public void onSuccess(List<ImageLabel> imageLabels) {
                if(imageLabels.size() > 0){
                    StringBuilder builder = new StringBuilder();
                    for(ImageLabel label : imageLabels){
                        builder.append(label.getText())
                                .append(" : ")
                                .append(label.getConfidence())
                                .append("\n");
                    }
                    getOutputText().setText(builder.toString());
                }else {
                    getOutputText().setText("Could not classify");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}
