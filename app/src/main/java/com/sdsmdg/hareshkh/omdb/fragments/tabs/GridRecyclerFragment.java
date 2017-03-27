package com.sdsmdg.hareshkh.omdb.fragments.tabs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sdsmdg.hareshkh.omdb.HomeActivity;
import com.sdsmdg.hareshkh.omdb.R;
import com.sdsmdg.hareshkh.omdb.adapters.GridRecyclerAdapter;
import com.sdsmdg.hareshkh.omdb.models.MovieModel;
import com.sdsmdg.hareshkh.omdb.utilities.GridSpacingItemDecoration;

import java.util.ArrayList;

public class GridRecyclerFragment extends Fragment {

    public RecyclerView movieGridRecycler;
    private ArrayList<MovieModel> movies;
    public GridRecyclerAdapter gridRecyclerAdapter;
    private GridLayoutManager gridLayoutManager;
    public TextView message;

    public GridRecyclerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        movies = HomeActivity.movies;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        movieGridRecycler = (RecyclerView) view.findViewById(R.id.grid_recycler);
        message = (TextView) view.findViewById(R.id.message);
        if (movies != null) {
            gridRecyclerAdapter = new GridRecyclerAdapter(getContext(), movies);
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
            movieGridRecycler.setLayoutManager(gridLayoutManager);
            movieGridRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(3), true));
            movieGridRecycler.setItemAnimator(new DefaultItemAnimator());
            movieGridRecycler.setAdapter(gridRecyclerAdapter);
        }
        movieGridRecycler.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
