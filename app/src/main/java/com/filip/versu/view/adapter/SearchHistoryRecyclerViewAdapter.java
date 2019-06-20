package com.filip.versu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.HashtagSearch;
import com.filip.versu.model.Location;
import com.filip.versu.model.Searchable;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.viewmodel.callback.ISearchHistoryViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchHistoryRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<Searchable> {

    private ISearchHistoryViewModel viewModel;


    public SearchHistoryRecyclerViewAdapter(List<Searchable> recyclerViewItems, Context context, ISearchHistoryViewModel viewModel) {
        super(recyclerViewItems, context);
        this.viewModel = viewModel;
    }

    @Override
    public AbsBindViewHolder<Searchable> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_user, parent, false);
        return new SearchHistoryRecyclerViewHolder(view);
    }

    class SearchHistoryRecyclerViewHolder extends AbsBindViewHolder<Searchable> {

        private Searchable entity;
        private View parentView;
        private CircleImageView itemImageView;
        private TextView nameTextView;

        public SearchHistoryRecyclerViewHolder(View itemView) {
            super(itemView);
            parentView = itemView.findViewById(R.id.userRowParent);
            itemImageView = (CircleImageView) itemView.findViewById(R.id.user_profile_photo);
            nameTextView = (TextView) itemView.findViewById(R.id.user_username);
        }

        @Override
        public void bindView(final Searchable entity, int pos) {
            this.entity = entity;

            nameTextView.setText(entity.getEntryName());

            if (entity instanceof UserDTO) {
                final UserDTO userDTO = (UserDTO) entity;
                Picasso.with(context).load(userDTO.profilePhotoURL).
                        placeholder(R.mipmap.ic_account_circle_white_48dp).into(itemImageView);

                this.parentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.requestDisplayProfileOfUser(userDTO);
                    }
                });

            } else if (entity instanceof HashtagSearch) {

                itemImageView.setImageResource(R.drawable.hashtag);

                parentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashtagSearch hashtagSearch = (HashtagSearch) entity;
                        viewModel.requestPostsByFeedbackPossibilities(new String[]{hashtagSearch.possibilityA, hashtagSearch.possibilityB});
                    }
                });

            } else if (entity instanceof Location) {
                itemImageView.setImageResource(R.drawable.map_marker);

                parentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Location location = (Location) entity;
                        viewModel.requestDisplayLocationProfile(location);
                    }
                });

            }
        }
    }

}
