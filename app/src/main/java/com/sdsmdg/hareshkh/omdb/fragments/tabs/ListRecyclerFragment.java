package com.sdsmdg.hareshkh.omdb.fragments.tabs;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sdsmdg.hareshkh.omdb.HomeActivity;
import com.sdsmdg.hareshkh.omdb.R;
import com.sdsmdg.hareshkh.omdb.adapters.ListRecyclerAdapter;
import com.sdsmdg.hareshkh.omdb.models.MovieModel;
import com.sdsmdg.hareshkh.omdb.utilities.DividerItemDecoration;

import java.util.ArrayList;

public class ListRecyclerFragment extends Fragment {

    public RecyclerView movieListRecycler;
    private ArrayList<MovieModel> movies;
    public ListRecyclerAdapter listRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    public TextView message;

    public ListRecyclerFragment() {
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
        return inflater.inflate(R.layout.fragment_recycler_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        movieListRecycler = (RecyclerView) view.findViewById(R.id.list_recycler);
        message = (TextView) view.findViewById(R.id.message);
        if (movies != null) {
            listRecyclerAdapter = new ListRecyclerAdapter(getContext(), movies);
            linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            movieListRecycler.setLayoutManager(linearLayoutManager);
            movieListRecycler.setItemAnimator(new DefaultItemAnimator());
            movieListRecycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            movieListRecycler.setAdapter(listRecyclerAdapter);
        }
        movieListRecycler.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
    }
}
