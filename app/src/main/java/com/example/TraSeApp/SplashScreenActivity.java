package com.example.TraSeApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreenActivity extends AppCompatActivity {
    ImageView iv_background;
    LottieAnimationView iv_splash_man_riding,iv_splash_progressbar;
    TextView tv_welcome,tv_trase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViews();

        animateViews();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        }, 4300);

    }

    private void animateViews() {
        iv_background.animate().translationY(-2600).setDuration(700).setStartDelay(4000);
        tv_welcome.animate().translationY(2600).setDuration(700).setStartDelay(4000);
        iv_splash_man_riding.animate().translationY(2600).setDuration(700).setStartDelay(4000);
        iv_splash_progressbar.animate().translationY(2600).setDuration(700).setStartDelay(4000);
        tv_trase.animate().translationY(2600).setDuration(700).setStartDelay(4000);
    }

    private void findViews() {
        tv_welcome = findViewById(R.id.tv_welcome);
        tv_trase = findViewById(R.id.tv_trase);
        iv_splash_man_riding = findViewById(R.id.iv_splash_man_riding);
        iv_splash_progressbar = findViewById(R.id.iv_splash_progressbar);
        iv_background = findViewById(R.id.iv_background);
    }
}