package com.filip.versu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.view.viewmodel.AbsPostsTimelineFeedViewModel;

import java.util.List;


public class PostFeedRecyclerViewAdapter extends AbsPostRecyclerViewAdapter {

    public PostFeedRecyclerViewAdapter(List<PostDTO> postDTOs, Context applicationContext, AbsPostsTimelineFeedViewModel viewModel) {
        super(postDTOs, applicationContext, viewModel);
    }


    @Override
    public AbsBindViewHolder<PostDTO> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == POST_ITEM_TYPE.SIMPLE_MINE.getValue()) {

            View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_post_feed, parent, false);
            return new SimplePostMyProfileRecyclerViewHolder(view);

        } else if (viewType == POST_ITEM_TYPE.SIMPLE_OTHER.getValue()) {

            View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_post_feed, parent, false);
            return new SimplePostFeedRecyclerViewHolder(view);

        } else if (viewType == POST_ITEM_TYPE.DOUBLE_OTHER.getValue()) {

            View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_voting_shopping_item, parent, false);
            return new DoublePostRecyclerViewHolder(view, true);

        } else if (viewType == POST_ITEM_TYPE.DOUBLE_MINE.getValue()) {

            View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_voting_shopping_item, parent, false);
            return new DoublePostRecyclerViewHolder(view, false);

        }

        return null;
    }
}
