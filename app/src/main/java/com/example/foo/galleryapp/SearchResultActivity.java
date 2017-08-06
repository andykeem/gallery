package com.example.foo.galleryapp;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class SearchResultActivity extends AppCompatActivity {

    protected static final String TAG = "SearchResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        if (!Intent.ACTION_SEARCH.equals(intent.getAction())) return;
        String query = intent.getStringExtra(SearchManager.QUERY);

        String msg = "Search query: " + query;
        Log.d(TAG, msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
