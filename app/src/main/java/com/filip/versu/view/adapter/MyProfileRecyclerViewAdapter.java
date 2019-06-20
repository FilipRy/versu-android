package com.filip.versu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.filip.versu.R;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.abs.BaseDTO;
import com.filip.versu.model.view.UserProfileFeedViewModel;
import com.filip.versu.view.viewmodel.MyProfileViewModel;
import com.filip.versu.view.viewmodel.callback.IMyProfileViewModel;


/**
 * This profile displays my profile.
 */
public class MyProfileRecyclerViewAdapter extends AbsPostRecyclerViewAdapter {

    public static final int HEADER_TYPE = 11;

    private UserProfileFeedViewModel userProfileFeedViewModel;


    public MyProfileRecyclerViewAdapter(UserProfileFeedViewModel userProfileFeedViewModel, Context applicationContext, MyProfileViewModel viewModel) {
        super(userProfileFeedViewModel.feedItems, applicationContext, viewModel);
        this.userProfileFeedViewModel = userProfileFeedViewModel;
    }

    @Override
    public void onBindViewHolder(AbsBindViewHolder holder, int position) {
        if (holder instanceof MeCardRecyclerViewHolder) {
            ((MeCardRecyclerViewHolder) holder).bindView(userProfileFeedViewModel.userCard, 0);
        } else if (holder instanceof EmptyHeaderRecyclerViewHolder) {
            holder.bindView(null, 0);
        } else {
            super.onBindViewHolder(holder, position - 1);
        }
    }

    public void setContent(UserProfileFeedViewModel userProfileFeedViewModel) {
        this.userProfileFeedViewModel = userProfileFeedViewModel;
        clear();
        addAllItems(userProfileFeedViewModel.getPageableContent());
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_TYPE;
        }
        int viewType = super.getItemViewType(position);
        return viewType;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public PostDTO getItem(int position) {
        return super.getItem(position - 1);
    }


    @Override
    public void removeItem(PostDTO item) {
        int pos = recyclerViewItems.indexOf(item);
        if(pos != -1) {
            recyclerViewItems.remove(pos);
            notifyItemRemoved(pos + 1);//+1 because pos 0 is header
        }
    }

    @Override
    public AbsBindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_header_user_card, parent, false);
            return new MeCardRecyclerViewHolder(view);
        } else {
            if (viewType == POST_ITEM_TYPE.SIMPLE_MINE.getValue()) {
                View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_post_feed, parent, false);
                return new SimplePostMyProfileRecyclerViewHolder(view);
            } else if (viewType == POST_ITEM_TYPE.DOUBLE_MINE.getValue()) {
                View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_voting_shopping_item, parent, false);
                return new DoublePostRecyclerViewHolder(view, false);
            }
            return null;
        }
    }

    /**
     * This is a view holder for user card of mine.
     */
    class MeCardRecyclerViewHolder extends UserProfileRecyclerViewAdapter.AbsUserCardRecyclerViewHolder {

        private Button editButton;

        private IMyProfileViewModel myProfileViewModel;

        public MeCardRecyclerViewHolder(View itemView) {
            super(itemView, MyProfileRecyclerViewAdapter.this.context, iPostsFeedViewModel);
            myProfileViewModel = (IMyProfileViewModel) iPostsFeedViewModel;
            editButton = (Button) itemView.findViewById(R.id.headerBtn);
        }

        @Override
        public void bindView(UserProfileFeedViewModel.UserCardViewModel entity, int pos) {
            super.bindView(entity, pos);

            editButton.setText(R.string.edit);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myProfileViewModel.requestDisplaySettings();
                }
            });

        }
    }

    /**
     * This view holder is used in MyProfileFragment if it displays the details of some post.
     */
    class EmptyHeaderRecyclerViewHolder extends AbsBindViewHolder {

        public EmptyHeaderRecyclerViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(BaseDTO entity, int pos) {
        }
    }

}
