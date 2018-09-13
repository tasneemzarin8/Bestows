package com.example.zarin.bestows;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Build.*;

public class SetupActivity extends AppCompatActivity {
private CircleImageView setupImage;
private Uri mainImageURI=null;
private EditText setupName, steupDescription;
private Button setupAccountBtn;
private String user_id;
private Boolean isChanged=false;
private FirebaseFirestore firebaseFirestore;
private ProgressBar setupProgress;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        setupName=(EditText)findViewById(R.id.setup_name);
        setupImage=(CircleImageView) findViewById(R.id.setup_image);
        steupDescription=(EditText)findViewById(R.id.setup_description);
        setupAccountBtn=(Button)findViewById(R.id.setup_button);
        setupProgress=(ProgressBar)findViewById(R.id.setupProgress);

        setupAccountBtn.setEnabled(false);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String name=task.getResult().getString("name");
                        String description=task.getResult().getString("description");
                        String image=task.getResult().getString("image");
                        mainImageURI=Uri.parse(image);
                        setupName.setText(name);
                        steupDescription.setText(description);
                        RequestOptions placeholderRequest= new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
                    }
                    else {
                        Toast.makeText(SetupActivity.this,"Data does not exist",Toast.LENGTH_LONG).show();
                    }
                    }
                else{
                    String error=task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,"Firestore retrieve Error: "+error,Toast.LENGTH_LONG).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);
                setupAccountBtn.setEnabled(true);

            }
        });
        setupAccountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String username = setupName.getText().toString();
                final String description = steupDescription.getText().toString();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(description) && mainImageURI != null) {
                setupProgress.setVisibility(View.VISIBLE);

                if(isChanged) {
                        user_id = firebaseAuth.getCurrentUser().getUid();
                        StorageReference image_path = storageReference.child("Profle_Images").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(task, username, description);
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "Image Error: " + error, Toast.LENGTH_LONG).show();
                                    setupProgress.setVisibility(View.INVISIBLE);

                                }

                            }
                        });
                    }
                else {
                    storeFirestore(null,username,description);
                }
                }

            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //////////////////////
                if(VERSION.SDK_INT >= VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(SetupActivity.this,"Permission denied! ",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(SetupActivity.this);                    }
                }
                else {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(SetupActivity.this);

                }
                /////////////////////
            }
         });

    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task,String username,String description) {
        Uri download_uri;
        if(task!=null) {
             download_uri = task.getResult().getDownloadUrl();
        }
        else{
             download_uri = mainImageURI;
        }
        Toast.makeText(SetupActivity.this,"The image is Uploaded",Toast.LENGTH_LONG).show();
        Map<String,String> userMAP= new HashMap<>();
        userMAP.put("name",username);
        userMAP.put("description",description);
        userMAP.put("image",download_uri.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMAP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SetupActivity.this,"The user settings are updated",Toast.LENGTH_LONG).show();
                    Intent mainIntent=new Intent(SetupActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
                else {
                    String error=task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,"Firestore Error: "+error,Toast.LENGTH_LONG).show();

                }
                setupProgress.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();

                setupImage.setImageURI(mainImageURI);

                isChanged=true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
