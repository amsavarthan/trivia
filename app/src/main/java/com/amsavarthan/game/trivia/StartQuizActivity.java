package com.amsavarthan.game.trivia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.game.trivia.models.Category;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StartQuizActivity extends AppCompatActivity {

    private static List<Category> categories;
    private String category_id,category_name,bg_color,image;

    public static void startActivity(@NonNull Context context, String id, String category, String color, String image){
       Intent intent=new Intent(context,StartQuizActivity.class)
               .putExtra("category_id",id)
               .putExtra("category",category)
               .putExtra("bg_color",color)
               .putExtra("image",image);

       context.startActivity(intent);

    }

    private static void setCategories() {

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

    }

    public static void startActivity(@NonNull Context context, boolean random){
        if(random) {

            categories=new ArrayList<>();
            setCategories();
            Random r=new Random();
            int id= r.nextInt(18);

            Intent intent = new Intent(context, StartQuizActivity.class)
                    .putExtra("category_id", String.valueOf(categories.get(id).getId()))
                    .putExtra("category", categories.get(id).getText())
                    .putExtra("bg_color", categories.get(id).getColor())
                    .putExtra("image", categories.get(id).getImage());
            context.startActivity(intent);
            ((AppCompatActivity)context).finish();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);

        RelativeLayout layout = findViewById(R.id.layout);
        ImageView imageView = findViewById(R.id.category_image);
        TextView textView=findViewById(R.id.text);
        FloatingActionButton fab=findViewById(R.id.fab);

        category_id=getIntent().getStringExtra("category_id");
        category_name=getIntent().getStringExtra("category");
        image=getIntent().getStringExtra("image");
        bg_color=getIntent().getStringExtra("bg_color");

        layout.setBackgroundColor(Color.parseColor(bg_color));
        textView.setText(category_name);
        Glide.with(this)
                .load(image)
                .into(imageView);

        fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up));
        fab.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    public void onFabClick(View view) {

        new MaterialDialog.Builder(this)
                .title("Start Quiz")
                .content("Select the number of questions to ask")
                .items(R.array.number)
                .itemsCallbackSingleChoice(0, (dialog, itemView, which, text) -> {
                    QuizActivity.startActivity(view.getContext(),category_id,category_name,bg_color,image,text.toString());
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    return true;
                })
                .positiveText("Start")
                .show();

    }



}
