package com.amsavarthan.trivia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.amsavarthan.trivia.adapters.ScoresAdapter;
import com.amsavarthan.trivia.models.Category;
import com.amsavarthan.trivia.models.Score;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryScoreFragment extends Fragment {

    private View view;
    private String category_name;
    private ScoresAdapter adapter;
    private RecyclerView recyclerView;
    private List<Score> scoreList;
    private List<Category> categories;

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

        //if(getArguments()!=null){

            //category_name=getArguments().getString("category_name");
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
                    //.whereEqualTo("category",category_name)
                    .addSnapshotListener(getActivity(), (queryDocumentSnapshots, e) -> {

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

        //}

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

    }


}
