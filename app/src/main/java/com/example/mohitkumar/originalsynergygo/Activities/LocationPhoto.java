package com.example.mohitkumar.originalsynergygo.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mohitkumar.originalsynergygo.Adapters.MySingleton;
import com.example.mohitkumar.originalsynergygo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class LocationPhoto extends AppCompatActivity {


    protected LocationManager locationManager;
    private double latitude = 0;
    private double longitude = 0;
    TextView lat, lng, textView;
    Button refresh;
    String refno,address,applcoappl;
    FloatingActionButton photo;
    ProgressDialog dialog;
    private Bitmap bitmap;
    private Uri filepath;
    private Uri fileUri;
    ProgressDialog progressDialog;
    Button button;
    File destination;
    private int PICK_IMAGE_REQUEST = 1;

    public static final String UPLOAD_URL = "http://aztek.in/repignite/android/imageupload.php";
    public static final String UPLOAD_KEY = "image";
    public static final String TAG = "LOCATIONCLASS";

    public static final int LOCATION_REQ_CODE = 100;
    public static final int EXTERNAL_STORAGE_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_photo);


       // ProgressDialog progressDialog = new ProgressDialog(LocationPhoto.this);

        Log.d("TAG 1 ", "onCreate: inside oncreate");

        button = (Button) findViewById(R.id.exit);
        refno = getIntent().getStringExtra("REFNO");
        address = getIntent().getStringExtra("ADDRESS");
        applcoappl = getIntent().getStringExtra("APPL");


        progressDialog = new ProgressDialog(LocationPhoto.this);

    }


    public void exitfunc(View view) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,"http://aztek.in/repignite/android/updateallocations.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

//                progressDialog.show();
                Map<String,String> params = new HashMap<String, String>();

                params.put("REFNO",refno);
                params.put("TYPE",address);
                params.put("APPLORCO",applcoappl);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


        Intent intent = new Intent(LocationPhoto.this,AssignmentChoose.class);
        intent.putExtra("Agent",AssignmentChoose.AgentID);
        startActivity(intent);
    }

    public void onc(View view) {
        button.setVisibility(View.VISIBLE);
        destination = new   File(Environment.getExternalStorageDirectory(),"image.jpg");
        Intent cam_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      //  fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        cam_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        Log.d(TAG, "onc: inside ONC");
        startActivityForResult(cam_intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: inside the pickimagerequest");
            String realImageStr = new String("");
            try {
                FileInputStream in = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10; //Downsample 10x
                Log.d("PP", " bitmap factory=========="+options);
                Bitmap user_picture_bmp = BitmapFactory.decodeStream(in, null, options);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                user_picture_bmp.compress(Bitmap.CompressFormat.JPEG, 20, bos);
                byte[] bArray = bos.toByteArray();
                String encodedImage = Base64.encodeToString(bArray, Base64.DEFAULT);
                uploadImage(encodedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadImage(final String image){
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();
        Log.d("Entered","ENTER");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

//                progressDialog.show();
                Map<String,String> params = new HashMap<String, String>();

                Log.d("NAME",address + "_" + applcoappl);
                params.put("REFNO",refno);
                params.put("NAME",address + "_" + applcoappl);
                params.put("IMAGE",image);

                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        progressDialog.dismiss();
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Log.d(TAG,"Compressing");
    //    progressDialog.show();
        bmp.compress(Bitmap.CompressFormat.JPEG,20, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        Log.d(TAG,encodedImage);
        return encodedImage;
    }

    public static class Utility {
        public static boolean checkPermissions(Context context, String... permissions) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_CODE);
                        } else {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_CODE);
                        }
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.DEBUG + "directory");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.DEBUG + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
}
