package com.filip.versu.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.filip.versu.R;
import com.filip.versu.model.dto.NotificationDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.NotificationFeedViewModel;
import com.filip.versu.view.PostDetailActivity;
import com.filip.versu.view.adapter.AbsBaseEntityRecyclerViewAdapter;
import com.filip.versu.view.adapter.NotificationRecyclerViewAdapter;
import com.filip.versu.view.custom.SimpleDividerItemDecoration;
import com.filip.versu.view.viewmodel.NotificationViewModel;
import com.filip.versu.view.viewmodel.callback.INotificationsViewModel.INotificationViewModelCallback;

import java.util.ArrayList;

public class NotificationsFragment
        extends AbsRefreshablePageableFragment<INotificationViewModelCallback, NotificationFeedViewModel, NotificationViewModel>
        implements INotificationViewModelCallback {

    private ProfileDisplayer profileDisplayer = new ProfileDisplayer();


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getApplicationContext(), R.drawable.notification_divider));
        getViewModel().requestItemsFromInternalStorage(getActivity().getApplicationContext());
    }

    @Override
    public AbsBaseEntityRecyclerViewAdapter createRecyclerViewAdapter() {
        NotificationRecyclerViewAdapter adapter =  new NotificationRecyclerViewAdapter(new ArrayList<NotificationDTO>(), getActivity(), getViewModel());
        return adapter;
    }

    @Override
    public void setModelView() {
        setModelView(this);
    }

    @Nullable
    @Override
    public Class<NotificationViewModel> getViewModelClass() {
        return NotificationViewModel.class;
    }

    @Override
    public void displayProfileOfUserFragment(UserDTO userDTO) {
        profileDisplayer.displayUserProfile(userDTO, getActivity());
    }

    @Override
    public void displayNotificationDetails(NotificationDTO notificationDTO) {
        if(notificationDTO.type == NotificationDTO.NotificationType.post ||
                notificationDTO.type == NotificationDTO.NotificationType.post_feedback ||
                notificationDTO.type == NotificationDTO.NotificationType.comment) {

            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            intent.putExtra(PostDetailActivity.POST_FEED_TYPE_KEY, PostDetailActivity.PostFeedType.DETAIL);
            intent.putExtra(PostsTimelineFeedFragment.SHOPPING_ITEM_ID, notificationDTO.contentEntityID);
            intent.putExtra(PostDetailActivity.DETAIL_TYPE_KEY, notificationDTO.type);
            startActivity(intent);

        } else if (notificationDTO.type == NotificationDTO.NotificationType.following) {
            profileDisplayer.displayUserProfile(notificationDTO.userDTO, getActivity());
        }

    }
}
