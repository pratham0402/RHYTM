package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private EditText email, password;
    private Button loginBtn;
    private TextView stat1;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgotPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.etMail);
        password = (EditText) findViewById(R.id.etPass);
        loginBtn = (Button) findViewById(R.id.btnLogin);
        stat1 = (TextView) findViewById(R.id.tvRegister);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        forgotPassword = (TextView) findViewById(R.id.tvForgotPassword);

        // getting current user login already
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            finish();
            startActivity(new Intent(login.this, online_home.class));
        }


        stat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, registeration.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = email.getText().toString().trim();
                String user_pass = password.getText().toString().trim();
                isValid(user_email, user_pass);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, resetPassword.class));
            }
        });


    }

    private void isValid(String user_email,String user_pass){
        progressDialog.setMessage("Welcome.....");
        progressDialog.show();
        //sign in function
        firebaseAuth.signInWithEmailAndPassword(user_email,user_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    checkemailVerification();
                }
                else {
                    Toast.makeText(login.this, "Failed to Login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // check for verification
    private void checkemailVerification(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailflag = firebaseUser.isEmailVerified();
        if (emailflag){
            finish();
            Toast.makeText(login.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(login.this, online_home.class));
        }
        else{
            Toast.makeText(login.this, "Verify your Email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

}

