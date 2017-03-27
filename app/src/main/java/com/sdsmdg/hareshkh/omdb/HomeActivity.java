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

import com.sdsmdg.hareshkh.omdb.fragments.tabs.GridRecyclerFragment;
import com.sdsmdg.hareshkh.omdb.fragments.tabs.ListRecyclerFragment;
import com.sdsmdg.hareshkh.omdb.models.MovieModel;
import com.sdsmdg.hareshkh.omdb.models.SearchResultModel;
import com.sdsmdg.hareshkh.omdb.retrofit.ApiCall;
import com.sdsmdg.hareshkh.omdb.utilities.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = "HomeActivity";
    public SearchResultModel searchResult;
    public static ArrayList<MovieModel> movies;
    private int pagesLoaded;
    private String latestQuery;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SearchView searchView;
    private ProgressDialog progressDialog;

    private ListRecyclerFragment listFragment;
    private GridRecyclerFragment gridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        movies = new ArrayList<>();

        listFragment = new ListRecyclerFragment();
        gridFragment = new GridRecyclerFragment();

        setupLazyLoad();

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
                HomeActivity.this.latestQuery = query;
                getData(query, true);
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

    public void getData(final String query, boolean newQuery) {
        if (newQuery) {
            movies.clear();
            listFragment.listRecyclerAdapter.notifyDataSetChanged();
            gridFragment.gridRecyclerAdapter.notifyDataSetChanged();
            pagesLoaded = 0;
            ApiCall.Factory.getInstance().search(query, "movie", 1).enqueue(new Callback<SearchResultModel>() {
                @Override
                public void onResponse(Call<SearchResultModel> call, Response<SearchResultModel> response) {
                    searchResult = response.body();
                    if (searchResult.getResponse().equals("True")) {
                        //Movie Found
                        pagesLoaded = 1;
                        getMovies();
                        listFragment.message.setVisibility(View.GONE);
                        gridFragment.message.setVisibility(View.GONE);
                        listFragment.movieListRecycler.setVisibility(View.VISIBLE);
                        gridFragment.movieGridRecycler.setVisibility(View.VISIBLE);
                    } else {
                        //Movie not found
                        progressDialog.dismiss();
                        listFragment.message.setText("No movies found. Try again.");
                        gridFragment.message.setText("No movies found. Try again.");
                        listFragment.message.setVisibility(View.VISIBLE);
                        gridFragment.message.setVisibility(View.VISIBLE);
                        listFragment.movieListRecycler.setVisibility(View.GONE);
                        gridFragment.movieGridRecycler.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<SearchResultModel> call, Throwable t) {
                    Log.e(TAG, "Failure : " + t.getMessage());
                    progressDialog.dismiss();
                    listFragment.message.setText("Query request failed. Try again");
                    gridFragment.message.setText("Query request failed. Try again");
                    listFragment.message.setVisibility(View.VISIBLE);
                    gridFragment.message.setVisibility(View.VISIBLE);
                    listFragment.movieListRecycler.setVisibility(View.GONE);
                    gridFragment.movieGridRecycler.setVisibility(View.GONE);
                }
            });
        } else {
            pagesLoaded++;
            ApiCall.Factory.getInstance().search(query, "movie", pagesLoaded).enqueue(new Callback<SearchResultModel>() {
                @Override
                public void onResponse(Call<SearchResultModel> call, Response<SearchResultModel> response) {
                    searchResult = response.body();
                    if (searchResult.getResponse().equals("True")) {
                        //Movie Found
                        getMovies();
                    } else {
                        //Reached End
                        movies.remove(movies.size() - 1);
                        listFragment.listRecyclerAdapter.notifyItemRemoved(movies.size());
                        gridFragment.gridRecyclerAdapter.notifyItemRemoved(movies.size());
                        listFragment.listRecyclerAdapter.notifyDataSetChanged();
                        gridFragment.gridRecyclerAdapter.notifyDataSetChanged();
                        listFragment.setLoaded();
                        gridFragment.setLoaded();
                    }
                }

                @Override
                public void onFailure(Call<SearchResultModel> call, Throwable t) {
                    movies.remove(movies.size() - 1);
                    listFragment.listRecyclerAdapter.notifyItemRemoved(movies.size());
                    gridFragment.gridRecyclerAdapter.notifyItemRemoved(movies.size());
                    listFragment.listRecyclerAdapter.notifyDataSetChanged();
                    gridFragment.gridRecyclerAdapter.notifyDataSetChanged();
                    listFragment.setLoaded();
                    gridFragment.setLoaded();
                }
            });
        }
    }

    public void getMovies() {
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
                    count[0]++;
                    isDataFetchComplete(count[0]);
                }
            });
        }
    }

    private void isDataFetchComplete(int count) {
        if (count == searchResult.getSearch().size()) {
            progressDialog.dismiss();
            for (int i = 0; i < movies.size(); i++) {
                if (movies.get(i) == null) {
                    movies.remove(i);
                    listFragment.listRecyclerAdapter.notifyItemRemoved(i);
                    gridFragment.gridRecyclerAdapter.notifyItemRemoved(i);
                }
            }
            listFragment.listRecyclerAdapter.notifyDataSetChanged();
            listFragment.setLoaded();
            gridFragment.gridRecyclerAdapter.notifyDataSetChanged();
            gridFragment.setLoaded();
        }
    }

    private void setupLazyLoad() {
        listFragment.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                movies.add(null);
                listFragment.listRecyclerAdapter.notifyItemInserted(movies.size() - 1);
                getData(latestQuery, false);
            }
        });
        gridFragment.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                movies.add(null);
                gridFragment.gridRecyclerAdapter.notifyItemInserted(movies.size() - 1);
                getData(latestQuery, false);
            }
        });
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
