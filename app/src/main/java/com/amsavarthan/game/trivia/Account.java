package com.amsavarthan.game.trivia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.game.trivia.adapters.ScoresAdapter;
import com.amsavarthan.game.trivia.models.Score;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PlayGamesAuthProvider;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends AppCompatActivity {

    static final String TAG = Account.class.getSimpleName();
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    static final int RC_SIGN_IN = 234;
    GoogleSignInClient mGoogleSignInClient;
    CircleImageView pic;
    TextView name,g_name,points;
    ImageButton logoutBtn;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    private ProgressDialog mDialog;
    private Button signin;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        MobileAds.initialize(this);
        AdView adView=findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());

        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        pic=findViewById(R.id.pic);
        name=findViewById(R.id.name);
        g_name=findViewById(R.id.g_name);
        points=findViewById(R.id.points);
        logoutBtn=findViewById(R.id.logout);
        refreshLayout=findViewById(R.id.refreshLayout);
        recyclerView=findViewById(R.id.recyclerView);
        signin=findViewById(R.id.signIn);

        mDialog=new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestServerAuthCode(getString(R.string.web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        if(mAuth.getCurrentUser()!=null){


            mFirestore.collection("Users")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        String point=documentSnapshot.getString("points");
                        getSharedPreferences("Trivia", Context.MODE_PRIVATE).edit().putString("points",point).apply();

                        name.setText(documentSnapshot.getString("name"));
                        points.setText(String.format("Points scored : %s", point));
                        g_name.setText(documentSnapshot.getString("gplay_name"));
                        Glide.with(getApplicationContext())
                                .load(documentSnapshot.getString("picture"))
                                .into(pic);
                    })
                    .addOnFailureListener(Throwable::printStackTrace);


            findViewById(R.id.connected_layout).setVisibility(View.VISIBLE);
            setupUI(true);
        }else{
            findViewById(R.id.connect_layout).setVisibility(View.VISIBLE);
            setupUI(false);
        }

    }

    private void setupUI(boolean b) {

        if(b){
            logoutBtn.setOnClickListener(v -> signOut());

            List<Score> scoreList=new ArrayList<>();
            ScoresAdapter adapter=new ScoresAdapter(scoreList);

            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(adapter);

            refreshLayout.setOnRefreshListener(() -> {

                scoreList.clear();
                findViewById(R.id.default_item).setVisibility(View.GONE);
                refreshLayout.setRefreshing(true);
                FirebaseFirestore.getInstance().collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("Scores")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {

                            if(!queryDocumentSnapshots.isEmpty()) {

                                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        refreshLayout.setRefreshing(false);
                                        Score score = doc.getDocument().toObject(Score.class);
                                        scoreList.add(score);
                                        adapter.notifyDataSetChanged();

                                    }

                                }

                                if (scoreList.size() == 0) {

                                    refreshLayout.setRefreshing(false);
                                    findViewById(R.id.default_item).setVisibility(View.VISIBLE);
                                    findViewById(R.id.default_item).setAlpha(0.0f);
                                    findViewById(R.id.default_item).animate()
                                            .setDuration(300)
                                            .alpha(1.0f)
                                            .start();

                                }

                            }else{

                                refreshLayout.setRefreshing(false);
                                findViewById(R.id.default_item).setVisibility(View.VISIBLE);
                                findViewById(R.id.default_item).setAlpha(0.0f);
                                findViewById(R.id.default_item).animate()
                                        .setDuration(300)
                                        .alpha(1.0f)
                                        .start();

                            }

                        })
                        .addOnFailureListener(Throwable::printStackTrace);

            });

            scoreList.clear();
            findViewById(R.id.default_item).setVisibility(View.GONE);
            refreshLayout.setRefreshing(true);
            FirebaseFirestore.getInstance().collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("Scores")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if(!queryDocumentSnapshots.isEmpty()) {

                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    refreshLayout.setRefreshing(false);
                                    Score score = doc.getDocument().toObject(Score.class);
                                    scoreList.add(score);
                                    adapter.notifyDataSetChanged();

                                }

                            }

                            if (scoreList.size() == 0) {

                                refreshLayout.setRefreshing(false);
                                findViewById(R.id.default_item).setVisibility(View.VISIBLE);
                                findViewById(R.id.default_item).setAlpha(0.0f);
                                findViewById(R.id.default_item).animate()
                                        .setDuration(300)
                                        .alpha(1.0f)
                                        .start();

                            }

                        }else{

                            refreshLayout.setRefreshing(false);
                            findViewById(R.id.default_item).setVisibility(View.VISIBLE);
                            findViewById(R.id.default_item).setAlpha(0.0f);
                            findViewById(R.id.default_item).animate()
                                    .setDuration(300)
                                    .alpha(1.0f)
                                    .start();

                        }

                    })
                    .addOnFailureListener(Throwable::printStackTrace);

        }else{

            signin.setOnClickListener(v -> signIn());

        }

    }

    public void signIn()
    {
        mDialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut(){

        new MaterialDialog.Builder(this)
                .title("Logout")
                .content("Are you sure do you want to logout?")
                .positiveText("Yes")
                .negativeText("No")
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    mDialog.show();
                    mGoogleSignInClient.signOut()
                            .addOnSuccessListener(aVoid -> {

                                mAuth.signOut();
                                mDialog.dismiss();
                                Glide.with(getApplicationContext())
                                        .load(R.mipmap.user)
                                        .into(pic);

                                findViewById(R.id.connected_layout).animate()
                                        .setDuration(300)
                                        .alpha(0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                findViewById(R.id.connected_layout).setVisibility(View.GONE);
                                                findViewById(R.id.connect_layout).setVisibility(View.VISIBLE);
                                                setupUI(false);
                                            }
                                        })
                                        .start();

                            })
                            .addOnFailureListener(e -> {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Error logging out.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG,e.getLocalizedMessage());
                            });
                })
                .onNegative((dialog, which) -> {
                    dialog.dismiss();

                })
                .show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
                try {
                    firebaseAuthWithPlayGames(signedInAccount);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                mDialog.dismiss();
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }



    }

    private void firebaseAuthWithPlayGames(GoogleSignInAccount acct) {
        AuthCredential credential = PlayGamesAuthProvider.getCredential(acct.getServerAuthCode());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        FirebaseFirestore.getInstance().collection("Users")
                                .document(user.getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    if (!documentSnapshot.exists()) {

                                        Map<String,Object> userMap=new HashMap<>();
                                        userMap.put("id",user.getUid());
                                        userMap.put("name",acct.getDisplayName());
                                        userMap.put("gplay_name",user.getDisplayName());
                                        userMap.put("picture",acct.getPhotoUrl().toString().replace("s96-c","s200-c"));
                                        userMap.put("email",acct.getEmail());
                                        userMap.put("points","0");

                                        points.setText("Points scored : 0");
                                        getSharedPreferences("Trivia", Context.MODE_PRIVATE).edit().putString("points","0").apply();

                                        FirebaseFirestore.getInstance().collection("Users")
                                                .document(user.getUid())
                                                .set(userMap)
                                                .addOnSuccessListener(aVoid -> {
                                                    mDialog.dismiss();
                                                    setupUI(true);
                                                    Glide.with(getApplicationContext())
                                                            .setDefaultRequestOptions(new RequestOptions().dontAnimate())
                                                            .setDefaultRequestOptions(new RequestOptions().placeholder(R.mipmap.user))
                                                            .load(documentSnapshot.getString("picture"))
                                                            .into(pic);
                                                    name.setText(documentSnapshot.getString("name"));
                                                    g_name.setText(documentSnapshot.getString("gplay_name"));
                                                    findViewById(R.id.connect_layout).animate()
                                                            .setDuration(300)
                                                            .alpha(0f)
                                                            .setListener(new AnimatorListenerAdapter() {
                                                                @Override
                                                                public void onAnimationEnd(Animator animation) {
                                                                    super.onAnimationEnd(animation);
                                                                    findViewById(R.id.connect_layout).setVisibility(View.GONE);
                                                                    findViewById(R.id.connected_layout).setVisibility(View.VISIBLE);
                                                                }
                                                            })
                                                            .start();

                                                })
                                                .addOnFailureListener(e -> {
                                                    mDialog.dismiss();
                                                    Log.e(TAG,e.getLocalizedMessage());
                                                });

                                    } else {

                                        mDialog.dismiss();
                                        setupUI(true);
                                        Glide.with(getApplicationContext())
                                                .setDefaultRequestOptions(new RequestOptions().dontAnimate())
                                                .setDefaultRequestOptions(new RequestOptions().placeholder(R.mipmap.user))
                                                .load(documentSnapshot.getString("picture"))
                                                .into(pic);
                                        name.setText(documentSnapshot.getString("name"));
                                        g_name.setText(documentSnapshot.getString("gplay_name"));
                                        findViewById(R.id.connect_layout).animate()
                                                .setDuration(300)
                                                .alpha(0f)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                        findViewById(R.id.connect_layout).setVisibility(View.GONE);
                                                        findViewById(R.id.connected_layout).setVisibility(View.VISIBLE);
                                                    }
                                                })
                                                .start();
                                        String point=documentSnapshot.getString("points");
                                        points.setText(String.format("Points scored : %s", point));
                                        getSharedPreferences("Trivia", Context.MODE_PRIVATE).edit().putString("points",point).apply();


                                    }

                                });

                    }else{
                        mDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e -> {
                    mDialog.dismiss();
                    Log.e(TAG, e.getLocalizedMessage());
                });

    }


}
