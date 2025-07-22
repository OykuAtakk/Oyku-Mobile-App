package com.oykuatak.oyku.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oykuatak.oyku.Model.Blog;
import com.oykuatak.oyku.databinding.RecyclerRowBinding;

import java.util.ArrayList;


public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogHolder> {

    private ArrayList<Blog> blogArrayList;

    public BlogAdapter(ArrayList<Blog> blogArrayList) {
        this.blogArrayList = blogArrayList;
    }

    @NonNull
    @Override
    public BlogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BlogHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogHolder holder, int position) {
        Blog blog = blogArrayList.get(position);

        holder.recyclerRowBinding.recyclerViewEmailText.setText(blog.getUserEmail()); // Kullanıcı emaili
        holder.recyclerRowBinding.recyclerViewTitleText.setText(blog.getTitle()); // Başlık
        holder.recyclerRowBinding.recyclerViewDescriptionText.setText(blog.getDescription()); // Açıklama
        holder.recyclerRowBinding.recyclerViewTimestampText.setText(blog.getTimestamp()); // Zaman damgası

        // Glide ile görsel yükleme
        Glide.with(holder.recyclerRowBinding.recyclerViewImageView.getContext())
                .load(blog.getImageUrl())
                .into(holder.recyclerRowBinding.recyclerViewImageView); // Fotoğraf
    }

    @Override
    public int getItemCount() {
        return blogArrayList.size();
    }

    class BlogHolder extends RecyclerView.ViewHolder {

        RecyclerRowBinding recyclerRowBinding;

        public BlogHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }
}
