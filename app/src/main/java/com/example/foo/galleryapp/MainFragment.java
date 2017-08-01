package com.example.foo.galleryapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foo.galleryapp.helper.GalleryFetchr;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GalleryTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blank, container, false);

        return v;
    }

    private class GalleryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... var1) {
            String url = "http://google.com";
            new GalleryFetchr().getUrlContents(url);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
