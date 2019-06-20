package com.filip.versu.view.viewmodel;

import com.filip.versu.exception.ServiceException;
import com.filip.versu.model.dto.NotificationDTO;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.model.view.NotificationFeedViewModel;
import com.filip.versu.service.INotificationService;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.GlobalModelVersion;
import com.filip.versu.service.helper.Page;
import com.filip.versu.service.impl.NotificationService;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.viewmodel.callback.INotificationsViewModel;
import com.filip.versu.view.viewmodel.callback.INotificationsViewModel.INotificationViewModelCallback;

import java.util.List;


public class NotificationViewModel
        extends AbsRefreshablePageableViewModel<INotificationViewModelCallback, NotificationFeedViewModel>
        implements INotificationsViewModel {

    private INotificationService notificationService = NotificationService.instance();
    private IUserSession userSession = UserSession.instance();


    @Override
    protected long getGlobalItemVersionTimestamp() {
        return GlobalModelVersion.getGlobalNotificationTimestamp();
    }

    @Override
    public NotificationFeedViewModel createInstance() {
        return new NotificationFeedViewModel();
    }

    @Override
    public String getInternalStorageKey() {
        return NotificationViewModel.class.getSimpleName().toLowerCase();
    }

    @Override
    public String getLastTimeRefreshInternalStorageKey() {
        return NotificationViewModel.class.getSimpleName().toLowerCase();
    }

    @Override
    public ILocalCacheService<NotificationFeedViewModel> getLocalCache() {
        return LocalCacheFactory.createForNotifications();
    }

    @Override
    public NotificationFeedViewModel retrieveItemsFromBackend(Page<Long> page) throws ServiceException {
        List<NotificationDTO> notificationDTOs = notificationService.findForUser(userSession.getLogedInUser(), page);
        NotificationFeedViewModel notificationFeedViewModel = new NotificationFeedViewModel();
        notificationFeedViewModel.feedItems = notificationDTOs;
        return notificationFeedViewModel;
    }

    @Override
    public void requestDisplayProfileOfUser(UserDTO userDTO) {
        if(getView() != null) {
            getView().displayProfileOfUserFragment(userDTO);
        }
    }

    @Override
    public void requestNotificationDetails(NotificationDTO notificationDTO) {
        if(getView() != null) {
            markNotificationAsSeen(notificationDTO);
            getView().displayNotificationDetails(notificationDTO);
            getView().notifyDatasetChanged();
        }
    }

    private void markNotificationAsSeen(NotificationDTO notificationDTO) {
        notificationDTO.seen = true;
        new MarkAsSeenUpdater(notificationDTO).execute();
    }

    class MarkAsSeenUpdater extends AbsAsynchronTask<Boolean> {

        private NotificationDTO notificationDTO;

        public MarkAsSeenUpdater(NotificationDTO notificationDTO) {
            this.notificationDTO = notificationDTO;
        }

        @Override
        protected Boolean asynchronOperation() throws ServiceException {
            return notificationService.markAsSeen(notificationDTO);
        }

        @Override
        protected void onPostExecuteSuccess(Boolean item) {
            super.onPostExecuteSuccess(item);
            NotificationViewModel.this.persistItemsToInternalStorage(lastLoadedContent);
        }

        @Override
        protected void onPostExecuteError(Boolean item) {
            super.onPostExecuteError(item);
            notificationDTO.seen = false;
            if(getView() != null) {
                getView().notifyDatasetChanged();
            }
        }

        @Override
        protected void displayErrorMessage(String errorMsg) {

        }
    }

}
