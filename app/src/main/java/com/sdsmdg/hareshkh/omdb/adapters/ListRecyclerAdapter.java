package com.sdsmdg.hareshkh.omdb.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.hareshkh.omdb.R;
import com.sdsmdg.hareshkh.omdb.models.MovieModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<MovieModel> movies;

    public ListRecyclerAdapter(Context context, ArrayList<MovieModel> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recycler_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MovieModel movie = movies.get(position);
        Picasso.with(context).load(movie.getPoster()).fit().into(holder.poster);
        holder.title.setText(movie.getTitle());
        holder.description.setText(movie.getPlot());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView title, description;

        public MyViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.poster);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
