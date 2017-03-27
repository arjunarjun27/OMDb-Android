package com.sdsmdg.hareshkh.omdb.retrofit;

import com.sdsmdg.hareshkh.omdb.models.MovieModel;
import com.sdsmdg.hareshkh.omdb.models.SearchResultModel;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiCall {

    String BASE_URL = "http://www.omdbapi.com";

    @GET("/")
    Call<SearchResultModel> search(@Query("s") String query, @Query("type") String type, @Query("page") int page);

    @GET("/")
    Call<MovieModel> getMovie(@Query("i") String imdbId);

    class Factory {
        public static ApiCall service;

        public static ApiCall getInstance() {
            if (service == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
                service = retrofit.create(ApiCall.class);
                return service;
            } else {
                return service;
            }
        }
    }
}
