package com.example.zarin.bestows;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class passwordActivity extends AppCompatActivity {

    private EditText passwordemail;
    private Button passwordreset;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        passwordemail=findViewById(R.id.editpass);
        passwordreset=findViewById(R.id.reset);
        firebaseAuth=FirebaseAuth.getInstance();

        passwordreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String useremail = passwordemail.getText().toString().trim();

                if(useremail.equals(""))
                {
                    Toast.makeText(passwordActivity.this,"Please enter your registered Email Id",Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(passwordActivity.this,"Password Reset email sent!",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(passwordActivity.this,LoginActivity.class));

                            }else{
                                Toast.makeText(passwordActivity.this,"Error in Sending Password reset Email",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}

