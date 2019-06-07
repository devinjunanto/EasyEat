package com.cse110easyeat.easyeat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cse110easyeat.api.service.GooglePlacesAPIServices;
import com.cse110easyeat.network.listener.NetworkListener;
import com.cse110easyeat.network.manager.NetworkVolleyManager;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

import static com.android.volley.VolleyLog.TAG;

public class inputFragment extends Fragment  {
    private final String TAG = "InputFragment";
    private EditText budgetField;
    private EditText distanceField;
    private EditText timeField;

    private int budget;
    private int distance;
    private int timeToWait;
    private float longitude;
    private float latitude;

    private ProgressDialog progressCircle;

    private NetworkVolleyManager networkManager;
    private GooglePlacesAPIServices apiHelper;

//    private LocationManager locationManager;
//    private LocationListener tracker;

    // TODO: WHY IS IT NOT ENDING
    // TODO: IMAGE IS STILL A BIT TOO LARGE
    // TODO: onPostExecute
    private JSONArray writeDataToJsonFile(String apiResult) {
        final JSONArray arrToWrite = new JSONArray();
        try {
            final JSONObject jsonResult = new JSONObject(apiResult);
            JSONArray resultsArr = jsonResult.getJSONArray("results");
            Log.d(TAG, "parsed result array: \n" + resultsArr.toString());
            // TODO: EXTRACT THE RATING, NAME, DISTANCE, IMAGE URL
            // TODO: USE DISTANCE MATRIX API TO GET DISTANCE
            // TODO: EXTRACT FORMATED ADDRESS FIELD
            // TODO: GET THE PLACE_ID
            for (int i = 0; i < resultsArr.length(); i++) {
                Log.d(TAG, "Results length: " + resultsArr.length());
                JSONObject restaurantRes = resultsArr.getJSONObject(i);
                // TODO: figure out how to get distance

                String photoRef = restaurantRes.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
//                String height = restaurantRes.getJSONArray("photos").getJSONObject(0).getString("height");
//                String width = restaurantRes.getJSONArray("photos").getJSONObject(0).getString("width");

                String height = "220";
                String width = "150";

                String address = restaurantRes.getString("formatted_address");
                String priceLevel = restaurantRes.getString("price_level");
                String priceIcon = "";
                try {
                    int price = Integer.parseInt(priceLevel);
                    for (int j = 0; j < price; j++) {
                        priceIcon += "$";
                    }
                } catch (NumberFormatException e) {
                    Log.d(TAG, "Weird result from aPI");
                }

                jsonResult.put("address", address);
                jsonResult.put("price", priceIcon);
                jsonResult.put("distance", "3" + " miles");
                jsonResult.put("rating", restaurantRes.getString("rating"));
                jsonResult.put("name", restaurantRes.getString("name"));

                String placeId = restaurantRes.getString("place_id");
                String totalNumRatings = restaurantRes.getString("user_ratings_total");

                String imageURL = generateImageURL(photoRef, height, width);
                float currentLat = 32.8801f;
                float currentLong = -117.2340f;

                jsonResult.put("url", imageURL);
                Log.d(TAG, "json res: \n" + jsonResult.toString());
                String distanceURL = generateDistanceURL(currentLat, currentLong, placeId);

                // USE THIS TO GET DISTANCE
//                networkManager.postRequestAndReturnString(distanceURL, new NetworkListener<String>() {
//                    @Override
//                    public void getResult(String result) {
//                        Log.d(TAG, "Distance API Call result: \n" + result);
//                        arrToWrite.put(jsonResult.toString());
//                    }
//                });
//                arrToWrite.put(jsonResult.toString());
                arrToWrite.put(jsonResult.toString());
            }

        } catch (final JSONException e) {
            Log.d(TAG, "Error in parsing the api results" + e.getMessage());
        }
        return arrToWrite;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.input_layout, container, false);
    }

    // TODO: GET LOCATION
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        // TODO: LOCATION CHECK (THEY ARE ASYNC)
//        checkLocationPermissions();
//        getLocation();


        progressCircle = new ProgressDialog(getActivity());

        networkManager = NetworkVolleyManager.getInstance(getContext());
        apiHelper = new GooglePlacesAPIServices();
        Button btn = (Button) view.findViewById(R.id.submitButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO THE API CALL
                progressCircle.setMessage("Validating input fields...");
                progressCircle.show();
                if (validateInputFields()) {
                    // TODO: TESTING API CALL
                    progressCircle.setMessage("Finding Easiest Eats...");
                    Log.d(TAG, "Longitude: " + longitude);
                    Log.d(TAG, "Latitude: " + latitude);

                    // TODO: HARD CODE COORDINATES
                    String queryURL = apiHelper.generateAPIQueryURL("restaurant nearby", 1,
                            budget, 32.8801f, -117.2340f, distance);
                    networkManager.postRequestAndReturnString(queryURL, new NetworkListener<String>() {
                        @Override
                        public void getResult(String result) {
                            // write the results to a json file
                            Log.d(TAG, "API RESULTS:\n" + result);
                            JSONArray test = writeDataToJsonFile(result);

                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            btnFragment fragClass = new btnFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("data", test.toString());
                            fragClass.setArguments(bundle);
                            // Replace the contents of the container with the new fragment
                            ft.replace(R.id.mainFragment, fragClass);
                            // or ft.add(R.id.your_placeholder, new FooFragment());
                            // Complete the changes added above
                            progressCircle.hide();
                            progressCircle.dismiss();
                            ft.commit();
                        }
                    });
                }
            }
        });

        timeField = (EditText) view.findViewById(R.id.timeInput);
        distanceField = (EditText) view.findViewById(R.id.distanceInput);
        budgetField = (EditText) view.findViewById(R.id.budgetInput);

    }

    private boolean validateInputFields() {
        if (budgetField.getText().toString().matches("") ||
                distanceField.getText().toString().matches("") ||
                timeField.getText().toString().matches("")) {
            Log.d(TAG, "One of the input fields is empty");
            progressCircle.hide();
            Toast.makeText(getActivity(), "Please fill all the fields",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            budget = Integer.parseInt(budgetField.getText().toString());
            if (budget <= 0 || budget > 4) {
                progressCircle.hide();
                Toast.makeText(getActivity(), "Please enter budget between 1-4",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            distance = Integer.parseInt(distanceField.getText().toString());
            timeToWait = Integer.parseInt(timeField.getText().toString());
        } catch (NumberFormatException e) {
            progressCircle.hide();
            Toast.makeText(getActivity(), "Please enter integers", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


//    // START OF ATTEMPT TO GET LOCATION
//    public void checkLocationPermissions() {
//        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
//                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
//                            android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
//        }
//    }
//
//    public void getLocation() {
//        try {
//            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
//        }
//        catch(SecurityException e) {
//            e.printStackTrace();
//        }
//    }

    // TODO: REFACTOR THESE METHODS
    // URL GENERATOR HELPER METHODS
    public String generateImageURL(String photoReference, String height, String width) {
        String imageURL = "https://maps.googleapis.com/maps/api/place/photo?key=%s&photoreference=%s&maxheight=%s&maxwidth=%s";
        imageURL = String.format(imageURL, BuildConfig.PLACES_API_KEY, photoReference,
                height, width);
        return imageURL;
    }

    public String generateDistanceURL(float currentLat, float currentLong, String destinationId) {
        String distanceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%f,%f" +
                "&destinations=place_id:%s&units=imperial&key=%s";
        distanceURL = String.format(distanceURL, currentLat, currentLong, destinationId,
                BuildConfig.PLACES_API_KEY);
        return distanceURL;
    }

//    // TODO: IMPLEMENT GET LOCATION METHODS
//    @Override
//    public void onLocationChanged(Location location) {
//        try {
//            Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            longitude = (float)location.getLongitude();
//            latitude =  (float)location.getLatitude();
//
////            locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
////                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
//        } catch(Exception e) {
//            Log.d(TAG, "Exception caught onLocationChanged");
//        }
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) { }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        Log.d(TAG, "Location services and internet enabled");
//        Toast.makeText(getActivity().getApplicationContext(), "Location Services enabled",
//                Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        Log.d(TAG, "Location services is not activated yet");
//        Toast.makeText(getActivity().getApplicationContext(), "Enable location services",
//                Toast.LENGTH_SHORT).show();
//    }
}
