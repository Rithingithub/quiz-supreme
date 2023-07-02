package com.example.quiz1;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;

import com.airbnb.lottie.LottieAnimationView;
import com.example.quiz1.Custom_quiz;
import com.example.quiz1.R;

public class Splash_create_quiz extends AppCompatActivity {

    private LottieAnimationView animationView;

    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_create_quiz);

        isAdmin = getIntent().getBooleanExtra("ChatAdmin", false);

        animationView = findViewById(R.id.animation_view);
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Animation ended
                startActivity(new Intent(Splash_create_quiz.this, Custom_quiz.class));
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Animation cancelled
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // Animation repeated
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        animationView.playAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        animationView.pauseAnimation();
    }
}
