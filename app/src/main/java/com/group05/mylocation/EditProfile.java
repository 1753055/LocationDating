package com.group05.mylocation;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class EditProfile extends AppCompatActivity {

    EditText etFullname;
    EditText etBirthday;
    EditText etPhone;
    EditText etBio;

    RadioGroup radioGrGender;
    RadioButton rbMale;
    RadioButton rbFemale;

    Button btnSave;
    Button btnCancel;
    ImageView btnPick;

    FirebaseUser currentFirebaseUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etFullname = (EditText) findViewById(R.id.etFullname);
        etBirthday = (EditText) findViewById(R.id.etDOB);
        radioGrGender = (RadioGroup) findViewById(R.id.rgGender);
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etBio = (EditText) findViewById(R.id.etBio);
        btnPick = (ImageView) findViewById(R.id.btnPick);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = currentFirebaseUser.getUid().toString();
        final DocumentReference docRef = db.collection("User").document(uId);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);


        rbMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RadioButton)radioGrGender.getChildAt(0)).setChecked(true);
                ((RadioButton)radioGrGender.getChildAt(1)).setChecked(false);
            }
        });

        rbFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RadioButton)radioGrGender.getChildAt(0)).setChecked(false);
                ((RadioButton)radioGrGender.getChildAt(1)).setChecked(true);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docRef.get().addOnCompleteListener((task) -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()){
                            User user = User.MapToUser(document.getData(), document.getId());

                            user.setFullName(etFullname.getEditableText().toString());
                            user.setBirthday(etBirthday.getEditableText().toString());
                            user.setPhone(etPhone.getEditableText().toString());
                            user.setBiography(etBio.getEditableText().toString());

                            // get checked in gender
                            if (rbMale.isChecked() == true){
                                user.setSex(1);
                            }
                            else if (rbFemale.isChecked() == true){
                                user.setSex(2);
                            }

                            db.collection("User").document(uId).set(user.toMap());
                        }
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Intent back = new Intent(EditProfile.this, MainActivity.class);
                startActivity(back);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backHome = new Intent(EditProfile.this, MainActivity.class);
                startActivity(backHome);
                finish();
            }
        });

        docRef.get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    User user = User.MapToUser(document.getData(), document.getId());
                    etFullname.setText(user.getFullName());
                    etBirthday.setText(user.getBirthday());
                    etPhone.setText(user.getPhone());
                    etBio.setText(user.getBiography());
                }
            }
        });

        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                //newDate.set(year, monthOfYear, dayOfMonth);
                etBirthday.setText(Integer.toString(dayOfMonth) + " / " + Integer.toString(monthOfYear + 1) + " / " + Integer.toString(year));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTime.show();
            }
        });
    }
}
