package com.cse110easyeat.easyeat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cse110easyeat.network.listener.NetworkListener;
import com.cse110easyeat.network.manager.NetworkVolleyManager;
import com.cse110easyeat.swipeviewtools.Profile;
import com.cse110easyeat.swipeviewtools.RestaurantCard;
import com.cse110easyeat.swipeviewtools.Utils;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class btnFragment extends Fragment {
    private float x1, x2;
    private static final int MIN_DISTANCE = 150;
    private final String TAG = "btnFragment";

    public static RestaurantCard prevCard;
    public static List<Profile> restaurantList;

    private boolean endOfList = false;
    private NetworkVolleyManager networkManager;

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private String apiResult;
    private int desiredTravelTime;

    public static void setLastCardInfo(RestaurantCard lastCard) {
        prevCard = lastCard;
    }

    public static RestaurantCard getLastCardInfo() {
        return prevCard;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prevCard = null;
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            apiResult = savedInstanceState.getString("profiles");
            Log.d(TAG, "profiles saved: " + apiResult);
        } else {
            apiResult = this.getArguments().getString("data");
            desiredTravelTime = this.getArguments().getInt("desiredTime");
            Log.d(TAG, " Arugments found: \n" + apiResult + "\n\n Time:" + desiredTravelTime);
        }

        return inflater.inflate(R.layout.activity_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSwipeView = (SwipePlaceHolderView)view.findViewById(R.id.swipeView);
        //mSwipeView.unlockViews();
        mContext = getActivity().getApplicationContext();

        networkManager = NetworkVolleyManager.getInstance(getContext());

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
//        for(Profile profile : Utils.loadProfiles(getActivity().getApplicationContext())){
//            mSwipeView.addView(new RestaurantCard(mContext, profile, mSwipeView));
//        }

        try {
            if (restaurantList == null || restaurantList.isEmpty() ) {
                restaurantList = new ArrayList<Profile>();
                JSONArray testArr = new JSONArray(apiResult);
                restaurantList = Utils.loadProfilesFromAPI(testArr);
            }

            for (final Profile profile : restaurantList) {
                networkManager.postRequestAndReturnString(profile.getDistanceURL(), new NetworkListener<String>() {
                    @Override
                    public void getResult(String result) {
                        Pair<String, Integer> parsedRes = extractDistanceAndTimeInMinutes(result);
                        if (parsedRes.second <= desiredTravelTime) {
                            mSwipeView.addView(new RestaurantCard(mContext, profile, mSwipeView));
                        }
                        else {
                            restaurantList.remove(profile);
                        }
                    }
                });
            }
        } catch(JSONException e ) {
            Log.d(TAG, "oops");
        }

        view.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
                Log.d(TAG, "length of restaurants: " + restaurantList.size());
                if (restaurantList.isEmpty()) {
                    mSwipeView.lockViews();
                    endOfList = true;
                    /** Get a new fragment that display an empty screen */
                    Toast.makeText(getContext(), "Ran out of restaurants to show. Please" +
                            " review your preferences", Toast.LENGTH_SHORT).show();
                }

            }
        });

        view.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prevCard != null || !restaurantList.isEmpty()) {
                    swipeAndUpdateList();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    // Replace the contents of the container with the new fragment
                    ft.replace(R.id.mainFragment, new infoFragment());

                    getActivity().getSupportFragmentManager().beginTransaction();

                    ft.addToBackStack(null);
                    ft.commit();
                } else {
                    Toast.makeText(getContext(),"No restaurants were found for the currently set preferences",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static List<Profile> getRestaurantList() {
        return restaurantList;
    }

    public static void removeRestaurant(Profile restaurantToRemove) {
        restaurantList.remove(restaurantToRemove);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "Hello there, im saving");
        String jsonProfiles = convertListToJSONArrayString(restaurantList);
        Log.i(TAG, "saving " + jsonProfiles);
        outState.putString("profiles", jsonProfiles);
    }

    private String convertListToJSONArrayString(List<Profile> restaurantProfiles) {
        try {
            JSONArray restaurantProfileArr = new JSONArray();
            final JSONObject restaurantProfile = new JSONObject();
            for(Profile profile: restaurantList) {
                restaurantProfile.put("name", profile.getName());
                restaurantProfile.put("url", profile.getImageUrl());
                restaurantProfile.put("rating", profile.getRestaurantRating());
                restaurantProfile.put("distance", profile.getDistanceFromCurLoc());
                restaurantProfile.put("address", profile.getAddress());
                restaurantProfile.put("price", profile.getPrice());
                restaurantProfile.put("distanceURL", profile.getDistanceURL());
                restaurantProfileArr.put(restaurantProfile.toString());
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSON READING EXCEPTION: " + e.getMessage());
        }
        return restaurantProfiles.toString();
    }

    /**
     * A function for a workaround to retain card information when user press
     * the back button. This is in conjunction with the addition of the removal
     * of the last element from the list in RestaurantCard's onSwipe()
     */
    private void swipeAndUpdateList() {
        if (!restaurantList.isEmpty()) {
            final Profile lastProfile = restaurantList.get(0);
            mSwipeView.doSwipe(false);
            restaurantList.add(0, lastProfile);
        }
    }

    public Pair<String, Integer> extractDistanceAndTimeInMinutes(String apiResult) {
        String distResult = "Unknown";
        int timeRes = 0;

        try {
            final JSONObject jsonResult = new JSONObject(apiResult);
            JSONArray apiJSONResult = jsonResult.getJSONArray("rows");

            JSONArray distTimeRes = apiJSONResult.getJSONObject(0).getJSONArray("elements");
            JSONObject distance = distTimeRes.getJSONObject(0).getJSONObject("distance");
            JSONObject duration = distTimeRes.getJSONObject(0).getJSONObject("duration");

            distResult = distance.getString("text");
            String timeResult = duration.getString("value");
            int secondsVal = Integer.parseInt(timeResult);
            timeRes = secondsVal / 60;
            if (secondsVal % 60 != 0) {
                timeRes += 1;
            }
        } catch(JSONException e) {
            Log.d(TAG, "JSON Exception: " + e.getMessage());
        }
        return new Pair(distResult, timeRes);
    }
}