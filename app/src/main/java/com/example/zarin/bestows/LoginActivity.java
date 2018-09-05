package com.example.zarin.bestows;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
    private EditText loginEmailText,loginPassText;
    private Button loginBtn,loginRegBtn;
    private FirebaseAuth mAuth;
    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginEmailText=(EditText) findViewById(R.id.login_email);
        loginPassText=(EditText) findViewById(R.id.login_pass);
        loginBtn=(Button)findViewById(R.id.loginBtn);
        loginRegBtn=(Button)findViewById(R.id.login_reg_btn);
        loginProgress=(ProgressBar)findViewById(R.id.login_progress);
        loginProgress.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail=loginEmailText.getText().toString().trim();
                String loginPass=loginPassText.getText().toString().trim();
                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){
                    //Toast.makeText(LoginActivity.this,"Successful",Toast.LENGTH_LONG).show();
                    loginProgress.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                                Toast.makeText(LoginActivity.this,"Successful",Toast.LENGTH_LONG).show();
                            }
                            else{
                                String errorMessage=task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Wrong user-ID or password",Toast.LENGTH_LONG).show();
                            }
                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

//    }
//    protected void onStart(){
//        super.onStart();
//        FirebaseUser currentUser=mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent mainIntent =new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(mainIntent);
//            finish();        }
//    }


}}