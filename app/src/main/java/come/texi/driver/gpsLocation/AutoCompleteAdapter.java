package come.texi.driver.gpsLocation;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import come.texi.driver.utils.Common;
import cz.msebera.android.httpclient.Header;

/**
 * Created by techintegrity on 08/07/16.
 */
public class AutoCompleteAdapter extends ArrayAdapter implements Filterable {

    private ArrayList resultList;
    Activity activity;

    public AutoCompleteAdapter(Activity act, int textViewResourceId) {
        super(act, textViewResourceId);
        activity = act;
    }

    @Override
    public int getCount() {
        if(resultList != null   )
            return resultList.size();
        return 0;
    }

    @Override
    public String getItem(int index) {
        String result = (String) resultList.get(index);
        return result;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString(),activity);

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    if(resultList != null)
                        filterResults.count = resultList.size();
                    else
                        filterResults.count = 0;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    public static ArrayList autocomplete(String input,Activity activity) {


        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

            String locationUrl = "";
            try {
                locationUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyD5QFt6IdBuIHpvV1Z9FdAs0yBnBBwyI_g&input=" + URLEncoder.encode(input, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        try {

            URL url = new URL(locationUrl);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("", "Error processing Places API URL", e);

        } catch (IOException e) {
            Log.e("", "Error connecting to Places API", e);
            Common.ShowHttpErrorMessage(activity, "Connect to");
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            Log.d("jsonObj","jsonObj = "+jsonObj);
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e("", "Cannot process JSON results", e);
        }

        return resultList;
    }
}

