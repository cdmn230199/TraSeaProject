package com.example.TraSeApp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.TraSeApp.databinding.ActivityLoginBinding;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final Pattern PASS_WORD = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,16}");
    ActivityLoginBinding binding;
    CallbackManager callbackManager;
    FirebaseAuth auth;
    ProgressDialog pd;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();


        binding.btnLogIn.setOnClickListener(this);

        binding.tvSignUp.setOnClickListener(this);

        binding.tvForgotPass.setOnClickListener(this);

        registerSignInFBClient();



    }


    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null)
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            gotoMainActivity();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        try {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception o) {
            // Hạn chế crash khi ko login đc hay back lại login page
            // Do nothing
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSignUp:
                Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.btnLogIn:
                handleSignIn();

                break;

            case R.id.tv_forgotPass:
                Intent intent2 = new Intent(this,ForgotPasswordActivity.class);
                startActivityForResult(intent2,888);

        }
    }


    private void handleSignIn() {

        String email = binding.txtUser.getText().toString().trim();
        String password = binding.txtPassword.getText().toString().trim();

        pd = new ProgressDialog(LogInActivity.this);
        pd.setMessage("Please wait a moment....");
        pd.show();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required !", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(auth.getCurrentUser().getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        pd.dismiss();
                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                        if (firebaseUser != null) {
                                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        pd.dismiss();
                                    }
                                });
                            } else {
                                pd.dismiss();
                                Toast.makeText(LogInActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    public void registerSignInFBClient() {
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getFacebookAccountInfo(loginResult.getAccessToken());
                        Log.d("AAA", "success");
                    }

                    @Override
                    public void onCancel() {
                        Log.d("AAA", "cancel");

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("AAA", "error");

                    }
                });
    }

    public void getFacebookAccountInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String name = object.getString("name");
                    //
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle param = new Bundle();
        param.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(param);
        request.executeAsync();
    }


    public void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
