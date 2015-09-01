package com.yahoo.apps.thirty.models;

import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jackylee on 9/1/15.
 */
public class AsyncResult {
    private int count;
    private SparseArray<JSONObject> results;


    public AsyncResult() {
        this.count = 0;
        this.results = new SparseArray<>();
    }

    public void addResult(int key, JSONObject result) {
        count += 1;
        results.append(key, result);
    }

    public int getCount() {
        return count;
    }

    public JSONArray getResults() {
        JSONArray jsonResults = new JSONArray();
        for (int i = 0, n = count; i < n; i++) {
            try {
                jsonResults.put(i, results.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonResults;
    }
}
