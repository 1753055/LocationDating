package com.group05.mylocation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group05.mylocation.Adapter.MessagesAdapter;
import com.group05.mylocation.Modal.Messages;
import com.group05.mylocation.Modal.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    String TAG = "messagesTAG";
    CircleImageView profile_image;
    TextView usrName;

    FirebaseUser user;
    FirebaseFirestore db;

    Intent intent;

    ImageButton send;
    EditText message;

    List<Messages> messagesList;
    RecyclerView recyclerView;
    MessagesAdapter messagesAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

//        Toolbar toolbar = findViewById(R.id.chat_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        profile_image = findViewById(R.id.profile_image);
        usrName = findViewById(R.id.chatUserName);
        send = findViewById(R.id.btn_send);
        message = findViewById(R.id.text_send);
        recyclerView = findViewById(R.id.messages_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        String uid = intent.getStringExtra("uid");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("User").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                if (task1.isSuccessful()) {
                    DocumentSnapshot document1 = task1.getResult();
                    if (document1.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                        User user = User.MapToUser(document1.getData(), uid);
                        usrName.setText(user.getFullName());

                        StorageReference profileRef = storageReference.child("users/"+user.getUid()+"/profile.jpg");
                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Glide.with(getApplicationContext())
                                        .load(uri)
                                        .override(40,40)
                                        .into(profile_image);
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task1.getException());
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString();
                if(!msg.equals("")){
                    sendMessage(msg, user.getUid(), uid);
                }
                message.setText("");
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(message.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        readMessages(user.getUid(), uid);
    }

    private void sendMessage(String message, String sender, String receiver){
        String path="";
        if(sender.compareTo(receiver)>0){
            path = sender+"+"+receiver;
        } else {
            path = receiver+"+"+sender;
        }
        Log.d(TAG,message);
        db.collection("Messages").document(path).collection("Content").document(Calendar.getInstance().getTime().toString())
                .set(new Messages(sender, receiver, message));

    }
    private void readMessages(final String sender, final String receiver){

        String path="";
        if(sender.compareTo(receiver)>0){
            path = sender+"+"+receiver;
        } else {
            path = receiver+"+"+sender;
        }
        db.collection("Messages/"+path+"/Content")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        messagesList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Log.w(TAG, doc.getId());
                            messagesList.add(doc.toObject(Messages.class));
                            StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/"+ receiver+"/profile.jpg");
                            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    messagesAdapter = new MessagesAdapter(MessageActivity.this, messagesList, uri);
                                    recyclerView.setAdapter(messagesAdapter);
                                }
                            });

                        }

                    }
                });
    }
}
