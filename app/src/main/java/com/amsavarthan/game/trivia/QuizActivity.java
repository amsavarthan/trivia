package com.amsavarthan.game.trivia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.game.trivia.models.BooleanQuestion;
import com.amsavarthan.game.trivia.models.MultipleQuestion;
import com.amsavarthan.game.trivia.models.SessionToken;
import com.amsavarthan.game.trivia.utils.Constants;
import com.amsavarthan.game.trivia.utils.DBManager;
import com.amsavarthan.game.trivia.utils.DatabaseHelper;
import com.amsavarthan.game.trivia.utils.HttpHandler;
import com.danimahardhika.cafebar.CafeBar;
import com.danimahardhika.cafebar.CafeBarTheme;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;

public class QuizActivity extends AppCompatActivity {

    private final String TAG=QuizActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private String category_id,category_name, bg_color,image;
    private TextView question_text,true_text,false_text;
    private List<MultipleQuestion> mQuestionList;
    private List<BooleanQuestion> bQuestionList;
    private DBManager dbManager;
    private List<String> answersList;
    private Animation fade_in,fade_out,scale_up,scale_down;
    private View t_check,f_check;
    private FloatingActionButton fab,r_fab,c_fab;
    private CardView a1,a2,a3,a4;
    private TextView a1_text,a2_text,a3_text,a4_text;
    private String selected_answer;
    private String q_type;
    private boolean error,question_loaded;
    private String full_error_log;
    private String response_code;
    private List<SessionToken> sessionTokenList;
    private ProgressBar progressbar;
    private LinearTimerView timerView;
    private LinearTimer timer;
    private int question_number=1;
    private FrameLayout layout;
    private String question_number_max;
    private int correct=0,incorrect=0,missed_count=0;

    private static final int RC_SIGN_IN = 234;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    private ProgressDialog mDialog;

    public static void startActivity(@NonNull Context context, String id, String category, String color, String image,String max){
        Intent intent=new Intent(context,QuizActivity.class)
                .putExtra("category_id",id)
                .putExtra("category",category)
                .putExtra("bg_color",color)
                .putExtra("image",image)
                .putExtra("max",max);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mAuth=FirebaseAuth.getInstance();
        mFirestore=FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mQuestionList=new ArrayList<>();
        bQuestionList=new ArrayList<>();
        dbManager=new DBManager(this);
        dbManager.open();
        selected_answer=null;
        question_loaded=false;
        sessionTokenList=new ArrayList<>();

        layout=findViewById(R.id.layout);
        progressbar=findViewById(R.id.progressbar);
        question_text=findViewById(R.id.question);
        true_text=findViewById(R.id.true_text);
        false_text=findViewById(R.id.false_text);
        t_check=findViewById(R.id.t_check);
        f_check=findViewById(R.id.f_check);
        fab=findViewById(R.id.fab);
        r_fab=findViewById(R.id.fab_retry);
        c_fab=findViewById(R.id.fab_close);
        a1=findViewById(R.id.a1);
        a2=findViewById(R.id.a2);
        a3=findViewById(R.id.a3);
        a4=findViewById(R.id.a4);
        a1_text=findViewById(R.id.a1_text);
        a2_text=findViewById(R.id.a2_text);
        a3_text=findViewById(R.id.a3_text);
        a4_text=findViewById(R.id.a4_text);
        timerView=findViewById(R.id.timer);
        timer = new LinearTimer.Builder()
                .linearTimerView(timerView)
                .duration(15000)//15 sec
                .timerListener(new LinearTimer.TimerListener() {
                    @Override
                    public void animationComplete() {
                        if(question_loaded)
                            validateAnswers();
                    }

                    @Override
                    public void timerTick(long tickUpdateInMillis) {

                    }

                    @Override
                    public void onTimerReset() {

                    }
                })
                .build();

        answersList = new ArrayList<>();
        category_id=getIntent().getStringExtra("category_id");
        category_name=getIntent().getStringExtra("category");
        image=getIntent().getStringExtra("image");
        bg_color=getIntent().getStringExtra("bg_color");
        question_number_max=getIntent().getStringExtra("max");

        layout.setBackgroundColor(Color.parseColor(bg_color));
        timerView.setInitialColor(Color.parseColor(bg_color));
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        scale_up = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scale_down = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(bg_color)));
        fab.hide();
        r_fab.hide();
        c_fab.hide();

        mDialog=new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        timer.startTimer();
        new GetAQuestion().execute();

    }

    public void checkAnswer(View view) {
        validateAnswers();
    }

    public void validateAnswers(){

        String[] appreciate={"You are correct!","That's correct!","You are a genius!","Wow, that's was the correct answer!","You made the correct choice","You are right!","It is the correct answer"};
        String[] depreciate={"You are wrong,","That's is not the correct answer,","What?, that's not the answer.","You are wrong,"};
        String[] missed={"Time's out","Time's up","You didn't answer the question within given time","You missed to select an answer","Hey! what were you doing","You missed it","OMG! you didn't select an answer"};


        if (q_type.equals("multiple")) {

            String correct_ans = decodeBase64(mQuestionList.get(mQuestionList.size() - 1).getCorrect_answer());

            if(TextUtils.isEmpty(selected_answer)) {

                missed_count++;
                CafeBar.builder(this)
                        .content((missed[new Random().nextInt(missed.length)]) + ", Correct answer was " + correct_ans)
                        .theme(CafeBarTheme.Custom(Color.parseColor("#f44336")))
                        .duration(CafeBar.Duration.MEDIUM)
                        .floating(true)
                        .show();

            }else{
                if (selected_answer.toLowerCase().equals(correct_ans.toLowerCase())) {

                    correct++;

                    CafeBar.builder(this)
                            .content((appreciate[new Random().nextInt(appreciate.length)]))
                            .theme(CafeBarTheme.Custom(Color.parseColor("#4caf50")))
                            .floating(true)
                            .duration(CafeBar.Duration.MEDIUM)
                            .show();
                }else {

                    incorrect++;
                    CafeBar.builder(this)
                            .content((depreciate[new Random().nextInt(depreciate.length)])+" Correct answer was " + correct_ans)
                            .theme(CafeBarTheme.Custom(Color.parseColor("#f44336")))
                            .duration(CafeBar.Duration.MEDIUM)
                            .floating(true)
                            .show();

                }
            }

        } else {

            String correct_ans = decodeBase64(bQuestionList.get(bQuestionList.size() - 1).getCorrect_answer());

            if(TextUtils.isEmpty(selected_answer)){

                missed_count++;
                CafeBar.builder(this)
                        .content((missed[new Random().nextInt(missed.length)])+", Correct answer was " + correct_ans)
                        .theme(CafeBarTheme.Custom(Color.parseColor("#f44336")))
                        .duration(CafeBar.Duration.MEDIUM)
                        .floating(true)
                        .show();

            }else{
                if (selected_answer.toLowerCase().equals(correct_ans.toLowerCase())) {

                    correct++;
                    CafeBar.builder(this)
                            .content((appreciate[new Random().nextInt(appreciate.length)]))
                            .theme(CafeBarTheme.Custom(Color.parseColor("#4caf50")))
                            .floating(true)
                            .duration(CafeBar.Duration.MEDIUM)
                            .show();

                } else {

                    incorrect++;
                    CafeBar.builder(this)
                            .content((depreciate[new Random().nextInt(depreciate.length)])+" Correct answer was " + correct_ans)
                            .theme(CafeBarTheme.Custom(Color.parseColor("#f44336")))
                            .duration(CafeBar.Duration.MEDIUM)
                            .floating(true)
                            .show();

                }
            }

        }

        if(question_number==Integer.valueOf(question_number_max)) {
            if(fab.isShown()){
                fab.hide();
            }
            timerView.setVisibility(View.GONE);
            c_fab.show();
            TextView result=findViewById(R.id.result);
            result.setText(String.format("%s/%s",String.valueOf(correct),question_number_max));
            if (findViewById(R.id.boolean_layout).getVisibility() == View.VISIBLE)
                findViewById(R.id.boolean_layout).setVisibility(View.GONE);
            if (findViewById(R.id.multiple_layout).getVisibility() == View.VISIBLE)
                findViewById(R.id.multiple_layout).setVisibility(View.GONE);
            if (findViewById(R.id.result_layout).getVisibility() == View.GONE)
                findViewById(R.id.result_layout).setVisibility(View.VISIBLE);
            layout.setBackgroundColor(Color.parseColor("#689F38"));
            question_text.startAnimation(fade_in);
            question_text.setText("Let's see what you have got");
            progressbar.setVisibility(View.INVISIBLE);
            try{
                timer.pauseTimer();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }else if(question_number==Integer.valueOf(question_number_max)-1) {
            question_number++;
            question_text.startAnimation(fade_in);
            question_text.setText("Getting your last question...");
            progressbar.setVisibility(View.VISIBLE);
            timer.restartTimer();
            new GetAQuestion().execute();
        }else{
            question_number++;
            question_text.startAnimation(fade_in);
            question_text.setText("Getting next question...");
            progressbar.setVisibility(View.VISIBLE);
            timer.restartTimer();
            new GetAQuestion().execute();
        }

    }

    @Override
    public void onBackPressed() {

        if(question_number==Integer.valueOf(question_number_max)) {

            FirebaseUser user=mAuth.getCurrentUser();
            if(user==null){

                new MaterialDialog.Builder(this)
                        .title("No account found")
                        .content("Connect with your Google account to upload your scores and compete with your friends")
                        .positiveText("Connect and Upload")
                        .negativeText("No Thanks")
                        .cancelable(false)
                        .canceledOnTouchOutside(false)
                        .onPositive((dialog, which) -> {
                            dialog.dismiss();
                            mDialog.show();
                            signIn();
                        })
                        .onNegative((dialog, which) -> {
                            dialog.dismiss();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        })
                        .show();

            }else{
                uploadScores();
            }

        }

        if(question_loaded){

            try {
                timer.pauseTimer();

                new MaterialDialog.Builder(this)
                        .title("Game is on")
                        .content("Are you sure do you want to go back?")
                        .positiveText("Yes")
                        .onPositive((dialog, which) -> {
                            dialog.dismiss();
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        })
                        .negativeText("No")
                        .onNegative((dialog, which) -> {
                            dialog.dismiss();
                            try{
                                timer.resumeTimer();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                                Toast.makeText(QuizActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .show();

            } catch (IllegalStateException e) {
                e.printStackTrace();
                Toast.makeText(QuizActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }else {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public void checkNetwork(View view) {
        new GetAQuestion().execute();
    }

    public void sendReport(View view) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"amsavarthan.a@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Trivia app report : "+ Build.MODEL+" (SDK:"+Build.VERSION.SDK_INT+")");
        i.putExtra(Intent.EXTRA_TEXT   , "Log: "+full_error_log);
        try {
            startActivity(Intent.createChooser(i, "Send mail using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(QuizActivity.this, "There are no email applications installed.", Toast.LENGTH_SHORT).show();
        }

    }

    public void completeQuiz(View view) {

        FirebaseUser user=mAuth.getCurrentUser();
        if(user==null){

            new MaterialDialog.Builder(this)
                    .title("No account found")
                    .content("Connect with your Google account to upload your scores and compete with your friends")
                    .positiveText("Connect and Upload")
                    .negativeText("No Thanks")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .onPositive((dialog, which) -> {
                        dialog.dismiss();
                        mDialog.show();
                        signIn();
                    })
                    .onNegative((dialog, which) -> {
                        dialog.dismiss();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    })
                    .show();

        }else{
            uploadScores();
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        FirebaseFirestore.getInstance().collection("Users")
                                .document(user.getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    if (!documentSnapshot.exists()) {

                                        Map<String,Object> userMap=new HashMap<>();
                                        userMap.put("id",user.getUid());
                                        userMap.put("name",user.getDisplayName());
                                        userMap.put("picture",user.getPhotoUrl().toString().replace("s96-c","s200-c"));
                                        userMap.put("email",user.getEmail());
                                        userMap.put("points","0");

                                        FirebaseFirestore.getInstance().collection("Users")
                                                .document(user.getUid())
                                                .set(userMap)
                                                .addOnSuccessListener(aVoid -> {
                                                    mDialog.dismiss();
                                                    uploadScores();
                                                })
                                                .addOnFailureListener(e -> {
                                                    mDialog.dismiss();
                                                    Log.e(TAG,e.getLocalizedMessage());
                                                });

                                    } else {

                                        Map<String,Object> userMap=new HashMap<>();
                                        userMap.put("id",user.getUid());
                                        userMap.put("name",user.getDisplayName());
                                        userMap.put("picture",user.getPhotoUrl().toString().replace("s96-c","s200-c"));
                                        userMap.put("email",user.getEmail());
                                        userMap.put("points",documentSnapshot.getString("points"));

                                        FirebaseFirestore.getInstance().collection("Users")
                                                .document(user.getUid())
                                                .set(userMap)
                                                .addOnSuccessListener(aVoid -> {
                                                    mDialog.dismiss();
                                                    uploadScores();                                      })
                                                .addOnFailureListener(e -> {
                                                    mDialog.dismiss();
                                                    Log.e(TAG,e.getLocalizedMessage());
                                                });

                                    }

                                });

                    } else {
                        mDialog.dismiss();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());

                        new MaterialDialog.Builder(QuizActivity.this)
                                .title("Authentication failed")
                                .content("Check your network connecting and try again.")
                                .positiveText("Try Again")
                                .negativeText("No Thanks")
                                .cancelable(false)
                                .canceledOnTouchOutside(false)
                                .onPositive((dialog, which) -> {
                                    dialog.dismiss();
                                    mDialog.show();
                                    signIn();
                                })
                                .onNegative((dialog, which) -> {
                                    dialog.dismiss();
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                    finish();
                                })
                                .show();


                    }

                });
    }

    private void uploadScores() {

        mDialog.show();
        Map<String,Object> scoreMap=new HashMap<>();
        scoreMap.put("timestamp",String.valueOf(System.currentTimeMillis()));
        scoreMap.put("incorrect",String.valueOf(incorrect));
        scoreMap.put("correct",String.valueOf(correct));
        scoreMap.put("missed",String.valueOf(missed_count));
        scoreMap.put("category",category_name);
        scoreMap.put("questions_asked",question_number_max);

        mFirestore.collection("Users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("Scores")
                .add(scoreMap)
                .addOnSuccessListener(documentReference -> mFirestore.collection("Users")
                        .document(mAuth.getCurrentUser().getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {

                            HashMap<String,Object> pointsMap=new HashMap<>();
                            int total=(Integer.parseInt(documentSnapshot.getString("points"))+correct)-(missed_count+incorrect);
                            if(total<0) {
                                pointsMap.put("points","0");
                            }else{
                                pointsMap.put("points",String.valueOf(total));
                            }

                            mFirestore.collection("Users")
                                    .document(mAuth.getCurrentUser().getUid())
                                    .update(pointsMap)
                                    .addOnSuccessListener(aVoid -> {
                                        mDialog.dismiss();
                                        Toast.makeText(QuizActivity.this, "Scores updated", Toast.LENGTH_SHORT).show();
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        mDialog.dismiss();
                                        Log.e(TAG,e.getLocalizedMessage());
                                    });

                        }))
                .addOnFailureListener(e -> {
                    mDialog.dismiss();
                    Log.e(TAG,e.getLocalizedMessage());
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                mDialog.dismiss();
                Toast.makeText(QuizActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private class GetAQuestion extends AsyncTask<Void, Void, Void> {


        private String token_id;
        private String[] question_types={"multiple","boolean"};

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                timer.pauseTimer();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
            selected_answer=null;

            hideLayouts();
            progressbar.setIndeterminate(true);

            Cursor cursor = dbManager.get();
            token_id=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TOKEN));

            if(!cursor.isClosed()) {
                cursor.close();
            }

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            q_type = (question_types[new Random().nextInt(question_types.length)]);
            String jsonStr = sh.makeServiceCall(Constants.BASE_URL + "amount=1&token="+token_id+"&category="+category_id+"&type="+q_type+"&encode=base64");

            Log.e(TAG,"jsonStr: "+Constants.BASE_URL + "amount=1&token="+token_id+"&category="+category_id+"&type="+q_type+"&encode=base64");
            Log.e(TAG,"Type: "+q_type);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    response_code = jsonObj.getString("response_code");
                    JSONArray results = jsonObj.getJSONArray("results");

                    JSONObject c = results.getJSONObject(0);

                    if(q_type.equals("multiple")){

                        String category = c.getString("category");
                        String type = c.getString("type");
                        String difficulty = c.getString("difficulty");
                        String question = c.getString("question");
                        String correct_answer = c.getString("correct_answer");
                        JSONArray incorrect_answers = c.getJSONArray("incorrect_answers");

                        mQuestionList.clear();
                        answersList.clear();
                        MultipleQuestion mQuestion = new MultipleQuestion(response_code, category, type, difficulty, question, correct_answer, incorrect_answers.get(0).toString(), incorrect_answers.get(1).toString(), incorrect_answers.get(2).toString());
                        mQuestionList.add(mQuestion);
                        answersList.add(correct_answer);
                        answersList.add(incorrect_answers.get(0).toString());
                        answersList.add(incorrect_answers.get(1).toString());
                        answersList.add(incorrect_answers.get(2).toString());

                        return null;
                    }else if(q_type.equals("boolean")){

                        String category = c.getString("category");
                        String type = c.getString("type");
                        String difficulty = c.getString("difficulty");
                        String question = c.getString("question");
                        String correct_answer = c.getString("correct_answer");
                        JSONArray incorrect_answers = c.getJSONArray("incorrect_answers");

                        bQuestionList.clear();
                        BooleanQuestion bQuestion = new BooleanQuestion(response_code, category, type, difficulty, question, correct_answer, incorrect_answers.get(0).toString());
                        bQuestionList.add(bQuestion);

                        return null;
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            full_error_log="Question parsing error: Code "+ response_code +":"+e.getMessage();
                            error=true;
                            if(!r_fab.isShown()) {
                                r_fab.startAnimation(scale_up);
                                r_fab.show();
                            }
                            if(findViewById(R.id.report_btn).getVisibility()==View.GONE){
                                findViewById(R.id.report_btn).startAnimation(scale_up);
                                findViewById(R.id.report_btn).setVisibility(View.VISIBLE);
                            }
                            findViewById(R.id.layout).startAnimation(fade_out);
                            findViewById(R.id.layout).setVisibility(View.GONE);
                            findViewById(R.id.error_layout).startAnimation(fade_in);
                            findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        error=true;
                        if(!r_fab.isShown()) {
                            r_fab.startAnimation(scale_up);
                            r_fab.show();
                        }
                        if(findViewById(R.id.report_btn).getVisibility()==View.VISIBLE){
                            findViewById(R.id.report_btn).startAnimation(scale_down);
                            findViewById(R.id.report_btn).setVisibility(View.GONE);
                        }
                        findViewById(R.id.report_btn).setVisibility(View.VISIBLE);
                        findViewById(R.id.layout).startAnimation(fade_out);
                        findViewById(R.id.layout).setVisibility(View.GONE);
                        findViewById(R.id.error_layout).startAnimation(fade_in);
                        findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            try {

                if (response_code.equals("3")) {
                    try {
                        timer.pauseTimer();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    new GetSessionToken().execute();
                    if (r_fab.isShown()) {
                        r_fab.startAnimation(scale_down);
                        r_fab.hide();
                    }
                    if (findViewById(R.id.report_btn).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.report_btn).startAnimation(scale_up);
                        findViewById(R.id.report_btn).setVisibility(View.GONE);
                    }
                    findViewById(R.id.layout).startAnimation(fade_in);
                    findViewById(R.id.layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.error_layout).startAnimation(fade_out);
                    findViewById(R.id.error_layout).setVisibility(View.GONE);
                } else if (response_code.equals("4")) {
                    try {
                        timer.pauseTimer();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    new ResetSessionToken().execute();
                    if (r_fab.isShown()) {
                        r_fab.startAnimation(scale_down);
                        r_fab.hide();
                    }
                    if (findViewById(R.id.report_btn).getVisibility() == View.VISIBLE) {
                        findViewById(R.id.report_btn).startAnimation(scale_up);
                        findViewById(R.id.report_btn).setVisibility(View.GONE);
                    }
                    findViewById(R.id.layout).startAnimation(fade_in);
                    findViewById(R.id.layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.error_layout).startAnimation(fade_out);
                    findViewById(R.id.error_layout).setVisibility(View.GONE);

                }

                if (error) {
                    hideLayouts();
                } else {
                    timer.restartTimer();
                    progressbar.setVisibility(View.INVISIBLE);

                    if (r_fab.isShown()) {
                        r_fab.startAnimation(scale_down);
                        r_fab.hide();
                    }

                    question_loaded = true;

                    if (q_type.equals("multiple")) {

                        if (findViewById(R.id.boolean_layout).getVisibility() == View.VISIBLE)
                            findViewById(R.id.boolean_layout).setVisibility(View.GONE);

                        findViewById(R.id.multiple_layout).startAnimation(fade_in);
                        findViewById(R.id.multiple_layout).setVisibility(View.VISIBLE);

                        question_text.startAnimation(fade_in);
                        question_text.setText(decodeBase64(mQuestionList.get(mQuestionList.size() - 1).getQuestion()));
                        Collections.shuffle(answersList);

                        a1_text.setText(decodeBase64(answersList.get(0)));
                        a2_text.setText(decodeBase64(answersList.get(1)));
                        a3_text.setText(decodeBase64(answersList.get(2)));
                        a4_text.setText(decodeBase64(answersList.get(3)));

                        setOnClickListeners("multiple");

                    } else {

                        if (findViewById(R.id.multiple_layout).getVisibility() == View.VISIBLE)
                            findViewById(R.id.multiple_layout).setVisibility(View.GONE);

                        findViewById(R.id.boolean_layout).startAnimation(fade_in);
                        findViewById(R.id.boolean_layout).setVisibility(View.VISIBLE);

                        question_text.startAnimation(fade_in);
                        question_text.setText(decodeBase64(bQuestionList.get(bQuestionList.size() - 1).getQuestion()));

                        setOnClickListeners("boolean");


                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        private void setOnClickListeners(String type) {

            if(type.equals("multiple")) {
                a1.setOnClickListener(v -> {

                    if (!fab.isShown()) {
                        fab.startAnimation(scale_up);
                        fab.show();
                    }

                    if (findViewById(R.id.a2_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a2_check).setVisibility(View.INVISIBLE);
                    if (findViewById(R.id.a3_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a3_check).setVisibility(View.INVISIBLE);
                    if (findViewById(R.id.a4_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a4_check).setVisibility(View.INVISIBLE);

                    findViewById(R.id.a1_check).setVisibility(View.VISIBLE);

                    selected_answer=a1_text.getText().toString();

                });

                a2.setOnClickListener(v -> {

                    if (!fab.isShown()) {
                        fab.startAnimation(scale_up);
                        fab.show();
                    }

                    if (findViewById(R.id.a1_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a1_check).setVisibility(View.INVISIBLE);
                    if (findViewById(R.id.a3_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a3_check).setVisibility(View.INVISIBLE);
                    if (findViewById(R.id.a4_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a4_check).setVisibility(View.INVISIBLE);

                    findViewById(R.id.a2_check).setVisibility(View.VISIBLE);

                    selected_answer=a2_text.getText().toString();

                });

                a3.setOnClickListener(v -> {

                    if (!fab.isShown()) {
                        fab.startAnimation(scale_up);
                        fab.show();
                    }

                    if (findViewById(R.id.a1_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a1_check).setVisibility(View.INVISIBLE);
                    if (findViewById(R.id.a2_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a2_check).setVisibility(View.INVISIBLE);
                    if (findViewById(R.id.a4_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a4_check).setVisibility(View.INVISIBLE);

                    findViewById(R.id.a3_check).setVisibility(View.VISIBLE);

                    selected_answer=a3_text.getText().toString();

                });

                a4.setOnClickListener(v -> {

                    if (!fab.isShown()) {
                        fab.startAnimation(scale_up);
                        fab.show();
                    }

                    if (findViewById(R.id.a1_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a1_check).setVisibility(View.INVISIBLE);
                    if (findViewById(R.id.a2_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a2_check).setVisibility(View.INVISIBLE);
                    if (findViewById(R.id.a3_check).getVisibility() == View.VISIBLE)
                        findViewById(R.id.a3_check).setVisibility(View.INVISIBLE);
                    findViewById(R.id.a4_check).setVisibility(View.VISIBLE);

                    selected_answer=a4_text.getText().toString();

                });
            }else{

                true_text.setOnClickListener(v -> {

                    if(!fab.isShown()) {
                        fab.startAnimation(scale_up);
                        fab.show();
                    }

                    if(f_check.getVisibility()==View.VISIBLE)
                        f_check.setVisibility(View.INVISIBLE);
                    t_check.setVisibility(View.VISIBLE);

                    selected_answer="true";

                });

                false_text.setOnClickListener(v -> {

                    if(!fab.isShown()) {
                        fab.startAnimation(scale_up);
                        fab.show();
                    }

                    if(t_check.getVisibility()==View.VISIBLE)
                        t_check.setVisibility(View.INVISIBLE);
                    f_check.setVisibility(View.VISIBLE);

                    selected_answer="false";

                });

            }

        }

    }

    private void hideLayouts() {

        if(findViewById(R.id.boolean_layout).getVisibility()==View.VISIBLE) {
            findViewById(R.id.boolean_layout).startAnimation(fade_out);
            findViewById(R.id.boolean_layout).setVisibility(View.GONE);
        }
        if(findViewById(R.id.multiple_layout).getVisibility()==View.VISIBLE) {
            findViewById(R.id.multiple_layout).startAnimation(fade_out);
            findViewById(R.id.multiple_layout).setVisibility(View.GONE);
        }
        if (findViewById(R.id.a1_check).getVisibility() == View.VISIBLE)
            findViewById(R.id.a1_check).setVisibility(View.INVISIBLE);
        if (findViewById(R.id.a2_check).getVisibility() == View.VISIBLE)
            findViewById(R.id.a2_check).setVisibility(View.INVISIBLE);
        if (findViewById(R.id.a3_check).getVisibility() == View.VISIBLE)
            findViewById(R.id.a3_check).setVisibility(View.INVISIBLE);
        if (findViewById(R.id.a4_check).getVisibility() == View.VISIBLE)
            findViewById(R.id.a4_check).setVisibility(View.INVISIBLE);
        if(f_check.getVisibility()==View.VISIBLE)
            f_check.setVisibility(View.INVISIBLE);
        if(t_check.getVisibility()==View.VISIBLE)
            t_check.setVisibility(View.INVISIBLE);

        if(fab.isShown()){
            fab.startAnimation(fade_in);
            fab.hide();
        }

    }

    @NonNull
    private String decodeBase64(String coded){
        byte[] valueDecoded= new byte[0];
        try {
            valueDecoded = Base64.decode(coded.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG,e.getMessage());
        }
        return new String(valueDecoded);
    }

    private class GetSessionToken extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(Constants.SESSION_TOKEN_GET);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String response_code = jsonObj.getString("response_code");
                    String token=jsonObj.getString("token");

                    sessionTokenList.clear();
                    SessionToken sessionToken=new SessionToken(response_code,token);
                    sessionTokenList.add(sessionToken);

                    return null;

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(() -> {
                        full_error_log="Session Token parsing error: " + e.getMessage();
                        if(!r_fab.isShown()) {
                            r_fab.startAnimation(scale_up);
                            r_fab.show();
                        }
                        if(findViewById(R.id.report_btn).getVisibility()==View.GONE){
                            findViewById(R.id.report_btn).startAnimation(scale_up);
                            findViewById(R.id.report_btn).setVisibility(View.VISIBLE);
                        }
                        findViewById(R.id.layout).startAnimation(fade_out);
                        findViewById(R.id.layout).setVisibility(View.GONE);
                        findViewById(R.id.error_layout).startAnimation(fade_in);
                        findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(() -> {
                    if(!r_fab.isShown()) {
                        r_fab.startAnimation(scale_up);
                        r_fab.show();
                    }
                    if(findViewById(R.id.report_btn).getVisibility()==View.VISIBLE){
                        findViewById(R.id.report_btn).startAnimation(scale_down);
                        findViewById(R.id.report_btn).setVisibility(View.GONE);
                    }
                    findViewById(R.id.layout).startAnimation(fade_out);
                    findViewById(R.id.layout).setVisibility(View.GONE);
                    findViewById(R.id.error_layout).startAnimation(fade_in);
                    findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            switch (sessionTokenList.get(sessionTokenList.size()-1).getResult_code()){
                case "0":
                    try{
                        dbManager.delete(1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        try {
                            timer.startTimer();
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                        dbManager.insert(sessionTokenList.get(sessionTokenList.size()-1).getToken());
                        new GetAQuestion().execute();
                    }
                    Log.e(TAG,"Code 0: Token Generated");
                    return;
                case "1":
                    Log.e(TAG,"Code 1: No Results");
                    return;
                case "2":
                    Log.e(TAG,"Code 2: Invalid Parameter");
                    return;
                case "3":
                    Log.e(TAG,"Code 3: Token Not Found");
                    return;
                case "4":
                    Log.e(TAG,"Code 4: Token Empty");
            }

        }

    }

    private class ResetSessionToken extends AsyncTask<Void, Void, Void> {

        String token_id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Cursor cursor = dbManager.get();
            token_id=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TOKEN));

            if(!cursor.isClosed()) {
                cursor.close();
            }

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(Constants.SESSION_TOKEN_RESET+token_id);

            Log.e(TAG,"Token: "+token_id);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String response_code = jsonObj.getString("response_code");
                    String token=jsonObj.getString("token");

                    sessionTokenList.clear();
                    SessionToken sessionToken=new SessionToken(response_code,token);
                    sessionTokenList.add(sessionToken);

                    return null;

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            "Error connecting to the internet.",
                            Toast.LENGTH_LONG)
                            .show());


                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                        "Error connecting to the internet.",
                        Toast.LENGTH_LONG)
                        .show());


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            switch (sessionTokenList.get(sessionTokenList.size()-1).getResult_code()){
                case "0":
                    try{
                        dbManager.update(1,sessionTokenList.get(sessionTokenList.size()-1).getToken());
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        try {
                            timer.startTimer();
                            new GetAQuestion().execute();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    Log.e(TAG,"Code 0: Token Generated");
                    return;
                case "1":
                    Log.e(TAG,"Code 1: No Results");
                    return;
                case "2":
                    Log.e(TAG,"Code 2: Invalid Parameter");
                    return;
                case "3":
                    Log.e(TAG,"Code 3: Token Not Found");
                    return;
                case "4":
                    Log.e(TAG,"Code 4: Token Empty");
            }

        }

    }


}
