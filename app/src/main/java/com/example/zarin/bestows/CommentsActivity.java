package com.example.zarin.bestows;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentToolbar;
    private EditText comment_field;
    private ImageView comment_post_btn;
    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String blog_post_id;
    private String current_user_id;
    private String user_id;
    private String userName;
    private int balance=0;
    private String u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentToolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comments");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();
        blog_post_id = getIntent().getStringExtra("blog_post_id");

        comment_field = findViewById(R.id.comment_field);
        comment_post_btn = findViewById(R.id.comment_post_btn);
        comment_list = findViewById(R.id.comment_list);

        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);

        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                     userName= task.getResult().getString("name");


                } else {

                    String errorMessage = task.getException().getMessage();
                }

            }
        });


        firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments")
                .addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String commentId = doc.getDocument().getId();
                                    Comments comments = doc.getDocument().toObject(Comments.class);
                                    commentsList.add(comments);
                                    commentsRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });

///////////////////////////////////
        firebaseFirestore.collection("Posts").document(blog_post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){


                    if(task.getResult().exists()){

                        String bal = task.getResult().getString("user_id");
                        u_id=bal;

                    }
                    else{
                        u_id=null;
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(CommentsActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

            }
        });
        ///////////////////////////////




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
                    Toast.makeText(CommentsActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }

            }
        });

        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message = comment_field.getText().toString();

                Map<String, Object> commentsMap = new HashMap<>();
                commentsMap.put("message", comment_message);
                commentsMap.put("user_id", current_user_id);
                commentsMap.put("timestamp", FieldValue.serverTimestamp());
                commentsMap.put("name",userName);
                int i=Integer.parseInt(comment_message);
                int j=Integer.parseInt(comment_message);
                if(i>balance){
                    Toast.makeText(CommentsActivity.this,"Insufficient Balance",Toast.LENGTH_LONG).show();
                }
                else {
                    balance = balance - i;

                    Map<String, Object> balanceMap = new HashMap<>();
//                balanceMap.put("user_id", current_user_id);
                    balanceMap.put("balance", balance);


                    firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (!task.isSuccessful()) {

                                Toast.makeText(CommentsActivity.this, "Error Posting Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            } else {
                                comment_field.setText("");
                            }
                        }
                    });
                    firebaseFirestore.collection("Balance").document(current_user_id).set(balanceMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(CommentsActivity.this, "Thank You For Donating!", Toast.LENGTH_LONG).show();

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(CommentsActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                            }


                        }
                    });

                    balance = balance + i+i;

                    balanceMap.put("balance", balance);

                    firebaseFirestore.collection("Balance").document(u_id).set(balanceMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(CommentsActivity.this, "Thank You For Donating!", Toast.LENGTH_LONG).show();

                            } else {

                                String error = task.getException().getMessage();
                                Toast.makeText(CommentsActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                            }


                        }
                    });



                }
            }
        });
    }
}

