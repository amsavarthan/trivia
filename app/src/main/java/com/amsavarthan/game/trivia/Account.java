package com.amsavarthan.game.trivia;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Account extends AppCompatActivity {

    private static final String TAG = Account.class.getSimpleName();
    private BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        navbar=findViewById(R.id.navbar);

        loadFragement(new AccountFragment());

        navbar.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.action_account:
                    loadFragement(new AccountFragment());
                    return true;
                case R.id.action_scores:

                    if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                        loadFragement(new CategoryScoreFragment());
                    }else{
                        Toast.makeText(this, "Sign in with your accounts to view your scores", Toast.LENGTH_SHORT).show();
                    }

                    return true;

            }

            return true;

        });

        navbar.setOnNavigationItemReselectedListener(item -> {

            switch (item.getItemId()){
                case R.id.action_account:
                case R.id.action_scores:
            }

        });

    }

    public void loadFragement(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,fragment)
                .commit();
    }

}
