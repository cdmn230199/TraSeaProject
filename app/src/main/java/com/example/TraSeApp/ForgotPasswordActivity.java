package com.example.TraSeApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText edt_forgot_email;
    Button btn_submit;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = findViewById(R.id.toolbar_forgot_pass);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_submit = findViewById(R.id.btn_submit);
        edt_forgot_email = findViewById(R.id.edt_forgot_email);

        firebaseAuth = FirebaseAuth.getInstance();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_forgot_email.getText().toString();

                if (email.equals("")) {
                    Toast.makeText(ForgotPasswordActivity.this, "You need to fill in !", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgotPasswordActivity.this,LogInActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}