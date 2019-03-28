package com.amsavarthan.game.trivia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amsavarthan.game.trivia.adapters.ScoresAdapter;
import com.amsavarthan.game.trivia.models.Category;
import com.amsavarthan.game.trivia.models.Score;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class CategoryScoreFragment extends Fragment {

    private View view;
    private ScoresAdapter adapter;
    private RecyclerView recyclerView;
    private List<Score> scoreList;

    public CategoryScoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_category_score, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            scoreList=new ArrayList<>();
            adapter=new ScoresAdapter(scoreList);


            recyclerView=view.findViewById(R.id.recylerview);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(adapter);

            FirebaseFirestore.getInstance().collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("Scores")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {

                        if(e!=null){
                            e.printStackTrace();
                            return;
                        }


                        for (DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){

                            if(doc.getType()== DocumentChange.Type.ADDED){

                                Score score=doc.getDocument().toObject(Score.class);
                                scoreList.add(score);
                                adapter.notifyDataSetChanged();

                            }

                        }

                        if(scoreList.size()==0){

                            recyclerView.setVisibility(View.GONE);
                            view.findViewById(R.id.null_layout).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.null_layout).setAlpha(0.0f);
                            view.findViewById(R.id.null_layout).animate()
                                    .setDuration(300)
                                    .alpha(1.0f)
                                    .start();

                        }

                    });


    }


}
