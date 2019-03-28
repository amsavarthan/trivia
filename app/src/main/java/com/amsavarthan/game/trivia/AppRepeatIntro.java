package com.amsavarthan.game.trivia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

public class AppRepeatIntro extends AppIntro2 {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance("Welcome to Trivia", "Trivia is an online quiz game powered by Open Trivia DB", R.mipmap.ic_launcher_web, Color.parseColor("#212121")));
        addSlide(AppIntro2Fragment.newInstance("10+ categories", "You can choose from any of your favourite categories and play", R.mipmap.comics, Color.parseColor("#e53935")));
        addSlide(AppIntro2Fragment.newInstance("Score Card", "Find out in which you are good or bad and improve your knowledge", R.mipmap.scorecard, Color.parseColor("#5e35b1")));
        addSlide(AppIntro2Fragment.newInstance("OpenSource","Trivia is OpenSource. Join with me and improve the user experience", R.mipmap.github, Color.parseColor("#212121")));


        setFadeAnimation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }
        showSkipButton(true);


    }

    @Override
    public void onSkipPressed() {
        super.onSkipPressed();
        finish();
    }

    @Override
    public void onDonePressed() {
        super.onDonePressed();
        finish();
    }
}
