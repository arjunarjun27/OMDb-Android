package com.sdsmdg.hareshkh.omdb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sdsmdg.hareshkh.omdb.models.SearchResultModel;
import com.sdsmdg.hareshkh.omdb.retrofit.ApiCall;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ApiCall.Factory.getInstance().search("Iron Man").enqueue(new Callback<SearchResultModel>() {
            @Override
            public void onResponse(Call<SearchResultModel> call, Response<SearchResultModel> response) {
                //success
            }

            @Override
            public void onFailure(Call<SearchResultModel> call, Throwable t) {
                //failure
            }
        });
    }
}
