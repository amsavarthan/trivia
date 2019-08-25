package com.amsavarthan.game.trivia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amsavarthan.game.trivia.models.Category;
import com.amsavarthan.game.trivia.adapters.CategoryAdapter;
import com.amsavarthan.game.trivia.models.SessionToken;
import com.amsavarthan.game.trivia.utils.Constants;
import com.amsavarthan.game.trivia.utils.DBManager;
import com.amsavarthan.game.trivia.utils.DatabaseHelper;
import com.amsavarthan.game.trivia.utils.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import shortbread.Shortbread;
import shortbread.Shortcut;

public class MainActivity extends AppCompatActivity {

    private List<Category> categories;
    private List<SessionToken> sessionTokenList;
    private CategoryAdapter categoryAdapter;
    private ProgressDialog pDialog,pDialog2;
    private final String TAG=MainActivity.class.getSimpleName();
    private String token;
    private DBManager dbManager;

    @Shortcut(id = "play_random", icon = R.drawable.ic_shortcut_play_arrow, shortLabel = "Quick Play")
    public void playRandom() {

        StartQuizActivity.startActivity(this,true);

    }

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiDex.install(this);

        sharedPreferences=getSharedPreferences("Trivia",MODE_PRIVATE);

        if(sharedPreferences.getBoolean("firstRun",true)){
            startActivity(new Intent(this,AppIntro.class));
            finish();
            return;
        }

        Shortbread.create(this);
        categories=new ArrayList<>();
        sessionTokenList=new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recylerview);

        categoryAdapter = new CategoryAdapter(categories,this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(categoryAdapter);

        setCategories();

        dbManager = new DBManager(this);
        dbManager.open();

        try {
            Cursor cursor = dbManager.get();
            token = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOKEN));

            if (!cursor.isClosed()) {
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(TextUtils.isEmpty(token))
            new GetSessionToken().execute();

    }

    private void setCategories() {

        Category category=new Category("Animals","#6d4c41","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fanimals.png?alt=media&token=1f3a8a1a-a5af-43a3-8be1-650d7286a6c3",27);
        categories.add(category);

        category=new Category("Art","#5e35b1","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fart.png?alt=media&token=578806b0-d8b0-41f3-b66e-54c1b8df3e36",25);
        categories.add(category);

        category=new Category("Books","#f4511e","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fe_books.png?alt=media&token=3d6ac023-8d10-4f86-bbb0-93d216898a72",10);
        categories.add(category);

        category=new Category("Celebrities","#fdd835","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fcelebrity.png?alt=media&token=d08e6af5-97f4-41aa-8b4a-cce05b4ccc04",26);
        categories.add(category);

        category=new Category("Comics","#e53935","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fcomics.png?alt=media&token=380e99ea-3eb0-4743-bc9d-852e8821b662",29);
        categories.add(category);

        category=new Category("Computers","#546e7a","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fcomputer.png?alt=media&token=c7c71fad-c14d-43d3-925c-bf17c188a1a9",18);
        categories.add(category);

        category=new Category("Film","#5e35b1","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fe_movie.png?alt=media&token=8fb634b1-cf6b-43d9-b733-47c2450d65c1",11);
        categories.add(category);

        category=new Category("Games","#3949ab","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fe_games.png?alt=media&token=66ab03da-96f8-4617-a92a-6eb6c5b42c08",15);
        categories.add(category);

        category=new Category("General Knowledge","#ffb300","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fgeneral_knowledge.png?alt=media&token=0c36beb4-489f-4929-8fa5-b9feac3f1fbe",9);
        categories.add(category);

        category=new Category("Geography","#5d4037","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fgeography.png?alt=media&token=fd5fbd58-173f-4500-9905-b8726b7bb624",22);
        categories.add(category);

        category=new Category("Music","#ad1457","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fe_music.png?alt=media&token=0f19db8e-e3d6-4de9-87c7-e42d47308c94",12);
        categories.add(category);

        category=new Category("History","#0097a7","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fhistory.png?alt=media&token=e6023b1a-3eef-460b-8c1e-9dc577e330da",23);
        categories.add(category);

        category=new Category("Mathematics","#0097a7","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fmaths.png?alt=media&token=c3fde85b-e36f-4655-91ea-91730ac17725",19);
        categories.add(category);

        category=new Category("Mythology","#303f9f","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fmythology.png?alt=media&token=8d7ff9bd-d8b4-4620-ae8b-4fe91e730a4f",20);
        categories.add(category);

        category=new Category("Science & Nature","#d81b60","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fscience.png?alt=media&token=440eda1a-8222-42d4-a586-826cb4b25779",17);
        categories.add(category);

        category=new Category("Sports","#7cb342","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fsports.png?alt=media&token=b7c5871d-cbb3-4bb0-b507-62217b06c37f",21);
        categories.add(category);

        category=new Category("Television","#757575","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fe_tv.png?alt=media&token=13f7eabf-c352-4d31-b119-96f347ba8a7a",14);
        categories.add(category);

        category=new Category("Vehicles","#fb8c00","https://firebasestorage.googleapis.com/v0/b/trivia-c135b.appspot.com/o/categories%2Fvehicle.png?alt=media&token=63602745-d5fb-48de-b191-c2dd12f79be5",28);
        categories.add(category);

        categoryAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        try {
            dbManager.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_account:

                Intent intent = new Intent(this,Account.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

                return true;
            case R.id.action_generate:

                new ResetSessionToken().execute();

                return true;

            case R.id.action_intro:

                Intent intent1 = new Intent(this,AppRepeatIntro.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

                return true;

            case R.id.action_policy:

                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://lvamsavarthan.github.io/lvstore/trivia_privacy_policy.html"));
                startActivity(i);

                return true;

            case R.id.action_about:

                Intent intent2 = new Intent(this,About.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetSessionToken extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Error connecting to the internet.",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });


                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Error connecting to the internet.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            switch (sessionTokenList.get(sessionTokenList.size()-1).getResult_code()){
                case "0":
                    dbManager.insert(sessionTokenList.get(sessionTokenList.size()-1).getToken());
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
            pDialog2 = new ProgressDialog(MainActivity.this);
            pDialog2.setMessage("Please wait...");
            pDialog2.setCancelable(false);
            pDialog2.show();

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
            // Dismiss the progress dialog
            if (pDialog2.isShowing())
                pDialog2.dismiss();

            switch (sessionTokenList.get(sessionTokenList.size()-1).getResult_code()){
                case "0":
                    dbManager.update(1, sessionTokenList.get(sessionTokenList.size()-1).getToken());
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
