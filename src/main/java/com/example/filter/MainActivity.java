package com.example.filter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.FileDescriptor;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private ImageView imageView;
    private Bitmap image;
    int height;
    int width;
    int[][] originalMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
    }

    public void choosePhoto(View v){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }


    public Bitmap getBitmapFromView(View v){
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    public void savePhoto(View v){
        Bitmap bitmapToSave = getBitmapFromView(imageView);
        MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmapToSave, "saved", "savedImage");
    }

    public int[][] getColorMatrix(Bitmap bitmap){
        int[][] colors = new int[height][width];

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int color = image.getPixel(j, i);
                colors[i][j] = color;
            }
        }
        return colors;
    }

    public void applyGreyscale(View v){
        if (image == null){
            return;
        }

        Bitmap greyImage = image.copy(image.getConfig(), true);

        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int Sat = (originalMatrix[i][j] >> 24) & 0xff;
                int Red = (originalMatrix[i][j] >> 16) & 0xff;
                int Green = (originalMatrix[i][j] >> 8) & 0xff;
                int Blue = (originalMatrix[i][j] ) & 0xff;

                int avg = (Red+Blue+Green)/3;

                int greyPixel = Color.argb(Sat, avg, avg, avg);
                greyImage.setPixel(j, i, greyPixel);
            }
        }
        imageView.setImageBitmap(greyImage);
    }

    

    public void applySepia(View v){
        if (image == null){
            return;
        }

        Bitmap SepiaImage = image.copy(image.getConfig(), true);

        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int Sat = (originalMatrix[i][j] >> 24) & 0xff;
                int Red = (originalMatrix[i][j] >> 16) & 0xff;
                int Green = (originalMatrix[i][j] >> 8) & 0xff;
                int Blue = (originalMatrix[i][j] ) & 0xff;

                int sepiaRed = (int) Math.min(255, Math.round(0.393*Red+0.769*Green+0.189*Blue));
                int sepiaGreen = (int) Math.min(255, Math.round(0.349*Red+0.686*Green+0.168*Blue));
                int sepiaBlue = (int) Math.min(255, Math.round(0.272*Red+0.534*Green+0.131*Blue));

                int sepiaPixel = Color.argb(Sat, sepiaRed, sepiaGreen, sepiaBlue);
                SepiaImage.setPixel(j, i, sepiaPixel);
            }
        }
        imageView.setImageBitmap(SepiaImage);
    }

    public void applyRetro(View v){
        if (image == null){
            return;
        }

        Bitmap RetroImage = image.copy(image.getConfig(), true);

        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int Sat = (originalMatrix[i][j] >> 24) & 0xff;
                int Red = (originalMatrix[i][j] >> 16) & 0xff;
                int Green = (originalMatrix[i][j] >> 8) & 0xff;
                int Blue = (originalMatrix[i][j] ) & 0xff;


                int sepiaRed = (int) Math.min(255, Math.round(0.272*Red+0.534*Green+0.131*Green));

                int sepiaPixel = Color.argb(Sat, sepiaRed, Green, Blue);
                RetroImage.setPixel(j, i, sepiaPixel);
            }
        }
        imageView.setImageBitmap(RetroImage);
    }

    public void applyReverse(View v){
        if (image == null){
            return;
        }

        Bitmap ReverseImage = image.copy(image.getConfig(), true);

        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int reversePixel = originalMatrix[i][width-1-j];
                ReverseImage.setPixel(j, i, reversePixel);
            }
        }
        imageView.setImageBitmap(ReverseImage);
    }

    public boolean validIndex(int x, int y, int w, int h){
        return x >=0 && x < w && y >= 0 && y < h;
    }

    public void applyWhitenoise(View v){
        if (image == null){
            return;
        }

        Bitmap WNImage = image.copy(image.getConfig(), true);

        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int avg = 0;
                int counter = 0;

                for(int k = 0; k <= 2; k++){
                    for(int l = 0; l <= 2; l++){
                        if (validIndex(j+l-1, k+i-1, width, height)){
                            avg += originalMatrix[i+k-1][j+l-1];
                            counter += 1;
                        }
                    }
                }
                int WNPixel = avg/counter;

                WNImage.setPixel(j, i, WNPixel);
            }
        }
        imageView.setImageBitmap(WNImage);
    }

    public void applyBlur(View v){
        if (image == null){
            return;
        }

        Bitmap BlurImage = image.copy(image.getConfig(), true);

        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int avgRed = 0, avgBlue = 0, avgGreen = 0, avgSat = 0;
                int counter = 0;

                for(int k = 0; k <= 4; k++){
                    for(int l = 0; l <= 4; l++){
                        if (validIndex(j+l-1, k+i-1, width, height)){
                            avgSat += (originalMatrix[i+k-1][j+l-1] >> 24) & 0xff;
                            avgRed += (originalMatrix[i+k-1][j+l-1] >> 16) & 0xff;
                            avgGreen += (originalMatrix[i+k-1][j+l-1] >> 8) & 0xff;
                            avgBlue += (originalMatrix[i+k-1][j+l-1] ) & 0xff;
                            counter += 1;
                        }
                    }
                }

                int BlurPixel = Color.argb(avgSat/counter, avgRed/counter, avgGreen/counter, avgBlue/counter);

                BlurImage.setPixel(j, i, BlurPixel);
            }
        }
        imageView.setImageBitmap(BlurImage);
    }

    public void applyEdge(View v){
        if (image == null){
            return;
        }

        Bitmap EdgeImage = image.copy(image.getConfig(), true);
        int[][] Gx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] Gy = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){

                int xWeightSumR = 0, xWeightSumG = 0, xWeightSumB = 0;
                int yWeightSumR = 0, yWeightSumG = 0, yWeightSumB = 0;

                for(int k = 0; k <= 2; k++){
                    for(int l = 0; l <= 2; l++){
                        if (validIndex(j+l-1, k+i-1, width, height)){
                            xWeightSumR += ((originalMatrix[i+k-1][j+l-1] >> 16) & 0xff)*Gx[k][l];
                            xWeightSumG += ((originalMatrix[i+k-1][j+l-1] >> 8) & 0xff)*Gx[k][l];
                            xWeightSumB += ((originalMatrix[i+k-1][j+l-1] ) & 0xff)*Gx[k][l];
                            yWeightSumR += ((originalMatrix[i+k-1][j+l-1] >> 16) & 0xff)*Gy[k][l];
                            yWeightSumG += ((originalMatrix[i+k-1][j+l-1] >> 8) & 0xff)*Gy[k][l];
                            yWeightSumB += ((originalMatrix[i+k-1][j+l-1] ) & 0xff)*Gy[k][l];
                        }
                    }
                }

                int redValue = (int) Math.min(255, Math.round(Math.sqrt(xWeightSumR*xWeightSumR+yWeightSumR*yWeightSumR)));
                int GreenValue = (int) Math.min(255, Math.round(Math.sqrt(xWeightSumG*xWeightSumG+yWeightSumG*yWeightSumG)));
                int BlueValue = (int) Math.min(255, Math.round(Math.sqrt(xWeightSumB*xWeightSumB+yWeightSumB*yWeightSumB)));

                int BlurPixel = Color.rgb(redValue, GreenValue, BlueValue);

                EdgeImage.setPixel(j, i, BlurPixel);
            }
        }
        imageView.setImageBitmap(EdgeImage);
    }

    public void applyFaded(View v){
        if (image == null){
            return;
        }

        Bitmap FadedImage = image.copy(image.getConfig(), true);

        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                int Sat = (originalMatrix[i][j] >> 24) & 0xff;
                int Red = (originalMatrix[i][j] >> 16) & 0xff;
                int Green = (originalMatrix[i][j] >> 8) & 0xff;
                int Blue = (originalMatrix[i][j] ) & 0xff;

                int avg = (Red+Blue+Green)/3;
                float multiplier = (((float) i/height)+((float) j/width))/2;

                int fadedRed = (int) Math.min(255, ((1-multiplier)*Red + multiplier*avg));
                int fadedGreen = (int) Math.min(255, ((1-multiplier)*Green + multiplier*avg));
                int fadedBlue = (int) Math.min(255, ((1-multiplier)*Blue + multiplier*avg));


                int fadePixel = Color.argb(Sat, fadedRed, fadedGreen, fadedBlue);
                FadedImage.setPixel(j, i, fadePixel);
            }
        }
        imageView.setImageBitmap(FadedImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null){
            try{
                Uri uri = data.getData();
                ParcelFileDescriptor parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                imageView.setImageBitmap(image);

                height = image.getHeight();
                width = image.getWidth();
                originalMatrix = getColorMatrix(image);
            }
            catch (IOException e){
                Log.e("cs50", "Image not found", e);
            }
        }
    }
}