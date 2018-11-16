package com.amsavarthan.trivia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import com.amsavarthan.trivia.models.Category;
import com.amsavarthan.trivia.adapters.CategoryAdapter;
import com.amsavarthan.trivia.models.SessionToken;
import com.amsavarthan.trivia.utils.Constants;
import com.amsavarthan.trivia.utils.DBManager;
import com.amsavarthan.trivia.utils.DatabaseHelper;
import com.amsavarthan.trivia.utils.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiDex.install(this);
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

        Category category=new Category("Animals","#6d4c41","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fanimals.png?alt=media&token=6eefc16e-9d1e-4d76-a6c6-f38503febfad",27);
        categories.add(category);

        category=new Category("Art","#5e35b1","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fart.png?alt=media&token=1b295302-a2a9-45a4-b489-f0eb4c3ee2bd",25);
        categories.add(category);

        category=new Category("Books","#f4511e","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fe_books.png?alt=media&token=10aff2d9-d76a-4bf4-b0f6-51cdcef60f80",10);
        categories.add(category);

        category=new Category("Celebrities","#fdd835","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fcelebrity.png?alt=media&token=39d991f7-15ff-4764-afbd-4a2407727074",26);
        categories.add(category);

        category=new Category("Comics","#e53935","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fcomics.png?alt=media&token=ac120fe9-e348-40f3-a90d-862ce2e3416c",29);
        categories.add(category);

        category=new Category("Computers","#546e7a","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fcomputer.png?alt=media&token=212f607b-f3f9-4e58-bba0-c813575c1b11",18);
        categories.add(category);

        category=new Category("Film","#5e35b1","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fe_movie.png?alt=media&token=1c95e469-0769-42ef-aca5-b7380c1e0965",11);
        categories.add(category);

        category=new Category("Games","#3949ab","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fe_games.png?alt=media&token=c35a5777-9a10-4382-8e9a-7ff883f9f3ea",15);
        categories.add(category);

        category=new Category("General Knowledge","#ffb300","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fgeneral_knowledge.png?alt=media&token=db35172c-7e49-4a9e-9eb2-e38da332b3fe",9);
        categories.add(category);

        category=new Category("Geography","#5d4037","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fgeography.png?alt=media&token=f7b2f9d8-b2ac-41fa-96a8-7df573a73639",22);
        categories.add(category);

        category=new Category("Music","#ad1457","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fe_music.png?alt=media&token=7743ab0e-6d11-4ae3-be22-a507a87db8f2",12);
        categories.add(category);

        category=new Category("History","#0097a7","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fhistory.png?alt=media&token=57d17265-1dc1-4be6-a1a1-edad47706d1e",23);
        categories.add(category);

        category=new Category("Mathematics","#0097a7","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fmaths.png?alt=media&token=643319d1-b182-4c9e-bf0f-79fe089d78dc",19);
        categories.add(category);

        category=new Category("Mythology","#303f9f","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fmythology.png?alt=media&token=87cd5a2b-06bd-4660-91c3-21f3e2255f44",20);
        categories.add(category);

        category=new Category("Science & Nature","#d81b60","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fscience.png?alt=media&token=7fa8b68e-8b74-4d67-a885-e4e18cda1cf7",17);
        categories.add(category);

        category=new Category("Sports","#7cb342","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fsports.png?alt=media&token=d237ee96-39ac-47e0-b7ae-e335fcbc1e4f",21);
        categories.add(category);

        category=new Category("Television","#757575","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fe_tv.png?alt=media&token=9be9719a-fbb7-47bb-8ebe-a22227325a77",14);
        categories.add(category);

        category=new Category("Vehicles","#fb8c00","https://firebasestorage.googleapis.com/v0/b/trivia-202cd.appspot.com/o/categories%2Fvehicle.png?alt=media&token=677f6208-46c6-43e9-b123-17795dcb3497",28);
        categories.add(category);

        categoryAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        dbManager.close();
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class GetSessionToken extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting session token...");
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
