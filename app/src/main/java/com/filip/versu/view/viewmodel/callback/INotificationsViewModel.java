package com.filip.versu.view.viewmodel.callback;

import com.filip.versu.model.dto.NotificationDTO;
import com.filip.versu.model.view.NotificationFeedViewModel;

public interface INotificationsViewModel extends IRefreshablePageableViewModel<NotificationFeedViewModel>, IDisplayingUserProfile {

    public void requestNotificationDetails(NotificationDTO notificationDTO);



    public interface INotificationViewModelCallback extends IRefreshablePageableViewCallback<NotificationFeedViewModel>, IDisplayUserProfileCallback {

        public void displayNotificationDetails(NotificationDTO notificationDTO);
    }
}
