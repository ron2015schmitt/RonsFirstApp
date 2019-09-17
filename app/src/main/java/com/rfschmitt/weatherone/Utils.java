package com.rfschmitt.weatherone;

/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import android.location.Address;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";


    static private Map<String, String> mStateMap = null;


    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    /**
     * Returns the {@code location} object as a human readable string.
     * @param location  The {@link Location}.
     */
    public static String getLocationCoordsText(Location location) {
        return location == null ? "Unknown location" :
                "(" + String.format("%.2f", location.getLatitude()) + ", " + String.format("%.2f", location.getLongitude()) + ")";
    }

    public static String getLocationText(Location location, Context context) {
        Geocoder geocoder = new Geocoder(context);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if (geocoder.isPresent()) {
                StringBuilder stringBuilder = new StringBuilder();
                if (addresses.size()>0) {
                    Address address = addresses.get(0);
                    String coords = getLocationCoordsText(location);
                    String localityString = address.getLocality();
                    String name = address.getFeatureName();
                    String subLocality = address.getSubLocality();
                    String country = address.getCountryName();
                    String region_code = address.getCountryCode();
                    String zipcode = address.getPostalCode();
                    String state = address.getAdminArea();
                    String stateAbreviation = getStateAbbreviation(address);

                    Log.println(Log.INFO, "GPS", "coords="+coords);
                    Log.println(Log.INFO, "GPS", "localityString="+localityString);
                    Log.println(Log.INFO, "GPS", "name="+name);
                    Log.println(Log.INFO, "GPS", "subLocality="+subLocality);
                    Log.println(Log.INFO, "GPS", "country="+country);
                    Log.println(Log.INFO, "GPS", "region_code="+region_code);
                    Log.println(Log.INFO, "GPS", "zipcode="+zipcode);
                    Log.println(Log.INFO, "GPS", "state="+state);
                    Log.println(Log.INFO, "GPS", "stateAbreviation="+stateAbreviation);
                    String s;
                    if (subLocality != null) {
                        s = subLocality;
                    } else {
                        s = localityString;
                    }
                    return s +", "+stateAbreviation;
                }
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getLocationCoordsText(location);
    }

    static String getStateAbbreviation(Address address) {

        final String TAG = "getStateAbbreviation";

        if (address == null) {
            return null;
        }

        populateStates();

        String stateCode = mStateMap.get(address.getAdminArea());
        if (stateCode == null) {
            Log.d(TAG, "State mapping failed, parsing from address");
            stateCode = parseStateCodeFromFullAddress(address);
            if (stateCode == null) {
                Log.d(TAG, "Could not parse state from address");
            }
        }
        else {
            Log.d(TAG, "Successfully mapped " + address.getAdminArea() + " to " + stateCode);
        }

        return stateCode;
    }

    static private String parseStateCodeFromFullAddress(Address address) {
        final String TAG = "parseStateCodeFromFull";
        if ((address == null) || address.getMaxAddressLineIndex() < 0) {
            return null;
        }

        String fullAddress = "";
        for(int j = 0; j <= address.getMaxAddressLineIndex(); j++) {
            if (address.getAddressLine(j) != null) {
                fullAddress += " " + address.getAddressLine(j);
            }
        }

        Log.d(TAG, "Full address: " + fullAddress);

        Pattern pattern = Pattern.compile("(?<![A-Za-z0-9])([A-Z]{2})(?![A-Za-z0-9])");
        Matcher matcher = pattern.matcher(fullAddress);

        String stateCode = null;
        while (matcher.find()) {
            stateCode = matcher.group().trim();
        }

        Log.d(TAG, "Parsed statecode: " + stateCode);

        return stateCode;
    }

    static void populateStates() {
        if (mStateMap == null) {
            mStateMap = new HashMap<String, String>();
            mStateMap.put("Alabama", "AL");
            mStateMap.put("Alaska", "AK");
            mStateMap.put("Alberta", "AB");
            mStateMap.put("American Samoa", "AS");
            mStateMap.put("Arizona", "AZ");
            mStateMap.put("Arkansas", "AR");
            mStateMap.put("Armed Forces (AE)", "AE");
            mStateMap.put("Armed Forces Americas", "AA");
            mStateMap.put("Armed Forces Pacific", "AP");
            mStateMap.put("British Columbia", "BC");
            mStateMap.put("California", "CA");
            mStateMap.put("Colorado", "CO");
            mStateMap.put("Connecticut", "CT");
            mStateMap.put("Delaware", "DE");
            mStateMap.put("District Of Columbia", "DC");
            mStateMap.put("Florida", "FL");
            mStateMap.put("Georgia", "GA");
            mStateMap.put("Guam", "GU");
            mStateMap.put("Hawaii", "HI");
            mStateMap.put("Idaho", "ID");
            mStateMap.put("Illinois", "IL");
            mStateMap.put("Indiana", "IN");
            mStateMap.put("Iowa", "IA");
            mStateMap.put("Kansas", "KS");
            mStateMap.put("Kentucky", "KY");
            mStateMap.put("Louisiana", "LA");
            mStateMap.put("Maine", "ME");
            mStateMap.put("Manitoba", "MB");
            mStateMap.put("Maryland", "MD");
            mStateMap.put("Massachusetts", "MA");
            mStateMap.put("Michigan", "MI");
            mStateMap.put("Minnesota", "MN");
            mStateMap.put("Mississippi", "MS");
            mStateMap.put("Missouri", "MO");
            mStateMap.put("Montana", "MT");
            mStateMap.put("Nebraska", "NE");
            mStateMap.put("Nevada", "NV");
            mStateMap.put("New Brunswick", "NB");
            mStateMap.put("New Hampshire", "NH");
            mStateMap.put("New Jersey", "NJ");
            mStateMap.put("New Mexico", "NM");
            mStateMap.put("New York", "NY");
            mStateMap.put("Newfoundland", "NF");
            mStateMap.put("North Carolina", "NC");
            mStateMap.put("North Dakota", "ND");
            mStateMap.put("Northwest Territories", "NT");
            mStateMap.put("Nova Scotia", "NS");
            mStateMap.put("Nunavut", "NU");
            mStateMap.put("Ohio", "OH");
            mStateMap.put("Oklahoma", "OK");
            mStateMap.put("Ontario", "ON");
            mStateMap.put("Oregon", "OR");
            mStateMap.put("Pennsylvania", "PA");
            mStateMap.put("Prince Edward Island", "PE");
            mStateMap.put("Puerto Rico", "PR");
            mStateMap.put("Quebec", "PQ");
            mStateMap.put("Rhode Island", "RI");
            mStateMap.put("Saskatchewan", "SK");
            mStateMap.put("South Carolina", "SC");
            mStateMap.put("South Dakota", "SD");
            mStateMap.put("Tennessee", "TN");
            mStateMap.put("Texas", "TX");
            mStateMap.put("Utah", "UT");
            mStateMap.put("Vermont", "VT");
            mStateMap.put("Virgin Islands", "VI");
            mStateMap.put("Virginia", "VA");
            mStateMap.put("Washington", "WA");
            mStateMap.put("West Virginia", "WV");
            mStateMap.put("Wisconsin", "WI");
            mStateMap.put("Wyoming", "WY");
            mStateMap.put("Yukon Territory", "YT");
        }
    }
}
