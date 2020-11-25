package com.group05.mylocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileUser extends Fragment {

    TextView tvEmail;
    TextView tvFullName;
    TextView tvGender;
    TextView tvDOB;
    TextView tvPhone;
    TextView tvBio;
    Button btnEdit;
    ImageView imgProfile;
    Button btnChooseImg;
    Button btnChangePass;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef;

    FirebaseAuth fAuth;
    //FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;//difference

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile_user, container, false);

        tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        tvFullName = (TextView) v.findViewById(R.id.tvFullname);
        tvGender = (TextView) v.findViewById(R.id.tvGender);
        tvDOB = (TextView) v.findViewById(R.id.tvDOB);
        tvPhone = (TextView) v.findViewById(R.id.tvPhone);
        tvBio = (TextView) v.findViewById(R.id.tvBio);

        btnEdit = (Button) v.findViewById(R.id.btnEdit);

        imgProfile = (ImageView) v.findViewById(R.id.imgProfile);
        btnChooseImg = (Button) v.findViewById(R.id.btnChooseImg);

        final FirebaseUser currentFirebaseUser;
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgProfile);
            }
        });

        btnChangePass = (Button) v.findViewById(R.id.btnChange);
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetPassword = new EditText(v.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter New Password > 6 Characters long.");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String newPassword = resetPassword.getText().toString();
                        currentFirebaseUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileUser.this.getActivity(), "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileUser.this.getActivity(), "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });

                passwordResetDialog.create().show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(ProfileUser.this.getActivity(), EditProfile.class);
                startActivity(profile);
            }
        });

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallaryIntent, 1000);
            }
        });

        String uId = currentFirebaseUser.getUid().toString();

        DocumentReference docRef = db.collection("User").document(uId);
        docRef.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    User user = User.MapToUser(document.getData(), document.getId());
                    tvEmail.setText(user.getEmail());
                    tvFullName.setText(user.getFullName());
                    tvBio.setText(user.getBiography());
                    if (user.getSex() == 1){
                        tvGender.setText("Male");
                    }
                    else if (user.getSex() == 2){
                        tvGender.setText("Female");
                    }
                    //Date birth = document.getDate("birthday");
                    //tvDOB.setText(new SimpleDateFormat("yyyy-MM-dd").format(birth));
                    tvDOB.setText(user.getBirthday());
                    tvPhone.setText(user.getPhone());
                }
            }
        });
        imgProfile.setClipToOutline(true);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1000){
                if (resultCode == Activity.RESULT_OK){
                    Uri imgUri = data.getData();
                    //imgProfile.setImageURI(imgUri);

                    uploadImageToFireBase(imgUri);
                }
            }
    }

    private void uploadImageToFireBase(Uri imgUri) {
        //upload img to firebase storage
        StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imgProfile);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileUser.this.getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
