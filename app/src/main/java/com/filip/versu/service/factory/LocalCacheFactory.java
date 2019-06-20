package com.filip.versu.service.factory;

import android.content.Context;

import com.filip.versu.model.view.CommentsFeedViewModel;
import com.filip.versu.model.view.FollowingsFeedViewModel;
import com.filip.versu.model.view.NotificationFeedViewModel;
import com.filip.versu.model.view.PostFeedViewModel;
import com.filip.versu.model.view.PostFeedbackFeedViewModel;
import com.filip.versu.model.view.SearchHistoryFeedViewModel;
import com.filip.versu.model.view.UserProfileFeedViewModel;
import com.filip.versu.service.cache.ILocalCacheService;
import com.filip.versu.service.cache.LocalCacheService;
import com.filip.versu.service.helper.Helper;
import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.JsonSerializer;


public class LocalCacheFactory {

    /**
     * The sizes of max sizes are in bytes.
     */

    public static final String FOLLOW_CACHE_FILENAME = "follow_cache";
    public static final int FOLLOW_RAM_CACHE_MAX_SIZE = 10000;//ca 7 followings per page, page = ca 1000 characters, 1B = 1 char
    public static final int FOLLOW_DISK_CACHE_MAX_SIZE = 50000;

    public static final String SHOPPING_ITEM_FILENAME = "shopping_items";
    public static final int SHOPPING_RAM_CACHE_MAX_SIZE = 15000;
    public static final int SHOPPING_DISK_CACHE_MAX_SIZE = 50000;

    public static final String USER_PROFILE_ITEM_FILENAME = "user_profile";
    public static final int USER_PROFILE_RAM_CACHE_MAX_SIZE = 15000;
    public static final int USER_PROFILE_DISK_CACHE_MAX_SIZE = 50000;

    public static final String SEARCH_HISTORY_FILENAME = "search_history";
    public static final int SEARCH_HISTORY_RAM_CACHE_MAX_SIZE = 10000;
    public static final int SEARCH_HISTORY_DISK_CACHE_MAX_SIZE = 50000;

    public static final String MY_PROFILE_FILENAME = "my_profile";
    public static final int MY_PROFILE_RAM_CACHE_MAX_SIZE = 15000;
    public static final int MY_PROFILE_DISK_CACHE_MAX_SIZE = 50000;

    public static final String COMMENTS_FILENAME = "comments";
    public static final int COMMENTS_RAM_CACHE_MAX_SIZE = 25000;
    public static final int COMMENT_DISK_CACHE_SIZE = 50000;

    public static final String POST_FEEDBACK_FILENAME = "post_feedback";
    public static final int POST_FEEDBACK_RAM_CACHE_MAX_SIZE = 15000;
    public static final int POST_FEEDBACK_DISK_CACHE_MAX_SIZE = 50000;

    public static final String NOTIFICATION_FILENAME = "notification";
    public static final int NOTIFICATION_RAM_CACHE_MAX_SIZE = 10000;
    public static final int NOTIFICATION_DISK_CACHE_SIZE = 50000;


    private static Context context;

    public static void init(Context context) {
        LocalCacheFactory.context = context;
    }


    public static ILocalCacheService<FollowingsFeedViewModel> createForMyFollowings() {

        LocalCacheService<FollowingsFeedViewModel> localCache =
                new LocalCacheService<>(FollowingsFeedViewModel.class, FOLLOW_CACHE_FILENAME, 0, FOLLOW_RAM_CACHE_MAX_SIZE, FOLLOW_DISK_CACHE_MAX_SIZE, context);

        return localCache;
    }


    public static ILocalCacheService<UserProfileFeedViewModel> createForUserProfile() {

        LocalCacheService<UserProfileFeedViewModel> localCache =
                new LocalCacheService<>(UserProfileFeedViewModel.class, USER_PROFILE_ITEM_FILENAME, 0, USER_PROFILE_RAM_CACHE_MAX_SIZE, USER_PROFILE_DISK_CACHE_MAX_SIZE, context);

        return localCache;
    }

    public static ILocalCacheService<PostFeedViewModel> createForShoppingItems() {

        LocalCacheService<PostFeedViewModel> localCache =
                new LocalCacheService<>(PostFeedViewModel.class, SHOPPING_ITEM_FILENAME, 0, SHOPPING_RAM_CACHE_MAX_SIZE, SHOPPING_DISK_CACHE_MAX_SIZE, context);

        return localCache;
    }

    public static ILocalCacheService<UserProfileFeedViewModel> createForMyProfile() {

        LocalCacheService<UserProfileFeedViewModel> localCache =
                new LocalCacheService<>(UserProfileFeedViewModel.class, MY_PROFILE_FILENAME, 0, MY_PROFILE_RAM_CACHE_MAX_SIZE, MY_PROFILE_DISK_CACHE_MAX_SIZE, context);

        return localCache;
    }

    public static ILocalCacheService<SearchHistoryFeedViewModel> createForSearchHistory() {

        LocalCacheService<SearchHistoryFeedViewModel> localCache =
                new LocalCacheService<>(SearchHistoryFeedViewModel.class, SEARCH_HISTORY_FILENAME, 0, SEARCH_HISTORY_RAM_CACHE_MAX_SIZE, SEARCH_HISTORY_DISK_CACHE_MAX_SIZE, context);

        return localCache;
    }

    public static ILocalCacheService<PostFeedbackFeedViewModel> createForPostFeedback() {

        LocalCacheService<PostFeedbackFeedViewModel> localCache =
                new LocalCacheService<>(PostFeedbackFeedViewModel.class, POST_FEEDBACK_FILENAME, 0, POST_FEEDBACK_RAM_CACHE_MAX_SIZE, POST_FEEDBACK_DISK_CACHE_MAX_SIZE, context);

        return localCache;
    }


    public static ILocalCacheService<CommentsFeedViewModel> createForComments() {

        LocalCacheService<CommentsFeedViewModel> localCache =
                new LocalCacheService<>(CommentsFeedViewModel.class, COMMENTS_FILENAME, 0, COMMENTS_RAM_CACHE_MAX_SIZE, COMMENT_DISK_CACHE_SIZE, context);

        return localCache;
    }


    public static ILocalCacheService<NotificationFeedViewModel> createForNotifications() {

        LocalCacheService<NotificationFeedViewModel> localCache =
                new LocalCacheService<>(NotificationFeedViewModel.class, NOTIFICATION_FILENAME, 0, NOTIFICATION_RAM_CACHE_MAX_SIZE, NOTIFICATION_DISK_CACHE_SIZE, context);

        return localCache;
    }

    public static DualCache<Long> createForLastTimeRefreshTimestamp() {

        CacheSerializer<Long> jsonSerializer = new JsonSerializer<>(Long.class);

        return new Builder<>("last_time_refresh_cache", Helper.APP_VERSION, Long.class).
                useSerializerInRam(500, jsonSerializer).
                useSerializerInDisk(500, true, jsonSerializer, context).build();
    }


}
