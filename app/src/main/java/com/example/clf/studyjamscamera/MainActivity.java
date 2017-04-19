package com.example.clf.studyjamscamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private CameraView myCameraView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_back:
                    myCameraView.setFacing(CameraKit.Constants.FACING_BACK);
                    return true;
                case R.id.navigation_front:
                    myCameraView.setFacing(CameraKit.Constants.FACING_FRONT);
                    return true;
                case R.id.navigation_send:
                    myCameraView.captureImage();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        myCameraView = (CameraView) findViewById(R.id.camera);
        myCameraView.setJpegQuality(50);

        myCameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);
                // Create a bitmap
                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                result.compress(Bitmap.CompressFormat.PNG, 0, stream);
                final byte[] bitmapdata = stream.toByteArray();

                try {
                    uploadImage(bitmapdata,"image");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void uploadImage(byte[] image, String imageName) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("img", imageName, RequestBody.create(MediaType.parse("image/png"), image))
                .build();

        Request request = new Request.Builder().url("http://demo.clfstudio.com/upload")
                .post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("NET","WORK");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCameraView.start();
    }

    @Override
    protected void onPause() {
        myCameraView.stop();
        super.onPause();
    }

}
