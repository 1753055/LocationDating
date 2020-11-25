package com.group05.mylocation.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group05.mylocation.Adapter.CardStackAdapter;
import com.group05.mylocation.Interface.MyCallback;
import com.group05.mylocation.Interface.UserCallback;
import com.group05.mylocation.R;
import com.group05.mylocation.User;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Menu_Dating_Match extends Fragment {

    private static final String TAG = "MainActivity";
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private FirebaseAuth myAuth = FirebaseAuth.getInstance();
    private List<User> list;
    private List<String> listLike;
    private CardStackView cardStackView;
    StorageReference storageReference;//difference
    private User user = new User();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_match, container, false);
        list = new ArrayList<>();
        listLike = new ArrayList<>();
        getCurrentUser(new UserCallback() {
            @Override
            public void onCallback(User value) {
                user = value;
                loadMatchLikeDislike(new MyCallback() {
                    @Override
                    public void onCallback(List<User> value) {

                    }
                    public void onCallback2(List<String> value) {
                        listLike = value;
                        Log.d(TAG, "onCallback: " + listLike.toString());
                        addList();
                    }
                });
            }

            @Override
            public void onCallbackImage(Uri uri) {

            }
        });

        cardStackView = v.findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(this.getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                String uid = list.get(manager.getTopPosition() - 1).getUid();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                if (direction == Direction.Right || direction == Direction.Top) {
                    DocumentReference docRef = db.collection("User").document(uid).collection("like").document(firebaseUser.getUid());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot document1 = task1.getResult();
                                if (document1.exists() && document1.get("like", boolean.class) == true) {
                                    getCurrentUser(new UserCallback() {
                                        @Override
                                        public void onCallback(User value) {
                                            Map tmp = new HashMap();
                                            tmp.put("like", true);
                                            Map tmp2 = new HashMap();
                                            tmp2.put("requested", false);
                                            tmp2.put("isRequested", false);
                                            tmp2.put("accepted", false);
                                            tmp2.put("isAccepted", false);
                                            tmp2.put("fullname", list.get(manager.getTopPosition() - 1).getFullName());
                                            Map tmp3 = new HashMap();
                                            tmp3.put("requested", false);
                                            tmp3.put("isRequested", false);
                                            tmp3.put("accepted", false);
                                            tmp3.put("isAccepted", false);
                                            tmp3.put("fullname", value.getFullName());
                                            Log.d(TAG, "onCallback: " + value.getFullName());
                                            Log.d(TAG, "onCallback: " + list.get(manager.getTopPosition() - 1).getFullName());
                                            db.collection("User").document(firebaseUser.getUid()).collection("connection").document(uid).set(tmp2, SetOptions.merge());
                                            db.collection("User").document(firebaseUser.getUid()).collection("like").document(uid).set(tmp, SetOptions.merge());
                                            db.collection("User").document(uid).collection("connection").document(firebaseUser.getUid()).set(tmp3, SetOptions.merge());
                                        }

                                        @Override
                                        public void onCallbackImage(Uri uri) {

                                        }
                                    });

                                } else {
                                        Map tmp = new HashMap();
                                        tmp.put("like", true);
                                        db.collection("User").document(firebaseUser.getUid()).collection("like").document(uid).set(tmp);
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task1.getException());
                            }
                        }
                    });
                }
                else{
                    Map tmp = new HashMap();
                    tmp.put("like", false);
                    db.collection("User").document(firebaseUser.getUid()).collection("like").document(uid).set(tmp);
                }
            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        cardStackView.setLayoutManager(manager);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

        return v;
    }
    private void loadMatchLikeDislike(MyCallback onCallBack){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> tmp = new ArrayList<String>();
        assert firebaseUser != null;
        db.collection("User/" + user.getUid() + "/like")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                tmp.add(document.getId());
                            }
                            onCallBack.onCallback2(tmp);
                        } else {
                            Log.d(TAG, "Error getting documents: ");
                        }
                    }
                });
    }
    private void addList() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        assert firebaseUser != null;
        db.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if(!document.getId().equals(user.getUid())){
                                    if(document.get("interest", Integer.class) == 3 || document.get("interest", Integer.class)== user.getSex()){
                                        if(user.getInterest() == 3 || user.getInterest() == document.get("gender", Integer.class)) {
                                            int age = 2020 - Integer.parseInt(document.get("birthday", String.class).split("/ ")[2]);
                                            int userage =  2020 - Integer.parseInt(user.getBirthday().split("/ ")[2]);
                                            if(age < user.getMaxage() && age > user.getMinage() && userage > document.get("minage", Integer.class) &&userage < document.get("maxage", Integer.class) ) {
                                                if (listLike.indexOf(document.getId()) == -1) {
                                                    getUri(document.getId(), new UserCallback() {
                                                        @Override
                                                        public void onCallback(User value) {

                                                        }

                                                        @Override
                                                        public void onCallbackImage(Uri uri) {
                                                            Log.d(TAG, "Map test" + document.getData().toString());
                                                            User user = new User(uri, (String) document.get("fullname"), 2020 - Integer.parseInt(document.get("birthday", String.class).split("/ ")[2]), document.get("biography", String.class), document.getId());
                                                            list.add(user);
                                                            adapter = new CardStackAdapter(list);
                                                            cardStackView.setAdapter(adapter);
                                                        }
                                                    });
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ");
                        }
                    }
                });
    }
    private void getCurrentUser(UserCallback myCallback){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("User").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                if (task1.isSuccessful()) {
                    DocumentSnapshot document1 = task1.getResult();
                    if (document1.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document1.getData());
                        User user = User.MapToUser(document1.getData(), firebaseUser.getUid());
                        myCallback.onCallback(user);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task1.getException());
                }
            }
        });
    }
    private void getUri(String uid, UserCallback myCallback){
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+uid+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                myCallback.onCallbackImage(uri);
            }
        });
    }
}
