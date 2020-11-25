package com.group05.mylocation.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group05.mylocation.Adapter.ConnectionAdapter;
import com.group05.mylocation.User;
import com.group05.mylocation.Interface.MyCallback;
import com.group05.mylocation.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class Connection extends Fragment {

    String TAG = "matches";
    private RecyclerView recyclerView;

    private ConnectionAdapter matchesAdater;
    private List<User> list;

    FirebaseFirestore db;

    public LinearLayout skeletonLayout;
    public ShimmerLayout shimmer;
    public LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matches, container, false);

        recyclerView = v.findViewById(R.id.matches_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        skeletonLayout = v.findViewById(R.id.skeletonLayout);
        shimmer = v.findViewById(R.id.shimmerSkeleton);
        this.inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        list = new ArrayList<>();
        showSkeleton(true);
        animateReplaceSkeleton(v);
        readMatches();
        return v;
    }
    public void showSkeleton(boolean show) {

        if (show) {

            skeletonLayout.removeAllViews();

            int skeletonRows = 1;
            for (int i = 0; i <= skeletonRows; i++) {
                ViewGroup rowLayout = (ViewGroup) inflater
                        .inflate(R.layout.skeleton_row, null);
                skeletonLayout.addView(rowLayout);
            }
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmerAnimation();
            skeletonLayout.setVisibility(View.VISIBLE);
            skeletonLayout.bringToFront();
        } else {
            shimmer.stopShimmerAnimation();
            shimmer.setVisibility(View.GONE);
        }
    }
    public void animateReplaceSkeleton(View listView) {

        listView.setVisibility(View.VISIBLE);
        listView.setAlpha(0f);
        listView.animate().alpha(1f).setDuration(500).start();

        skeletonLayout.animate().alpha(0f).setDuration(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                showSkeleton(false);
            }
        }).start();

    }
    private void readMatches() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        assert firebaseUser != null;
        db.collection("User/"+firebaseUser.getUid()+"/connection")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if(document.get("requested", boolean.class) == false && document.get("isAccepted", boolean.class) == false) {
                                    getUser(document.getId(), new MyCallback() {
                                        @Override
                                        public void onCallback(List<User> value) {
                                            matchesAdater = new ConnectionAdapter(getContext(), list);
                                            recyclerView.setAdapter(matchesAdater);
                                        }

                                        @Override
                                        public void onCallback2(List<String> value) {

                                        }
                                    });
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ");
                        }
                    }
                });
    }
    private void getUser(String uid, MyCallback
            myCallback){
        DocumentReference docRef = db.collection("User").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                if (task1.isSuccessful()) {
                    DocumentSnapshot document1 = task1.getResult();
                    if (document1.exists()) {
                        User user = User.MapToUser(document1.getData(), uid);
                        list.add(user);
                        myCallback.onCallback(list);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task1.getException());
                }
            }
        });
    }

}
