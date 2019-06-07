package com.cse110easyeat.api.service;

import com.cse110easyeat.easyeat.BuildConfig;
import com.cse110easyeat.network.listener.NetworkListener;
import com.cse110easyeat.network.manager.NetworkVolleyManager;

import android.content.Context;
import android.util.Log;


public class GooglePlacesAPIServices implements APIHandlerService {
    private NetworkVolleyManager requestManager;
    private static final String TAG = "GooglePlacesAPIServices";

    private static final String apiRequestURL = "https://maps.googleapis.com/maps/api/place" +
            "/textsearch/json?&query=%s&key=%s&location=%f,%f&radius=%f";

    public void initializeAPIClient(Context ctx) {
        requestManager = NetworkVolleyManager.getInstance(ctx);
    }

    public String generateAPIQueryURL(String queryString, int minPrice, int maxPrice,
                                      float latitude, float longitude, float radius) {
        String result = "";
        /* Replace whitespace with plus buttons */
        String modifiedQueryString = queryString.replaceAll("\\s+","+");
        String url = String.format(apiRequestURL, modifiedQueryString, BuildConfig.PLACES_API_KEY,
                latitude, longitude, radius);

        if (minPrice > 0 && minPrice <= 4) {
            url += "&minPrice=";
            url += minPrice;
        }

        if (maxPrice > 0 && maxPrice <= 4) {
            url += "&maxPrice=";
            url += maxPrice;
        }

        Log.d(TAG, "/****** API URL USED: " + url);
        return url;
    }


    // add minprice, maxprice opennow
    // Check if price is an option
    public String getRestaurantInfo(String queryString, int minPrice, int maxPrice,
                                    float latitude, float longitude, float radius) {

        String result = "";
        String url = generateAPIQueryURL(queryString, minPrice, maxPrice, latitude, longitude, radius);
        // TODO: Think of the flow
        requestManager.postRequestAndReturnString(url, new NetworkListener<String>() {
            @Override
            public void getResult(String result) {
                if (result != null) {
                    Log.d(TAG, "The API call is: \n" + result);
                }
            }
        });

        return result;
    }
}
