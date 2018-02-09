package com.tech.lotora.doorapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by stijn on 09/02/2018.
 *
 * Task for connecting to the server.
 */

public class ConnectTask extends AsyncTask<Void, Void, Boolean> {
    @SuppressLint("StaticFieldLeak")
    private ConnectActivity connectActivity;
    private String mName;
    private String mKey;
    private String mResponse;

    public ConnectTask(ConnectActivity connectActivity, String name, String key) {
        this.connectActivity = connectActivity;
        this.mName = name;
        this.mKey = key;
    }

    private void createRequest() {
        String url = connectActivity.URL + "?name=" + mName + "&key=" + mKey;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mResponse = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mResponse = "An error occurred";
            }
        });

        // Add the request to the RequestQueue.
        connectActivity.requestQueue.add(stringRequest);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            createRequest();
            while (mResponse == null) {
                Thread.sleep(1);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        connectActivity.mConnectTask = null;
        if (success) {
            connectActivity.switchScreen(mResponse != null ? mResponse : "No response");
        } else {
            connectActivity.mKeyView.requestFocus();
        }
        mResponse = null;
    }

    @Override
    protected void onCancelled() {
        connectActivity.mConnectTask = null;
    }
}
