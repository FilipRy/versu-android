package com.filip.versu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.viewmodel.callback.IViewersSelectorViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class PostViewersRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<PostViewersRecyclerViewAdapter.PostViewer> {

    public static class PostViewer extends AbsBaseEntityDTO<Long> {

        public UserDTO postViewer;
        public boolean isSelected;
    }

    private IViewersSelectorViewModel viewersSelectorViewModel;

    public PostViewersRecyclerViewAdapter(Context context, IViewersSelectorViewModel viewersSelectorViewModel) {
        super(context);
        recyclerViewItems = new ArrayList<>();
        this.viewersSelectorViewModel = viewersSelectorViewModel;
    }

    @Override
    public PostViewersRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_post_viewer, parent, false);
        return new PostViewersRecyclerViewHolder(view);
    }

    class PostViewersRecyclerViewHolder extends AbsBindViewHolder<PostViewer> {

        private CircleImageView userImageView;
        private TextView usernameTextView;
        private CheckBox selectedCheckBox;

        public PostViewersRecyclerViewHolder(View itemView) {
            super(itemView);
            userImageView = (CircleImageView) itemView.findViewById(R.id.user_profile_photo);
            usernameTextView = (TextView) itemView.findViewById(R.id.user_username);
            selectedCheckBox = (CheckBox) itemView.findViewById(R.id.isSelectedCheckBox);
        }

        @Override
        public void bindView(final PostViewer entity, int pos) {
            usernameTextView.setText(entity.postViewer.username);
            Picasso.with(context).load(entity.postViewer.profilePhotoURL).placeholder(R.mipmap.ic_account_circle_white).into(userImageView);

            selectedCheckBox.setChecked(entity.isSelected);

            selectedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    entity.isSelected = isChecked;

                    if(isChecked) {
                        viewersSelectorViewModel.addViewerToPost(entity.postViewer);
                    } else {
                        viewersSelectorViewModel.removeViewerFromPost(entity.postViewer);
                    }

                }
            });
        }
    }

}
