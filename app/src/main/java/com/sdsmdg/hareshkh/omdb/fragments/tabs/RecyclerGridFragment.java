package com.sdsmdg.hareshkh.omdb.fragments.tabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdsmdg.hareshkh.omdb.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerGridFragment extends Fragment {


    public RecyclerGridFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler_grid, container, false);
    }

}
