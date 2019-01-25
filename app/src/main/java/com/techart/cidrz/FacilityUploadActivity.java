package com.techart.cidrz;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techart.cidrz.constants.Constants;
import com.techart.cidrz.constants.FireBaseUtils;
import com.techart.cidrz.utils.EditorUtils;
import com.techart.cidrz.utils.ImageUtils;
import com.techart.cidrz.utils.UploadUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static com.techart.cidrz.utils.ImageUtils.hasPermissions;

/**
 * Created by Kelvin on 30/07/2017.
 * Handles actions related to asking facilityCode
 */

public class FacilityUploadActivity extends AppCompatActivity {
    //string resources
    private String facilityCode;
    private String facilityName;
    private String realPath;

    Uri uriFromPath;


    //ui components
    private ImageView ivFacilityPicture;
    private EditText etFacilityCode;
    private EditText etFacilityName;
    StorageReference filePath;

    //image
    private static final int GALLERY_REQUEST = 1;
    private static final int CAPTURE_REQUEST = 2;
    private Uri uri;

    //Permission
    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilty);
        etFacilityCode = findViewById(R.id.et_facility_code);
        etFacilityName = findViewById(R.id.et_facillity_name);

        ivFacilityPicture = findViewById(R.id.ib_item);

        ivFacilityPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectIntentAction();
            }
        });
    }

    /**
     * requests for permission in android >= 23
     */
    @TargetApi(23)
    private void onGetPermission() {
        // only for MarshMallow and newer versions
        if(!hasPermissions(this, PERMISSIONS)){
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                onPermissionDenied();
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST);
        }
    }

    /**
     * Trigger gallery selection for a photo
     * @param requestCode
     * @param permissions permissions to be requested
     * @param grantResults granted results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST);
        } else {
            //do something like displaying a message that he did not allow the app to access gallery and you wont be able to let him select from gallery
            onPermissionDenied();
        }
    }

    /**
     * Displays when permission is denied
     */
    private void onPermissionDenied() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            ActivityCompat.requestPermissions(FacilityUploadActivity.this, PERMISSIONS, PERMISSION_ALL);
                        }
                        if (button == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.dismiss();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("YOU NEED TO ALLOW ACCESS TO MEDIA STORAGE")
                .setMessage("Without this permission you can not upload an image")
                .setPositiveButton("ALLOW", dialogClickListener)
                .setNegativeButton("DENY", dialogClickListener)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu menu resource to be inflated
     * @return true if successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_post:
                if (validate()){
                    upload();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sends information to database
     * @param downloadImageUrl url of upload image
     */
    private void sendPost(String downloadImageUrl, String url) {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.FACILITY_MODE, facilityCode.toUpperCase());
        values.put(Constants.FACILITY_NAME, facilityName);
        values.put(Constants.IMAGE_URL,downloadImageUrl);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        FireBaseUtils.mDatabaseFacility.child(url).setValue(values)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(FacilityUploadActivity.this, "Item Posted",LENGTH_LONG).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FacilityUploadActivity.this, "ERROR" + e.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    /**
     * Uploads image to cloud storage
     */
    private void upload() {
        final String url = FireBaseUtils.mDatabaseFacility.push().getKey();
        final ProgressDialog mProgress = new ProgressDialog(FacilityUploadActivity.this);
        mProgress.setMessage("Uploading photo, please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        filePath = FireBaseUtils.mStorageReports.child("Facilities" + "/"  +  url);
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        //uploading the image
        UploadTask uploadTask = filePath.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    sendPost(task.getResult().toString(),url);
                    mProgress.dismiss();
                    finish();

                } else {
                    // Handle failures
                    UploadUtils.makeNotification("Image upload failed",FacilityUploadActivity.this);
                }
            }
        });

    }

    /**
     * validates the entries before submission
     * @return true if successful
     */
    private boolean validate(){
        facilityCode = etFacilityCode.getText().toString().trim();
        facilityName = etFacilityName.getText().toString().trim();
        return  EditorUtils.editTextValidator(facilityCode, etFacilityCode,"Enter facility code") &&
                EditorUtils.editTextValidator(facilityName, etFacilityName,"Enter facility name") &&
                EditorUtils.imagePathValdator(this,realPath);
    }

    private  void selectIntentAction() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_rate_app);
        dialog.setCanceledOnTouchOutside(false);
        TextView tv = dialog.findViewById(R.id.tv_camera);
        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takePhoto();
                dialog.dismiss();
            }
        });

        TextView b1 = dialog.findViewById(R.id.tv_gallery);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadImage();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void uploadImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onGetPermission();
        }  else {
            Intent imageIntent = new Intent();
            imageIntent.setType("image/*");
            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(imageIntent,GALLERY_REQUEST);
        }
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_REQUEST);
    }


    /**
     * Called upon selecting an image
     * @param requestCode
     * @param resultCode was operation successful or not
     * @param data data returned from the operation
     */
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK && data != null && data.getData() != null ) {
                uri = data.getData();
                realPath = ImageUtils.getRealPathFromUrl(this, uri);
                uriFromPath = Uri.fromFile(new File(realPath));
                setImage(ivFacilityPicture,uriFromPath);
            }
    }

    /**
     * inflates image into the image view
     * @param image component into which image will be inflated
     * @param uriFromPath uri of image to be inflated
     */
    private void setImage(ImageView image, Uri uriFromPath) {
        Glide.with(this)
                .load(uriFromPath)
              //  .centerCrop()
                .into(image);
    }

}