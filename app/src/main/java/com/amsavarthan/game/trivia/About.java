package com.amsavarthan.game.trivia;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void openWebsite(View view) {

        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://lvamsavarthan.github.io/lvstore"));
        startActivity(i);

    }

    public void openInstagram(View view) {
        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.instagram.com/lvamsavarthan"));
        startActivity(i);
    }

    public void openGithub(View view) {
        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://github.com/lvamsavarthan"));
        startActivity(i);
    }

    public void openEmail(View view) {

        Intent email=new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL,new String[]{"amsavarthan.a@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT,"Sent from Trivia v1.0.0 ("+ Build.BRAND+", "+Build.VERSION.SDK_INT+")");
        email.putExtra(Intent.EXTRA_TEXT,"");
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email,"Send mail using..."));

    }

    public void shakeView(View view) {
        
        view.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake_view));

    }

    public void openGooglePlay(View view) {

        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://play.google.com/store/apps/dev?id=8738176098315595821"));
        startActivity(i);

    }
}
