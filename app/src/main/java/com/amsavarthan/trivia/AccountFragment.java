package com.amsavarthan.trivia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsavarthan.trivia.adapters.ScoresAdapter;
import com.amsavarthan.trivia.models.Category;
import com.amsavarthan.trivia.models.Score;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private static final String TAG =AccountFragment.class.getSimpleName() ;
    private View view;
    private TextView name;
    private static final int RC_SIGN_IN = 234;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    LinearLayout connect_layout,disconnect_layout;
    CircleImageView user_profile;
    private ProgressDialog mDialog;
    private String points;
    private Button signin,signout;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_user_info, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth=FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);

        name = view.findViewById(R.id.name);
        connect_layout=view.findViewById(R.id.connect_layout);
        disconnect_layout=view.findViewById(R.id.disconnect_layout);
        user_profile=view.findViewById(R.id.user_profile);
        signin=view.findViewById(R.id.signIn);
        signout=view.findViewById(R.id.signOut);

        mDialog=new ProgressDialog(view.getContext());
        mDialog.setMessage("Please wait...");
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        if(mAuth.getCurrentUser()!=null)
        {
            FirebaseUser user = mAuth.getCurrentUser();

            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            points=documentSnapshot.getString("points");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                    });

            Glide.with(getActivity())
                    .setDefaultRequestOptions(new RequestOptions().dontAnimate())
                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.mipmap.user))
                    .load(user.getPhotoUrl().toString().replace("s96-c","s200-c"))
                    .into(user_profile);
            name.setText(String.format(getString(R.string.you_are_signed_in),user.getDisplayName()));
            connect_layout.setVisibility(View.GONE);
            disconnect_layout.setVisibility(View.VISIBLE);
        } else {
            disconnect_layout.setVisibility(View.GONE);
            connect_layout.setVisibility(View.VISIBLE);
        }

        signin.setOnClickListener(v -> signIn());

        signout.setOnClickListener(v -> signOut());

    }

    public void signIn()
    {
        mDialog.show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut(){

        mDialog.show();
        mAuth.signOut();
        mGoogleSignInClient.signOut()
                .addOnSuccessListener(aVoid -> {
                    mDialog.dismiss();
                    Glide.with(getActivity())
                            .load(R.mipmap.user)
                            .into(user_profile);

                    disconnect_layout.setVisibility(View.GONE);
                    connect_layout.setVisibility(View.VISIBLE);

                })
                .addOnFailureListener(e -> {
                    mDialog.dismiss();
                    Toast.makeText(getActivity(), "Error logging out.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG,e.getLocalizedMessage());
                });


    }

    @Override
    public void onResume() {
        super.onResume();

        if(mAuth.getCurrentUser()!=null)
        {
            FirebaseUser user = mAuth.getCurrentUser();
            name.setText(String.format(getString(R.string.you_are_signed_in),user.getDisplayName()));
            Glide.with(getActivity())
                    .setDefaultRequestOptions(new RequestOptions().dontAnimate())
                    .setDefaultRequestOptions(new RequestOptions().placeholder(R.mipmap.user))
                    .load(user.getPhotoUrl().toString().replace("s96-c","s200-c"))
                    .into(user_profile);
            connect_layout.setVisibility(View.GONE);
            disconnect_layout.setVisibility(View.VISIBLE);
        } else {
            disconnect_layout.setVisibility(View.GONE);
            connect_layout.setVisibility(View.VISIBLE);
        }

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
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
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
                                                    Glide.with(getActivity())
                                                            .setDefaultRequestOptions(new RequestOptions().dontAnimate())
                                                            .setDefaultRequestOptions(new RequestOptions().placeholder(R.mipmap.user))
                                                            .load(user.getPhotoUrl().toString().replace("s96-c","s200-c"))
                                                            .into(user_profile);
                                                    name.setText(String.format(getString(R.string.you_are_signed_in),user.getDisplayName()));
                                                    connect_layout.setVisibility(View.GONE);
                                                    disconnect_layout.setVisibility(View.VISIBLE);                                        })
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
                                                    Glide.with(getActivity())
                                                            .setDefaultRequestOptions(new RequestOptions().dontAnimate())
                                                            .setDefaultRequestOptions(new RequestOptions().placeholder(R.mipmap.user))
                                                            .load(user.getPhotoUrl().toString().replace("s96-c","s200-c"))
                                                            .into(user_profile);
                                                    name.setText(String.format(getString(R.string.you_are_signed_in),user.getDisplayName()));
                                                    connect_layout.setVisibility(View.GONE);
                                                    disconnect_layout.setVisibility(View.VISIBLE);                                        })
                                                .addOnFailureListener(e -> {
                                                    mDialog.dismiss();
                                                    Log.e(TAG,e.getLocalizedMessage());
                                                });

                                    }

                                });

                    }else{
                        mDialog.dismiss();
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getActivity(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e -> {
                    mDialog.dismiss();
                    Log.e(TAG, e.getLocalizedMessage());
                });

    }
}
