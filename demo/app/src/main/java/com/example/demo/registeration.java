package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class registeration extends AppCompatActivity {

    private EditText email, password, name;
    private Button register;
    private TextView stat2;
    private FirebaseAuth firebaseAuth;
    private String user_email, user_pass, user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        firebaseAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.etRegEmail);
        password = (EditText) findViewById(R.id.etRegPass);
        name = (EditText) findViewById(R.id.etRegName);
        register = (Button) findViewById(R.id.btnReg);
        stat2 = (TextView) findViewById(R.id.tvLogin);

        stat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registeration.this, login.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()){
                    user_email = email.getText().toString().trim();
                    user_pass = password.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendEmailVerification();
                                sendInfo();
                                finish();
                                startActivity(new Intent(registeration.this, login.class));
                            }
                            else {
                                Toast.makeText(registeration.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public boolean isValid(){
        user_email = email.getText().toString().trim();
        user_pass = password.getText().toString().trim();
        user_name = name.getText().toString().trim();
        if (user_email.isEmpty() && user_pass.isEmpty() && user_name.isEmpty()){
            Toast.makeText(this, "Something Went Wrong TRY AGAIN", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(registeration.this, "Verification mail is send", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(registeration.this, login.class));
                    }else {
                        Toast.makeText(registeration.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendInfo(){
        UserInfo userInfo = new UserInfo(user_name, user_email);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //DatabaseReference myref = firebaseDatabase.getReference(firebaseAuth.getUid());
        DatabaseReference myref = firebaseDatabase.getReference("user/"+firebaseAuth.getUid());
        myref.setValue(userInfo);
    }
}

