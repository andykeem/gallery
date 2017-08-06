package com.example.foo.galleryapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.foo.galleryapp.helper.PhotoFetchr;
import com.example.foo.galleryapp.helper.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    protected static final String TAG = "MainFragment";
    protected static final int NUM_GRID_COLS = 3;
    protected List<Photo> mPhotos = new ArrayList<>();
    protected RecyclerView mGalleryContainer;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GalleryTask().execute();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blank, container, false);
        mGalleryContainer = v.findViewById(R.id.gallery_container);
        GridLayoutManager layout = new GridLayoutManager(getContext(), NUM_GRID_COLS);
        mGalleryContainer.setLayoutManager(layout);
//        updateAdapter();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "query: " + query);
                new GalleryTask().setQuery(query).execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Log.d(TAG, "newText: " + newText);
                return false;
            }
        });
    }

    public void updateAdapter() {
        if (mPhotos.isEmpty()) return;
        GalleryAdapter adapter = new GalleryAdapter(mPhotos);
        mGalleryContainer.setAdapter(adapter);
    }

    private class GalleryItemHolder extends RecyclerView.ViewHolder {
        protected Photo mItem;
        protected TextView mTitle;
        public GalleryItemHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(android.R.id.text1);
        }
        public void bindItem(Photo item) {
            mItem = item;
            mTitle.setText(mItem.getTitle());
        }
    }

    private class GalleryAdapter extends RecyclerView.Adapter<GalleryItemHolder> {
        private List<Photo> mItems = new ArrayList<>();
        public GalleryAdapter(List items) {
            mItems = items;
        }
        @Override
        public GalleryItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View v = inflater.inflate(android.R.layout.simple_gallery_item, parent, false);
            return new GalleryItemHolder(v);
        }
        @Override
        public int getItemCount() {
            return mItems.size();
        }
        @Override
        public void onBindViewHolder(GalleryItemHolder holder, int position) {
            Photo item = mItems.get(position);
            holder.bindItem(item);
        }
    }

    private class GalleryTask extends AsyncTask<Void, Void, List> {
        protected String mQuery;
        @Override
        protected List doInBackground(Void... params) {
            if (null != mQuery) {
                return new PhotoFetchr().searchPhotosByQuery(mQuery);
            }
            return new PhotoFetchr().fetchPhotos();
        }

        @Override
        protected void onPostExecute(List result) {
            mPhotos = result;
            updateAdapter();
        }

        public GalleryTask setQuery(String query) {
            mQuery = query;
            return this;
        }
    }
}
