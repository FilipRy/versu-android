package com.filip.versu.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.viewmodel.callback.IDisplayingUserProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserRecyclerViewAdapter extends AbsBaseEntityRecyclerViewAdapter<UserDTO> {

    private Context context;
    private IDisplayingUserProfile displayingUserProfile;

    public UserRecyclerViewAdapter(Context context, IDisplayingUserProfile displayingUserProfile) {
        super(context);
        this.context = context;
        this.displayingUserProfile = displayingUserProfile;
    }

    public UserRecyclerViewAdapter(List<UserDTO> recyclerViewItems, Context context, IDisplayingUserProfile displayingUserProfile) {
        super(recyclerViewItems, context);
        this.context = context;
        this.displayingUserProfile = displayingUserProfile;
    }

    @Override
    public AbsBindViewHolder<UserDTO> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_user, parent, false);
        return new VotersRecyclerViewHolder(view);
    }


    abstract class AbsGeneralUserRecyclerViewHolder extends AbsBindViewHolder<UserDTO> {

        protected UserDTO userDTO;

        protected View parentUserView;
        protected CircleImageView userImageView;
        protected TextView usernameTextView;

        public AbsGeneralUserRecyclerViewHolder(View itemView) {
            super(itemView);
            parentUserView = itemView.findViewById(R.id.userRowParent);
            userImageView = (CircleImageView) itemView.findViewById(R.id.user_profile_photo);
            usernameTextView = (TextView) itemView.findViewById(R.id.user_username);

        }

        @Override
        public void bindView(UserDTO entity, int pos) {
            this.userDTO = entity;
            this.usernameTextView.setText(userDTO.username);

            Picasso.with(context).load(userDTO.profilePhotoURL).
                    placeholder(R.mipmap.ic_account_circle_white_48dp).into(userImageView);

            this.parentUserView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayingUserProfile.requestDisplayProfileOfUser(userDTO);
                }
            });
        }
    }

    class VotersRecyclerViewHolder extends AbsGeneralUserRecyclerViewHolder {

        public VotersRecyclerViewHolder(View itemView) {
            super(itemView);
        }
    }

    class UserRecyclerViewHolder extends AbsGeneralUserRecyclerViewHolder {

        public UserRecyclerViewHolder(View itemView) {
            super(itemView);
        }
    }

}
