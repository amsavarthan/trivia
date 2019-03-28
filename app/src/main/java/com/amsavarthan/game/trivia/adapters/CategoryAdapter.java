package com.amsavarthan.game.trivia.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsavarthan.game.trivia.R;
import com.amsavarthan.game.trivia.StartQuizActivity;
import com.amsavarthan.game.trivia.models.Category;
import com.bumptech.glide.Glide;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<Category> categoryList;
    private Context context;
    private Activity activity;

    public CategoryAdapter(List<Category> categoryList,Activity activity) {
        this.categoryList = categoryList;
        this.activity=activity;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public LinearLayout layout;
        public ImageView image;
        public CardView cardView;

        MyViewHolder(View view) {
            super(view);
            text=view.findViewById(R.id.text);
            layout=view.findViewById(R.id.layout);
            image=view.findViewById(R.id.image);
            cardView=view.findViewById(R.id.card);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        context=parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Category category = categoryList.get(position);
        holder.layout.setBackgroundColor(Color.parseColor(category.getColor()));

        Glide.with(context)
                .load(category.getImage())
                .into(holder.image);

        holder.text.setText(category.getText());
        holder.cardView.setOnClickListener(v -> {
            StartQuizActivity.startActivity(context,String.valueOf(category.getId()),category.getText(),category.getColor(),category.getImage());
            activity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

}