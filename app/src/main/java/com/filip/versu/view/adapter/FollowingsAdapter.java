package com.filip.versu.view.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.FollowingDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.viewmodel.callback.IFollowingsViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingsAdapter extends AbsBaseEntityRecyclerViewAdapter<FollowingDTO> implements Filterable {

    private IFollowingsViewModel followingsViewModel;

    private List<FollowingDTO> filtertedResults;

    /**
     * the user, whose followings/FOLLOWERS are displayed here.
     */
    private UserDTO followingsOwner;

    private UserDTO me;

    private Activity activity;

    public FollowingsAdapter(List<FollowingDTO> recyclerViewItems, UserDTO followingsOwner, Context context, IFollowingsViewModel followingsViewModel, Activity activity) {
        super(recyclerViewItems, context);
        this.context = context;
        this.followingsViewModel = followingsViewModel;
        filtertedResults = new ArrayList<>();
        this.followingsOwner = followingsOwner;
        this.me = UserSession.instance().getLogedInUser();
        this.activity = activity;
    }

    @Override
    public AbsBindViewHolder<FollowingDTO> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_following, parent, false);
        return new FollowingRecyclerViewHolder(view);
    }

    @Override
    public Filter getFilter() {
        return new FollowingFilter(this, recyclerViewItems);
    }


    class FollowingRecyclerViewHolder extends AbsBindViewHolder<FollowingDTO> {

        private FollowingDTO followingDTO;
        private UserDTO anotherUser;

        private View parentUserView;
        private CircleImageView userImageView;
        private TextView usernameTextView;
        private Button addRemoveFollowingButton;

        public FollowingRecyclerViewHolder(View itemView) {
            super(itemView);
            parentUserView = itemView.findViewById(R.id.userRowParent);
            userImageView = (CircleImageView) itemView.findViewById(R.id.user_profile_photo);
            usernameTextView = (TextView) itemView.findViewById(R.id.user_username);
            addRemoveFollowingButton = (Button) itemView.findViewById(R.id.followButton);
        }

        @Override
        public void bindView(FollowingDTO entity, final int pos) {
            this.followingDTO = entity;

            anotherUser = followingDTO.target;//who the @followingsOwner is following

            //if the adapter is part of fragment showing "who is following @followingsOwner".
            if (followingDTO.target.equals(followingsOwner)) {
                anotherUser = followingDTO.creator;
                addRemoveFollowingButton.setText(R.string.remove);//if @followingsOwner == me
            }

            //if the adapter is part of fragment showing "who the @followingsOwner is following".
            if (followingDTO.creator.equals(followingsOwner)) {
                addRemoveFollowingButton.setText(R.string.following);//if @followingsOwner == me
            }


            Picasso.with(context).load(anotherUser.profilePhotoURL).
                    placeholder(R.mipmap.ic_account_circle_white_48dp).into(userImageView);


            this.usernameTextView.setText(anotherUser.username);
            this.parentUserView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followingsViewModel.requestDisplayProfileOfUser(anotherUser);
                }
            });

            if (followingsOwner.equals(me)) {
                this.addRemoveFollowingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //creating new following
                        if (followingDTO.creator == null) {
                            followingsViewModel.executeCreateItemTask(followingDTO, FollowingsAdapter.this.getContent());
                        } else {

                            if(followingDTO.creator.equals(followingsOwner)) {//if I want to stop following somebody
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                                builder.setTitle(R.string.stop_following_dialog_title);
                                builder.setMessage(context.getString(R.string.stop_following_dialog_text) + " " + followingDTO.target.username + " ?");
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        followingsViewModel.executeDeleteItemTask(followingDTO, pos, FollowingsAdapter.this.getContent());
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
                            } else {
                                followingsViewModel.executeDeleteItemTask(followingDTO, pos, FollowingsAdapter.this.getContent());
                            }
                        }

                    }
                });
            } else {
                this.addRemoveFollowingButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    class FollowingFilter extends Filter {

        private final FollowingsAdapter followingsAdapter;
        private final List<FollowingDTO> originalList;
        private final List<FollowingDTO> filteredList;

        public FollowingFilter(FollowingsAdapter followingsAdapter, List<FollowingDTO> originalList) {
            this.followingsAdapter = followingsAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            filteredList.clear();

            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                String usernamePattern = constraint.toString().toLowerCase().trim();

                for (FollowingDTO following : originalList) {
                    if (following.creator.equals(followingsOwner)) {
                        if (following.target.username.startsWith(usernamePattern)) {
                            filteredList.add(following);
                        }
                    } else if (following.target.equals(followingsOwner)) {
                        if (following.creator.username.startsWith(usernamePattern)) {
                            filteredList.add(following);
                        }
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            followingsAdapter.filtertedResults.clear();
            followingsAdapter.filtertedResults.addAll((Collection<? extends FollowingDTO>) results.values);
            followingsAdapter.notifyDataSetChanged();
        }
    }


}
