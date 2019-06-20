package com.filip.versu.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.filip.versu.R;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.NotificationDTO.NotificationType;
import com.filip.versu.service.impl.PostService;
import com.filip.versu.view.fragment.PostsTimelineFeedFragment;
import com.filip.versu.view.viewmodel.ActivityViewModel;
import com.filip.versu.view.viewmodel.PostsTimelineFeedViewModel;
import com.filip.versu.view.viewmodel.callback.IActivityViewModel;

import eu.inloop.viewmodel.base.ViewModelBaseActivity;

/**
 * This activity is a container for displaying the posts
 */
public class PostDetailActivity extends ViewModelBaseActivity<IActivityViewModel.IActivityViewModelCallback, ActivityViewModel> implements IActivityViewModel.IActivityViewModelCallback {

    /**
     * the type decides whether
     * 1. detail of one post should be displayed (DETAIL)
     * 2. posts containing a hashtag should be displayed (HASHTAG)
     * 3. posts created in a location should be shown
     */
    public enum PostFeedType {
        DETAIL, HASHTAG, LOCATION
    }

    public static final String POST_FEED_TYPE_KEY = "POST_FEED_TYPE_KEY";
    public static final String DETAIL_TYPE_KEY = "DETAIL_TYPE_KEY";

    private PostFeedType postFeedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followings);

        Toolbar appBar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.posts));

        Bundle extras = getIntent().getExtras();

        postFeedType = (PostFeedType) extras.getSerializable(POST_FEED_TYPE_KEY);

        Fragment fragment = null;

        if(postFeedType == PostFeedType.DETAIL) {

            NotificationType notificationType = (NotificationType) extras.getSerializable(DETAIL_TYPE_KEY);
            Long shoppingItemID = extras.getLong(PostsTimelineFeedFragment.SHOPPING_ITEM_ID);

            if (notificationType == NotificationType.following) {
                //TODO redirect to FOLLOWERS or user profile ?
            } else if (notificationType == NotificationType.post || notificationType == NotificationType.comment || notificationType == NotificationType.post_feedback) {
                fragment = PostsTimelineFeedFragment.newInstance(PostsTimelineFeedViewModel.NewsfeedType.DETAIL, shoppingItemID, null, null);
            }

        } else if (postFeedType == PostFeedType.HASHTAG) {

            String[] feedbackPossibilities = extras.getStringArray(PostsTimelineFeedFragment.FEEDBACK_POSSIBLITIES_KEY);

            String result = feedbackPossibilities[0];
            if(feedbackPossibilities.length > 1) {
                result = result + PostService.POSSIBILITIES_SEPARATOR + feedbackPossibilities[1];
            }

            getSupportActionBar().setTitle("#"+result);
            fragment = PostsTimelineFeedFragment.newInstance(PostsTimelineFeedViewModel.NewsfeedType.HASHTAG, null, feedbackPossibilities, null);

        } else if (postFeedType == PostFeedType.LOCATION) {

            Location location = (Location) extras.getSerializable(PostsTimelineFeedFragment.LOCATION_KEY);
            getSupportActionBar().setTitle(location.name);

            fragment = PostsTimelineFeedFragment.newInstance(PostsTimelineFeedViewModel.NewsfeedType.LOCATION, null, null, location);

        }

        else {
            throw new IllegalArgumentException();
        }


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.add(R.id.child_frame, fragment).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public Class<ActivityViewModel> getViewModelClass() {
        return ActivityViewModel.class;
    }
}
