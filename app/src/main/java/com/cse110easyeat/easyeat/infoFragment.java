package com.cse110easyeat.easyeat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cse110easyeat.Profile;
import com.cse110easyeat.TinderCard;
import com.cse110easyeat.Utils;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.w3c.dom.Text;
// TODO: TRY LIVEVIEW AND VIEWMODEL ASAP

public class infoFragment extends Fragment {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // GET ALL THE FIELDS
        PlaceHolderView cardDisplay = (PlaceHolderView)view.findViewById(R.id.swipeView);
        ImageView image = (ImageView) view.findViewById(R.id.profileImageView);
        TextView nameField = (TextView) view.findViewById(R.id.nameAgeTxt);
        TextView distanceField = (TextView) view.findViewById(R.id.locationNameTxt);
        //TextView ratingField = (TextView) view.findViewById(R.id.ratingTxt);

        TinderCard acceptCard = btnFragment.getLastCardInfo();
        Profile mProfile = acceptCard.getmProfile();
        Context mContext = getActivity().getApplicationContext();

        Glide.with(mContext).load(mProfile.getImageUrl()).into(image);
        nameField.setText("Name: " + mProfile.getName() + "\nRating: " + mProfile.getRestaurantRating());
        //ratingField.setText("Ratings: " + mProfile.getRestaurantRating() + "\n");
        distanceField.setText("Distance: " + mProfile.getDistanceFromCurLoc() +"\nPrice: " + mProfile.getPrice() + "\nAddress: " + mProfile.getAddress());
    }

    // override onBackPressed


}