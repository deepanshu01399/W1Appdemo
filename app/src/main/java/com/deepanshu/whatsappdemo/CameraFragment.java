package com.deepanshu.whatsappdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {
    private String mCM;
    private ValueCallback<Uri> mUploadMsg;
    private ValueCallback<Uri[]> mUploadMsgArray;
    //private final static int FCR = 1;
    private final static int FILECHOOSER_RESULTCODE = 01;
    String pathoffile;
    private ValueCallback<Uri[]> fPathCallback;
    ImageView imageView;

    Intent contentSelectionIntent = null;
    Intent[] intentArray;
    View fragment_camera;
    Button ImageCLICK;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment_camera = inflater.inflate(R.layout.fragment_camera, container, false);
        initializationfields();

        return fragment_camera;
    }

    private void initializationfields() {
        ImageCLICK=fragment_camera.findViewById(R.id.ImageCLICK);
        imageView = fragment_camera.findViewById(R.id.clickImage);
        ImageCLICK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckConditionsForCamera();

            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
    }

    private void Takephoto() {
        Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takepic != null) {
            File photofile = null;
            // try{
            //    sharefile=createPhotoFile();
            //}catch (Exception e)
            //{
            //    e.printStackTrace();
            //}
            photofile = createPhotoFile();
            if (photofile != null) {
                pathoffile = photofile.getAbsolutePath();
                Uri photUri = FileProvider.getUriForFile(getContext(), "com.deepanshu.whatsappdemo.fileprovider", photofile);
                takepic.putExtra(MediaStore.EXTRA_OUTPUT, photUri);
                startActivityForResult(takepic, 1);
            }
        }
        else {
            Toast.makeText(getContext(), "pic not capture", Toast.LENGTH_SHORT).show();
        }

    }



    private void openFileManagerAfterPermissionAllowed() {
        if (mUploadMsgArray != null) {
            mUploadMsgArray.onReceiveValue(null);
        }
        mUploadMsgArray = fPathCallback;

        Intent takePictureIntent = openCameraChooser();
        openGalleryChooser();
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

    }

    private Intent openCameraChooser() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra(getString(R.string.photopath), mCM);
            } catch (IOException ex) {
                // Log.e("Webview", getString(R.string.image_file_creation_failed), ex);
            }
            if (photoFile != null) {
                mCM = getString(R.string.file) + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }
        return takePictureIntent;
    }

    private void openGalleryChooser() {
        contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");

    }

    // Create an image file
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat(getString(R.string.yyyy_mm_dd_hh_mm_ss)).format(new Date());
        String imageFileName = getString(R.string.img) + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, getString(R.string.jpg), storageDir);
    }

    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File storagedirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storagedirectory);
        } catch (IOException e) {
            Toast.makeText(getContext(), "errror", Toast.LENGTH_LONG).show();
        }
        return image;
    }

    private void CheckConditionsForCamera() {
        Boolean checkCameraPermission = checkpermission();
        if (checkCameraPermission) {
            //openFileManagerAfterPermissionAllowed();
            Takephoto();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 01);
        }
    }

    private Boolean checkpermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {

            return true;
        } else
            return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == 01) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    //openFileManagerAfterPermissionAllowed();
                    Takephoto();
                    Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
            } else {

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == RESULT_OK) {
            /*    if (requestCode == 1) {
                    Bitmap bitmap = BitmapFactory.decodeFile(pathoffile);
                    imageView.setImageBitmap(bitmap);
                }
              if (requestCode == FILECHOOSER_RESULTCODE) {
                    if (null == mUploadMsgArray) {
                        return;
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                            mUploadMsgArray.onReceiveValue(results);
                            mUploadMsgArray = null;
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                            mUploadMsgArray.onReceiveValue(results);
                            mUploadMsgArray = null;
                        }
                    }
                }
            }//ok
            else { //not ok
                mUploadMsgArray.onReceiveValue(null);
                //mUploadMsgArray.onReceiveValue(new Uri[]{});
                mUploadMsgArray = null;
            }
        } else {*/
                Bitmap bitmap = BitmapFactory.decodeFile(pathoffile);
                imageView.setImageBitmap(bitmap);

                if (requestCode == FILECHOOSER_RESULTCODE) {
                    if (null == mUploadMsg) return;
                    Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                    mUploadMsg.onReceiveValue(result);
                    mUploadMsg = null;
                }
            }

            }

}}
