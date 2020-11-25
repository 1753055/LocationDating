package com.group05.mylocation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.group05.mylocation.Modal.SQLiteHelper;
import com.group05.mylocation.Modal.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Menu_Dating__Match extends Fragment {

    private FirebaseAuth myAuth;
    FirebaseFirestore db;
    int checker = 0;
    private ArrayList<String> listuid = new ArrayList<String>();
    User userInfo = new User();
    SQLiteHelper dbase;
    Button btnLike ; Button btnNo ; TextView txtName, txtQuotes, txtAge, txtDistance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_menu__dating___match, container, false);

        myAuth=FirebaseAuth.getInstance();

        final FirebaseUser user=myAuth.getCurrentUser();
        //add data (list tracked devices into spinner


        btnLike = (Button)v.findViewById(R.id.btn_like);
        btnNo = (Button)v.findViewById(R.id.btn_no);
        txtName = (TextView)v.findViewById(R.id.textViewName);
        txtQuotes = (TextView)v.findViewById(R.id.textViewQuotes);
        txtAge = (TextView)v.findViewById(R.id.textViewAge);
        txtDistance = (TextView)v.findViewById(R.id.textViewDistance);

        dbase = new SQLiteHelper(this.getContext());
        dbase.createDefaultNotesIfNeed();


        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user=myAuth.getCurrentUser();
                db=FirebaseFirestore.getInstance();
                db.collection("User").document(user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        userInfo = User.MapToUser(document.getData(), user.getUid());
                                        if (userInfo.getLiked() != null){
                                            if (userInfo.getLiked().indexOf(listuid.get(checker)) != -1) {
                                                Map<String, Object> tmp1 = new HashMap<String, Object>();
                                                ArrayList<String> tmp4 = new ArrayList<String>();
                                                tmp4.add(userInfo.getUid());
                                                tmp1.put("connection", tmp4);
                                                Map<String, Object> tmp2 = new HashMap<String, Object>();
                                                ArrayList<String> tmp3 = new ArrayList<String>();
                                                tmp3.add(listuid.get(checker));
                                                tmp2.put("connection", tmp3);
                                                db.collection("User").document(userInfo.getUid()).set(tmp2, SetOptions.merge());
                                                db.collection("User").document(listuid.get(checker)).set(tmp1, SetOptions.merge());
                                            }
                                        }
                                        else{
                                            Map<String, Object> tmp1 = new HashMap<String, Object>();
                                            ArrayList<String> tmp4 = new ArrayList<String>();
                                            tmp4.add(userInfo.getUid());
                                            tmp1.put("liked", tmp4);
                                            Map<String, Object> tmp2 = new HashMap<String, Object>();
                                            ArrayList<String> tmp3 = new ArrayList<String>();
                                            tmp3.add(listuid.get(checker));
                                            tmp2.put("like", tmp3);
                                            db.collection("User").document(userInfo.getUid()).set(tmp2, SetOptions.merge());
                                            db.collection("User").document(listuid.get(checker)).set(tmp1, SetOptions.merge());
                                        }

                                    }
                                }
                            }
                        });
                if(checker < listuid.size())
                {
                    checker++;
                }
                else checker = 0;
                loadChecker();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker < listuid.size())
                {
                    checker++;
                }
                else checker = 0;
                loadChecker();
            }
        });

        loadInfo();
        listuid.addAll(dbase.getAllNotes());
        if(listuid.size() != 0 ) loadChecker();

        return v;
    }


    public void loadInfo(){
        final FirebaseUser user=myAuth.getCurrentUser();
        db=FirebaseFirestore.getInstance();
        db.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                dbase.addNote(document.getId());
                            }
                        } else {
                        }

                    }
                });
    }

    private void loadChecker() {

        db.collection("User").document(listuid.get(checker))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User tmp = User.MapToUser(document.getData(), listuid.get(checker));
                                txtName.setText(tmp.getFullName());
                            }
                        }
                    }
                });
    }
}