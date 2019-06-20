package com.filip.versu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostFeedbackVoteDTO;
import com.filip.versu.view.viewmodel.callback.IPostFeedbackViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PostFeedbackRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<PostFeedbackVoteDTO> {

    private IPostFeedbackViewModel postFeedbackViewModel;

    public PostFeedbackRecyclerViewAdapter(List<PostFeedbackVoteDTO> recyclerViewItems, Context context, IPostFeedbackViewModel postFeedbackViewModel) {
        super(recyclerViewItems, context);
        this.postFeedbackViewModel = postFeedbackViewModel;
    }

    @Override
    public AbsBindViewHolder<PostFeedbackVoteDTO> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_user, parent, false);
        return new PostFeedbackRecyclerViewHolder(view);
    }

    class PostFeedbackRecyclerViewHolder extends AbsBindViewHolder<PostFeedbackVoteDTO> {

        private CircleImageView userImageView;
        private TextView usernameTextView;

        private PostFeedbackVoteDTO postFeedbackVoteDTO;

        public PostFeedbackRecyclerViewHolder(View itemView) {
            super(itemView);
            userImageView = (CircleImageView) itemView.findViewById(R.id.user_profile_photo);
            usernameTextView = (TextView) itemView.findViewById(R.id.user_username);
        }

        @Override
        public void bindView(final PostFeedbackVoteDTO entity, int pos) {
            this.postFeedbackVoteDTO = entity;

            this.usernameTextView.setText(entity.owner.username);

            Picasso.with(context).load(entity.owner.profilePhotoURL).
                    placeholder(R.mipmap.ic_account_circle_white_48dp).into(userImageView);

            this.usernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postFeedbackViewModel.requestDisplayProfileOfUser(entity.owner);
                }
            });
        }
    }

}
