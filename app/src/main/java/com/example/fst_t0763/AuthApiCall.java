package com.example.fst_t0763;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.APIClient_Classes.AuthHeaderClient;
import com.example.Interface.API_Interface;
import com.example.ModelClasses.AuthApiResponseModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AuthApiCall extends AppCompatActivity {

    private static final int SELECT_PICTURE = 10;
    TextView auth_header_image_text;
    ImageView auth_api_image;
    Button AuthHeader_click;
    Uri picked_image;
    String trial;
    File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_api_call);

        auth_header_image_text = findViewById(R.id.auth_header_image_text);
        AuthHeader_click = findViewById(R.id.AuthHeader_click);
        auth_api_image = findViewById(R.id.auth_api_image);

        auth_header_image_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picktheImage();
            }
        });

        AuthHeader_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });

    }


    private void picktheImage() {
        final CharSequence[] options = {"Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthApiCall.this);
        builder.setTitle("Select Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int option) {
                if (options[option].equals("Choose from Gallery")) {
                    Dexter.withContext(AuthApiCall.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, SELECT_PICTURE);
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    Toast.makeText(AuthApiCall.this, "No image select", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).check();

                } else if (options[option].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == SELECT_PICTURE) {
                picked_image = data.getData();
                auth_api_image.setVisibility(View.VISIBLE);
                auth_api_image.setImageURI(picked_image);
                String[] mstore = {MediaStore.Images.Media.DATA};
                Cursor cursor;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    cursor = getContentResolver().query(picked_image, mstore, null, null);

                    if (cursor.moveToFirst()) {
                        int col_index = cursor.getColumnIndexOrThrow(mstore[0]);
                        trial = cursor.getString(col_index);
                        file = new File(trial);
                        Toast.makeText(this, "retrieved image data", Toast.LENGTH_SHORT).show();
                        auth_header_image_text.setText(trial);

                       /* String path=picked_image.getPath();
                        String filename=picked_image.getLastPathSegment();

                        Log.i("nameandpath",path+"\n"+filename);
*/
                    }

                }
            }
        } else {
            Toast.makeText(this, "failed to get the data", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendRequest() {
        RequestBody Btechid = RequestBody.create(MediaType.parse("multipart/form-data"), "884543153");
        RequestBody ORDERNO = RequestBody.create(MediaType.parse("multipart/form-data"), "vl2a7eaa");
        MultipartBody.Part Image = null;
        if (file != null && file.exists()) {
            RequestBody f1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            Image = MultipartBody.Part.createFormData("file", file.getName(), f1);
        }

        try {


            API_Interface api_interface = AuthHeaderClient.getAuthHeaderClient().AuthheaderClient("http://techsostng.thyrocare.cloud/techsoapi/api/").create(API_Interface.class);
            Call<AuthApiResponseModel> authApiResponseModelCall = api_interface.sendAuthHeader(Btechid, ORDERNO, Image);
            authApiResponseModelCall.enqueue(new Callback<AuthApiResponseModel>() {
                @Override
                public void onResponse(Call<AuthApiResponseModel> call, Response<AuthApiResponseModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AuthApiResponseModel apiResponseModel=response.body();
                        String testAuthApi=apiResponseModel.getResponse();
                        Toast.makeText(AuthApiCall.this, "Response:= "+testAuthApi, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AuthApiCall.this, "response: null", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthApiResponseModel> call, Throwable t) {
                    Toast.makeText(AuthApiCall.this, "failed:" + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });


        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }


    }

}
