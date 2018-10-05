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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class balance_add extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private EditText amount,camount;
    private Button wihdraw_amount;
    private FirebaseFirestore firebaseFirestore;
    private int balance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_add);
        amount=findViewById(R.id.amount);
        camount=findViewById(R.id.camount);
        wihdraw_amount=findViewById(R.id.withdraw_balance);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore.collection("Balance").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                double bal=0;
                if(task.isSuccessful()){


                    if(task.getResult().exists()){

                        bal = task.getResult().getDouble("balance");
                        balance= (int) Math.round(bal);

                    }
                    else{
                        balance=0;
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(balance_add.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

            }
        });

        wihdraw_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amounts=amount.getText().toString().trim();
                String camounts=camount.getText().toString().trim();
                if(amounts.equals(camounts)){
                    int i=Integer.parseInt(amounts);


                        balance = balance + i;
                        Map<String, Object> balanceMap = new HashMap<>();
//                balanceMap.put("user_id", current_user_id);
                        balanceMap.put("balance", balance);
                        firebaseFirestore.collection("Balance").document(current_user_id).set(balanceMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(balance_add.this, "Balance Added!", Toast.LENGTH_LONG).show();

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(balance_add.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                                }


                            }
                        });


                }
                else {
                    Toast.makeText(balance_add.this,"Enter Desired Withdrawal amount in Both Fields",Toast.LENGTH_LONG).show();
                }
                Intent profileIntent=new Intent(balance_add.this,ProfileActivity.class);
                startActivity(profileIntent);
                finish();
            }
        });
    }
}
