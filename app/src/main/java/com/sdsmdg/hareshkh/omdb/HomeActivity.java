package com.sdsmdg.hareshkh.omdb;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.sdsmdg.hareshkh.omdb.fragments.tabs.RecyclerGridFragment;
import com.sdsmdg.hareshkh.omdb.fragments.tabs.RecyclerListFragment;
import com.sdsmdg.hareshkh.omdb.models.MovieModel;
import com.sdsmdg.hareshkh.omdb.models.SearchResultModel;
import com.sdsmdg.hareshkh.omdb.retrofit.ApiCall;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = "HomeActivity";
    public static SearchResultModel searchResult;
    public static ArrayList<MovieModel> movies;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SearchView searchView;
    private ProgressDialog progressDialog;

    private RecyclerListFragment listFragment;
    private RecyclerGridFragment gridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        movies = new ArrayList<>();

        listFragment = new RecyclerListFragment();
        gridFragment = new RecyclerGridFragment();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching...");
        progressDialog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, query);
                getData(query);
                if (progressDialog != null && !progressDialog.isShowing()) {
                    progressDialog.show();
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    public void getData(String query) {
        ApiCall.Factory.getInstance().search(query, "movie").enqueue(new Callback<SearchResultModel>() {
            @Override
            public void onResponse(Call<SearchResultModel> call, Response<SearchResultModel> response) {
                searchResult = response.body();
                if (searchResult.getResponse().equals("True")) {
                    getMovies();
                    listFragment.message.setVisibility(View.GONE);
                    listFragment.movieListRecycler.setVisibility(View.VISIBLE);
                } else {
                    //Movie not found
                    movies.clear();
                    progressDialog.dismiss();
                    listFragment.listRecyclerAdapter.notifyDataSetChanged();
                    listFragment.message.setText("No movies found. Try again.");
                    listFragment.message.setVisibility(View.VISIBLE);
                    listFragment.movieListRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<SearchResultModel> call, Throwable t) {
                Log.e(TAG, "Failure : " + t.getMessage());
            }
        });
    }

    public void getMovies() {
        movies.clear();
        final int[] count = {0};
        for (int i = 0; i < searchResult.getSearch().size(); i++) {
            String imdbId = searchResult.getSearch().get(i).getImdbID();
            ApiCall.Factory.getInstance().getMovie(imdbId).enqueue(new Callback<MovieModel>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    movies.add(response.body());
                    Log.d(TAG, movies.get(movies.size() - 1).getTitle());
                    count[0]++;
                    isDataFetchComplete(count[0]);
                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {
                    Log.e(TAG, "Failure : " + t.getMessage());
                    isDataFetchComplete(count[0]);
                    count[0]++;
                }
            });
        }
        listFragment.listRecyclerAdapter.notifyDataSetChanged();
    }

    private void isDataFetchComplete(int count) {
        if (count == searchResult.getSearch().size()) {
            progressDialog.dismiss();
            listFragment.listRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(listFragment, "LIST");
        adapter.addFragment(gridFragment, "GRID");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
