package com.group05.mylocation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnRegister, btnLogin;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);


        //check User
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent (StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnRegister = (Button)findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener((View.OnClickListener) this);
        btnRegister.setOnClickListener((View.OnClickListener) this);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == btnRegister.getId()) {
            Intent myIntentA1A2 = new Intent (StartActivity.this, RegisterActivity.class);
            startActivityForResult(myIntentA1A2, 1122);
        }
        else if (v.getId() == btnLogin.getId()) {
            Intent login = new Intent (StartActivity.this, LoginActivity.class);
            startActivityForResult(login, 1122);
        }
    };
}
