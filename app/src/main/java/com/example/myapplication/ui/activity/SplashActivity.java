package com.example.myapplication.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.enums.UserRole;
import com.example.myapplication.ui.viewmodel.auth.AuthViewModel;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 4000;
    private static final int ANIMATION_DURATION = 1000;

    private AuthViewModel authViewModel;
    private CardView cardLogo;
    private ImageView ivLogo;
    private TextView tvAppName;
    private TextView tvTagline;
    private TextView tvLoadingText;
    private ProgressBar progressBar;
    private View viewDecorTop;
    private View viewDecorBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Setup ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Initialize views
        initViews();

        // Start animations
        startAnimations();

        // Delay for splash screen effect
        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthenticationAndNavigate, SPLASH_DELAY);
    }

    private void initViews() {
        cardLogo = findViewById(R.id.cardLogo);
        ivLogo = findViewById(R.id.ivLogo);
        tvAppName = findViewById(R.id.tvAppName);
        tvTagline = findViewById(R.id.tvTagline);
        tvLoadingText = findViewById(R.id.tvLoadingText);
        progressBar = findViewById(R.id.progressBar);
        viewDecorTop = findViewById(R.id.viewDecorTop);
        viewDecorBottom = findViewById(R.id.viewDecorBottom);
    }

    private void startAnimations() {
        // Initially hide all views
        cardLogo.setAlpha(0f);
        cardLogo.setScaleX(0.3f);
        cardLogo.setScaleY(0.3f);

        tvAppName.setAlpha(0f);
        tvAppName.setTranslationY(50f);

        tvTagline.setAlpha(0f);
        tvTagline.setTranslationY(30f);

        tvLoadingText.setAlpha(0f);
        progressBar.setAlpha(0f);

        viewDecorTop.setAlpha(0f);
        viewDecorTop.setScaleX(0f);
        viewDecorBottom.setAlpha(0f);
        viewDecorBottom.setScaleX(0f);

        // Start sequential animations
        animateLogo();
        animateAppName();
        animateTagline();
        animateLoadingElements();
        animateDecorations();
    }

    private void animateLogo() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            AnimatorSet logoAnimSet = new AnimatorSet();

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(cardLogo, "scaleX", 0.3f, 1.2f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(cardLogo, "scaleY", 0.3f, 1.2f, 1f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(cardLogo, "alpha", 0f, 1f);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(ivLogo, "rotation", 0f, 360f);

            logoAnimSet.playTogether(scaleX, scaleY, alpha, rotation);
            logoAnimSet.setDuration(ANIMATION_DURATION);
            logoAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
            logoAnimSet.start();
        }, 300);
    }

    private void animateAppName() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            AnimatorSet nameAnimSet = new AnimatorSet();

            ObjectAnimator alpha = ObjectAnimator.ofFloat(tvAppName, "alpha", 0f, 1f);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(tvAppName, "translationY", 50f, 0f);

            nameAnimSet.playTogether(alpha, translationY);
            nameAnimSet.setDuration(800);
            nameAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
            nameAnimSet.start();
        }, 800);
    }

    private void animateTagline() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            AnimatorSet taglineAnimSet = new AnimatorSet();

            ObjectAnimator alpha = ObjectAnimator.ofFloat(tvTagline, "alpha", 0f, 1f);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(tvTagline, "translationY", 30f, 0f);

            taglineAnimSet.playTogether(alpha, translationY);
            taglineAnimSet.setDuration(600);
            taglineAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
            taglineAnimSet.start();
        }, 1200);
    }

    private void animateLoadingElements() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            AnimatorSet loadingAnimSet = new AnimatorSet();

            ObjectAnimator textAlpha = ObjectAnimator.ofFloat(tvLoadingText, "alpha", 0f, 1f);
            ObjectAnimator progressAlpha = ObjectAnimator.ofFloat(progressBar, "alpha", 0f, 1f);

            loadingAnimSet.playTogether(textAlpha, progressAlpha);
            loadingAnimSet.setDuration(500);
            loadingAnimSet.start();
        }, 1800);
    }

    private void animateDecorations() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            // Top decoration
            AnimatorSet topDecorSet = new AnimatorSet();
            ObjectAnimator topAlpha = ObjectAnimator.ofFloat(viewDecorTop, "alpha", 0f, 1f);
            ObjectAnimator topScale = ObjectAnimator.ofFloat(viewDecorTop, "scaleX", 0f, 1f);
            topDecorSet.playTogether(topAlpha, topScale);
            topDecorSet.setDuration(800);
            topDecorSet.start();

            // Bottom decoration
            AnimatorSet bottomDecorSet = new AnimatorSet();
            ObjectAnimator bottomAlpha = ObjectAnimator.ofFloat(viewDecorBottom, "alpha", 0f, 1f);
            ObjectAnimator bottomScale = ObjectAnimator.ofFloat(viewDecorBottom, "scaleX", 0f, 1f);
            bottomDecorSet.playTogether(bottomAlpha, bottomScale);
            bottomDecorSet.setDuration(800);
            bottomDecorSet.setStartDelay(200);
            bottomDecorSet.start();
        }, 1000);
    }


    private void checkAuthenticationAndNavigate() {
        // Add exit animation before navigation
        animateExit(() -> {
            if (authViewModel.isLoggedIn()) {
                // User is logged in, navigate based on role
                String userRole = authViewModel.getCurrentUserRole();
                navigateBasedOnRole(userRole);
            } else {
                // User not logged in, go to login
                navigateToLogin();
            }
        });
    }

    private void animateExit(Runnable onComplete) {
        AnimatorSet exitAnimSet = new AnimatorSet();

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(findViewById(R.id.containerMain), "alpha", 1f, 0f);
        ObjectAnimator scaleDown = ObjectAnimator.ofFloat(findViewById(R.id.containerMain), "scaleX", 1f, 0.8f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(findViewById(R.id.containerMain), "scaleY", 1f, 0.8f);

        exitAnimSet.playTogether(fadeOut, scaleDown, scaleDownY);
        exitAnimSet.setDuration(500);
        exitAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());

        exitAnimSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                onComplete.run();
            }
        });

        exitAnimSet.start();
    }

    private void navigateBasedOnRole(String role) {
        Intent intent;

        if (role == null) {
            role = UserRole.CUSTOMER.toString(); // Default
        }

        switch (role) {
            case "MANAGER":
                intent = new Intent(this, ManagerDashboardActivity.class);
                break;
            case "CUSTOMER":
            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }

        startActivity(intent);
        // Add smooth transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        // Add smooth transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}