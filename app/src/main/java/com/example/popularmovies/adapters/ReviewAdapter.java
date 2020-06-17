package com.example.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.databinding.ReviewListItemBinding;
import com.example.popularmovies.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private List<Review> mReviewData;

    public ReviewAdapter() {

    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        ReviewListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.review_list_item, parent, false
        );

        return new ReviewAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder reviewAdapterViewHolder, int position) {
        Review review = mReviewData.get(position);

        reviewAdapterViewHolder.mReviewListItemBinding.tvReviewAuthor.setText(review.getAuthor());
        reviewAdapterViewHolder.mReviewListItemBinding.tvReviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if(mReviewData == null) return 0;

        return mReviewData.size();
    }

    public void setReviewData(List<Review> reviews){
        mReviewData =  reviews;
        notifyDataSetChanged();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        final ReviewListItemBinding mReviewListItemBinding;

        public ReviewAdapterViewHolder(ReviewListItemBinding binding){
            super(binding.getRoot());
            mReviewListItemBinding = binding;
        }
    }
}
