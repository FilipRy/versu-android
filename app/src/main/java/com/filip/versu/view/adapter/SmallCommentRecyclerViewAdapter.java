package com.filip.versu.view.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.view.viewmodel.callback.IPostsFeedViewModel;

import java.util.List;

/**
 * This is a adapter for comments displayed below the shopping item
 */
public class SmallCommentRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<CommentDTO> {

    private IPostsFeedViewModel shoppingFeedViewModel;

    public SmallCommentRecyclerViewAdapter(Context context, IPostsFeedViewModel shoppingFeedViewModel) {
        super(context);
        this.shoppingFeedViewModel = shoppingFeedViewModel;
    }

    public SmallCommentRecyclerViewAdapter(List<CommentDTO> recyclerViewItems, Context context, IPostsFeedViewModel shoppingFeedViewModel) {
        super(recyclerViewItems, context);
        this.shoppingFeedViewModel = shoppingFeedViewModel;
    }

    @Override
    public AbsBindViewHolder<CommentDTO> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_comment_small, parent, false);
        return new SmallCommentRecyclerViewHolder(view);
    }


    class SmallCommentRecyclerViewHolder extends AbsBindViewHolder<CommentDTO> {

        private CommentDTO commentDTO;
        private TextView commentNameView;
        private TextView commentContentView;

        public SmallCommentRecyclerViewHolder(View itemView) {
            super(itemView);
            commentNameView = (TextView) itemView.findViewById(R.id.commentatorNameView);
            commentContentView = (TextView) itemView.findViewById(R.id.commentContentView);
        }

        @Override
        public void bindView(CommentDTO entity, int pos) {
            this.commentDTO = entity;
            commentNameView.setText(commentDTO.owner.username);
            commentContentView.setText(commentDTO.content);

            commentNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shoppingFeedViewModel.requestDisplayProfileOfUser(commentDTO.owner);
                }
            });

            commentContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shoppingFeedViewModel.requestCommentsOfPost(commentDTO.postDTO);
                }
            });

        }
    }

}
