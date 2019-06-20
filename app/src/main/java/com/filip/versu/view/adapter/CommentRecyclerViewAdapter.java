package com.filip.versu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.CommentDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.impl.CommentService;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.viewmodel.callback.ICommentViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommentRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<CommentDTO> {

    private Context context;
    private ICommentViewModel commentViewModel;
    private IUserSession userSession = UserSession.instance();

    public CommentRecyclerViewAdapter(Context context, ICommentViewModel commentViewModel) {
        super(context);
        this.context = context;
        this.commentViewModel = commentViewModel;
    }

    public CommentRecyclerViewAdapter(List<CommentDTO> recyclerViewItems, Context context, ICommentViewModel commentViewModel) {
        super(recyclerViewItems, context);
        this.context = context;
        this.commentViewModel = commentViewModel;
    }

    @Override
    public AbsBindViewHolder<CommentDTO> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_comment, parent, false);
        return new CommentRecyclerViewHolder(view);
    }

    class CommentRecyclerViewHolder extends AbsBindViewHolder<CommentDTO> {

        private CommentDTO commentDTO;
        private CircleImageView userImageView;
        private TextView usernameTextView;
        private TextView commentTimeView;

        private TextView commentContentTextView;
        private ImageView deleteImageView;

        public CommentRecyclerViewHolder(View itemView) {
            super(itemView);
            userImageView = (CircleImageView) itemView.findViewById(R.id.user_profile_photo);
            usernameTextView = (TextView) itemView.findViewById(R.id.user_username);
            deleteImageView = (ImageView) itemView.findViewById(R.id.deleteCommentView);
            commentContentTextView = (TextView) itemView.findViewById(R.id.comment_content);
            commentTimeView = (TextView) itemView.findViewById(R.id.commentTimeView);
        }

        @Override
        public void bindView(final CommentDTO entity, final int pos) {
            this.commentDTO = entity;
            this.usernameTextView.setText(commentDTO.owner.username);

            this.commentTimeView.setText(CommentService.getFormattedDate(context, commentDTO.timestamp));


            this.commentContentTextView.setText(commentDTO.content);

            if (entity.owner.profilePhotoURL != null && !entity.owner.profilePhotoURL.isEmpty()) {
                Picasso.with(context).load(entity.owner.profilePhotoURL).
                        placeholder(R.mipmap.ic_account_circle_white_48dp).into(userImageView);
            }


            this.usernameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentViewModel.requestDisplayProfileOfUser(commentDTO.owner);
                }
            });

            this.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentViewModel.executeDeleteItemTask(entity, pos, CommentRecyclerViewAdapter.this.getContent());
                }
            });

            UserDTO loggedIn = userSession.getLogedInUser();
            UserDTO commentCreator = entity.owner;

            if(commentCreator.equals(loggedIn)) {
                deleteImageView.setVisibility(View.VISIBLE);
            } else {
                deleteImageView.setVisibility(View.GONE);
            }
        }
    }





}
