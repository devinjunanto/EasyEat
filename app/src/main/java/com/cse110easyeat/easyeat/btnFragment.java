package com.cse110easyeat.easyeat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.cse110easyeat.Profile;
import com.cse110easyeat.TinderCard;
import com.cse110easyeat.Utils;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;


public class btnFragment extends Fragment {
    private float x1, x2;
    private static final int MIN_DISTANCE = 150;
    private final String TAG = "btnFragment";

    public static TinderCard prevCard;

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private String apiResult;

    public static void setLastCardInfo(TinderCard lastCard) {
        prevCard = lastCard;
    }

    public static TinderCard getLastCardInfo() {
        return prevCard;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        apiResult = this.getArguments().getString("data");
        return inflater.inflate(R.layout.activity_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSwipeView = (SwipePlaceHolderView)view.findViewById(R.id.swipeView);
        mContext = getActivity().getApplicationContext();

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
//        for(Profile profile : Utils.loadProfiles(getActivity().getApplicationContext())){
//            mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
//        }

        try {
            JSONArray testArr = new JSONArray(apiResult);
            for(Profile profile: Utils.loadProfilesFromAPI(testArr)) {
                mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
            }
        }catch(JSONException e ) {
            Log.d(TAG, "oops");
        }

        view.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        view.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.mainFragment, new infoFragment());

                getActivity().getSupportFragmentManager().beginTransaction();

                // TODO: TRY CALLING THE GETCARD HERE TURN IT TO JSON, PASS THE JASON AS ARGS
                // TODO: add on backstackchangedlistener |
                ft.addToBackStack(null);
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
            }
        });

        view.findViewById(R.id.moreInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.mainFragment, new infoFragment());


                getActivity().getSupportFragmentManager().beginTransaction();

                // TODO: TRY CALLING THE GETCARD HERE TURN IT TO JSON, PASS THE JASON AS ARGS
                // TODO: add on backstackchangedlistener |
                ft.addToBackStack(null);
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
            }
        });
    }

    // Overriding the methods to save instances of fragments
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (savedInstanceState != null) {
//            // NEED TO LOAD THE ARRAY HERE
//
//            //Restore the fragment's state here
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        //Save the fragment's state here
//    }


}