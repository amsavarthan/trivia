package com.amsavarthan.game.trivia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsavarthan.game.trivia.R;
import com.amsavarthan.game.trivia.models.Score;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import java.util.List;

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.MyViewHolder> {

    private List<Score> scoreList;
    private Context context;

    public ScoresAdapter(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView time,total,correct,incorrect,missed,category;

        MyViewHolder(View view) {
            super(view);
            category=view.findViewById(R.id.category);
            time =view.findViewById(R.id.time);
            total =view.findViewById(R.id.total_question);
            correct =view.findViewById(R.id.correct);
            incorrect =view.findViewById(R.id.incorrect);
            missed =view.findViewById(R.id.missed);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        context=parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Score score = scoreList.get(position);

        holder.time.setText(String.format(context.getString(R.string.played_time), TimeAgo.using(Long.valueOf(score.getTimestamp()))));
        holder.total.setText(String.format(context.getString(R.string.total_questions_asked),score.getQuestions_asked()));
        holder.correct.setText(String.format("Correct : %s",score.getCorrect()));
        holder.incorrect.setText(String.format("Incorrect : %s",score.getIncorrect()));
        holder.missed.setText(String.format("Missed : %s",score.getMissed()));
        holder.category.setText(score.getCategory());
        holder.category.setTextColor(getColor(score.getCategory()));

    }

    private int getColor(String category) {

        switch (category){

            case "Animals":

                return Color.parseColor("#6d4c41");
            case "Art":

                return Color.parseColor("#5e35b1");
            case "Books":

                return Color.parseColor("#f4511e");
            case "Celebrities":

                return Color.parseColor("#fdd835");
            case "Comics":

                return Color.parseColor("#e53935");
            case "Computers":

                return Color.parseColor("#546e7a");
            case "Film":

                return Color.parseColor("#5e35b1");
            case "Games":

                return Color.parseColor("#3949ab");
            case "General Knowledge":

                return Color.parseColor("#ffb300");
            case "Geography":

                return Color.parseColor("#5d4037");
            case "Music":

                return Color.parseColor("#ad1457");
            case "History":

                return Color.parseColor("#0097a7");
            case "Mathematics":

                return Color.parseColor("#0097a7");
            case "Mythology":

                return Color.parseColor("#303f9f");
            case "Science & Nature":

                return Color.parseColor("#d81b60");
            case "Sports":

                return Color.parseColor("#7cb342");
            case "Television":

                return Color.parseColor("#757575");
            case "Vehicles":

                return Color.parseColor("#fb8c00");
            default:
                return Color.parseColor("#212121");

        }

    }


    @Override
    public int getItemCount() {
        return scoreList.size();
    }

}