package com.filip.versu.view.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.dto.PostDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.UserProfileFeedViewModel;
import com.filip.versu.model.view.UserProfileFeedViewModel.UserCardViewModel;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.fragment.FollowingsFragment;
import com.filip.versu.view.viewmodel.UserProfileViewModel;
import com.filip.versu.view.viewmodel.callback.IDisplayingUserFollowers;
import com.filip.versu.view.viewmodel.callback.IPostsFeedViewModel;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This adapter displays the profile of another user (not me).
 */
public class UserProfileRecyclerViewAdapter extends PostFeedRecyclerViewAdapter {

    public static final int HEADER_TYPE = 11;

    private UserProfileFeedViewModel userProfileFeedViewModel;

    private UserDTO logedInUser;

    private Activity activity;

    public UserProfileRecyclerViewAdapter(UserProfileFeedViewModel userProfileFeedViewModel, Context applicationContext, UserProfileViewModel viewModel, Activity activity) {
        super(userProfileFeedViewModel.feedItems, applicationContext, viewModel);
        this.userProfileFeedViewModel = userProfileFeedViewModel;
        logedInUser = UserSession.instance().getLogedInUser();
        this.activity = activity;
    }

    public void setContent(UserProfileFeedViewModel userProfileFeedViewModel) {
        this.userProfileFeedViewModel = userProfileFeedViewModel;
        clear();
        addAllItems(userProfileFeedViewModel.getPageableContent());
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(AbsBindViewHolder holder, int position) {
        if (holder instanceof UserCardRecyclerViewHolder) {
            ((UserCardRecyclerViewHolder) holder).bindView(userProfileFeedViewModel.userCard, 0);
        } else {
            super.onBindViewHolder(holder, position - 1);
        }
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
    public AbsBindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_header_user_card, parent, false);
            return new UserCardRecyclerViewHolder(view, context, iPostsFeedViewModel);
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public PostDTO getItem(int position) {
        return super.getItem(position - 1);
    }


    /**
     * This is an abstract view holder for all user cards (me + other users)
     */
    public static abstract class AbsUserCardRecyclerViewHolder extends AbsBindViewHolder<UserCardViewModel> {

        protected UserCardViewModel userCardViewModel;
        protected IPostsFeedViewModel postsFeedViewModel;

        protected View profileContent;

        protected TextView usernameTextView;
        protected TextView locationTextView;
        protected TextView quoteTextView;
        protected CircleImageView circleImageView;
        protected TextView followersCountView;
        protected TextView followingsCountView;

        protected TextView followersTextView;
        protected TextView followingsTextView;

        protected Context context;

        public AbsUserCardRecyclerViewHolder(View itemView, Context context, IPostsFeedViewModel postsFeedViewModel) {
            super(itemView);
            this.context = context;
            this.postsFeedViewModel = postsFeedViewModel;
            profileContent = itemView.findViewById(R.id.profile_content);
            usernameTextView = (TextView) itemView.findViewById(R.id.usernameText);
            locationTextView = (TextView) itemView.findViewById(R.id.locationText);
            quoteTextView = (TextView) itemView.findViewById(R.id.quoteTextView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.profile_photo);
            followersCountView = (TextView) itemView.findViewById(R.id.followersCountText);
            followersTextView = (TextView) itemView.findViewById(R.id.followersText);
            followingsTextView = (TextView) itemView.findViewById(R.id.followingsText);
            followingsCountView = (TextView) itemView.findViewById(R.id.followingCountText);
        }

        @Override
        public void bindView(final UserCardViewModel entity, int pos) {
            this.userCardViewModel = entity;

            usernameTextView.setText(userCardViewModel.userDTO.username);

            if(userCardViewModel.userDTO.profilePhotoURL == null || userCardViewModel.userDTO.profilePhotoURL.isEmpty()) {
                circleImageView.setImageResource(R.mipmap.ic_account_circle_white_48dp);
            } else {
                Picasso.with(context).load(userCardViewModel.userDTO.profilePhotoURL).
                        placeholder(R.mipmap.ic_account_circle_white_48dp).into(circleImageView);
            }


            followersCountView.setText(Integer.toString(userCardViewModel.followersCount));
            followingsCountView.setText(Integer.toString(userCardViewModel.followingsCount));

            //displaying last known location of the user
            if(entity.userDTO.location != null) {
                locationTextView.setText(entity.userDTO.location.name);

                locationTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postsFeedViewModel.requestDisplayLocationProfile(entity.userDTO.location);
                    }
                });

            } else {
                locationTextView.setText("");
            }

            if(entity.userDTO.quote != null) {
                quoteTextView.setText(entity.userDTO.quote);
            } else {
                quoteTextView.setText("");
            }

            followersTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postsFeedViewModel.requestUserFollowers(entity.userDTO, FollowingsFragment.FollowingType.FOLLOWERS);
                }
            });

            followersCountView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postsFeedViewModel.requestUserFollowers(entity.userDTO, FollowingsFragment.FollowingType.FOLLOWERS);
                }
            });


            followingsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postsFeedViewModel.requestUserFollowers(entity.userDTO, FollowingsFragment.FollowingType.FOLLOWINGS);
                }
            });

            followingsCountView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postsFeedViewModel.requestUserFollowers(entity.userDTO, FollowingsFragment.FollowingType.FOLLOWINGS);
                }
            });


        }
    }

    /**
     * This is a view holder for user card of other users
     */
    class UserCardRecyclerViewHolder extends AbsUserCardRecyclerViewHolder {

        private Button followButton;

        public UserCardRecyclerViewHolder(View itemView, Context context, IDisplayingUserFollowers displayingUserFollowers) {
            super(itemView, context, (IPostsFeedViewModel) displayingUserFollowers);
            followButton = (Button) itemView.findViewById(R.id.headerBtn);
        }

        @Override
        public void bindView(UserCardViewModel entity, int pos) {
            super.bindView(entity, pos);

            if (userCardViewModel.followingDTO != null) {
                followButton.setText(R.string.following);
            } else {
                followButton.setText(R.string.follow);
            }

            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final UserProfileViewModel userProfileViewModel = (UserProfileViewModel) iPostsFeedViewModel;
                    if (userCardViewModel.followingDTO == null) {
                        FollowingDTO followingDTO = new FollowingDTO();
                        followingDTO.createTime = System.currentTimeMillis();
                        followingDTO.target = userCardViewModel.userDTO;
                        followingDTO.creator = logedInUser;
                        userCardViewModel.followingDTO = followingDTO;
                        userProfileViewModel.executeCreateItemTask(followingDTO, null);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                        builder.setTitle(R.string.stop_following_dialog_title);
                        builder.setMessage(context.getString(R.string.stop_following_dialog_text) + " " + userCardViewModel.followingDTO.target.username + " ?");
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userProfileViewModel.executeDeleteItemTask(userCardViewModel.followingDTO, 0, null);
                            }
                        });

                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }
    }

}
