package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class resetPassword extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();

        editText = (EditText) findViewById(R.id.resetPassEmail);
        button =(Button) findViewById(R.id.btnReset);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resetPassEmail = editText.getText().toString().trim();
                if (resetPassEmail.isEmpty()){
                    Toast.makeText(resetPassword.this, "Enter Email", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(resetPassEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(resetPassword.this, "Reset Password mail is send", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(resetPassword.this, login.class));
                            }
                            else {
                                Toast.makeText(resetPassword.this, "Enter correct Email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}

