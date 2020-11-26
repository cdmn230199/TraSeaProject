package com.example.TraSeApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassActivity extends AppCompatActivity {
    EditText edt_currentPass, edt_newPass, edt_confirmPass;
    Button btn_confirm, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        initView();

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePwd();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void initView() {
        edt_currentPass = findViewById(R.id.edt_currentPass);
        edt_newPass = findViewById(R.id.edt_newPass);
        edt_confirmPass = findViewById(R.id.edt_confirmPass);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel = findViewById(R.id.btn_cancel);
    }

    private void updatePwd() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String curPass = edt_currentPass.getText().toString().trim();
        String newPass = edt_newPass.getText().toString().trim();
        String confirmPass = edt_confirmPass.getText().toString().trim();

        if (user != null) {
            if ((!curPass.equals(newPass) && (newPass.equals(confirmPass)))) {
                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePassActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ChangePassActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please check again ! New Password must be different from current password !", Toast.LENGTH_SHORT).show();
            }
        }
    }

}