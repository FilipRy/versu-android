package com.filip.versu.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.service.factory.LocalCacheFactory;
import com.filip.versu.service.helper.GlobalModelVersion;
import com.filip.versu.service.helper.PhotoSize;
import com.filip.versu.view.custom.SlidingTabLayout;
import com.filip.versu.view.fragment.MyProfileFragment;
import com.filip.versu.view.fragment.parent.ActivityContainerFragment;
import com.filip.versu.view.fragment.parent.FriendsShoppingFeedContainerFragment;
import com.filip.versu.view.fragment.parent.SearchContainerFragment;
import com.filip.versu.view.viewmodel.ActivityViewModel;
import com.filip.versu.view.viewmodel.callback.IActivityViewModel.IActivityViewModelCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import eu.inloop.viewmodel.base.ViewModelBaseActivity;
import io.github.rockerhieu.emojiconize.Emojiconize;

public class MainViewActivity
        extends ViewModelBaseActivity<IActivityViewModelCallback, ActivityViewModel>
        implements IActivityViewModelCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static final String TAG = MainViewActivity.class.getSimpleName();
    public static final int MY_REQUEST_ACCESS_FINE_LOCATION = 42;
    public static final String LAUNCH_FROM_NOTIFICATION_KEY = "notification_key";
    public static final int SPLASH_SCREEN_TIMEOUT = 3000;

    private View mainLayout;
    private ViewPager viewPager;


    private int pagerCurrentItem;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Emojiconize.activity(this).go();

        super.onCreate(savedInstanceState);

        Log.d(TAG, "Started creating MainViewActivity");

        setContentView(R.layout.activity_main_view);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            GlobalModelVersion.refreshGlobalNotificationTimestamp();

            boolean displayNotificationFragment = bundle.getBoolean(LAUNCH_FROM_NOTIFICATION_KEY);
            pagerCurrentItem = displayNotificationFragment ? 3 : 0;

            Log.d(TAG, "Launched with displyNotifFragment = " + displayNotificationFragment);

        }

        setupMainScreen();


        buildGoogleApiClient();


    }

    private void setupMainScreen() {
        mainLayout = findViewById(R.id.mainLayout);
        viewPager = (ViewPager) findViewById(R.id.mainViewpager);

        int pixelSizeMargin = getResources().getDimensionPixelSize(R.dimen.main_view_side_margin);
        viewPager.setPageMargin(pixelSizeMargin);
        viewPager.setPageMarginDrawable(R.color.app_bg);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FriendsShoppingFeedContainerFragment());
        fragments.add(new SearchContainerFragment());
        fragments.add(new Fragment());
        fragments.add(new ActivityContainerFragment());
        fragments.add(MyProfileFragment.newInstance());

        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(mainViewPagerAdapter);
        viewPager.setCurrentItem(pagerCurrentItem);

        Log.d(TAG, "MainViewPagerAdapter initialized");

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.viewPagerTab);
        slidingTabLayout.setCustomTabView(R.layout.tab_main_view, R.id.custom_text);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setSelectedIndicatorColors(createSelectedIndicatorColorForTabLayout());

        Log.d(TAG, "SlidingTabLayout initialized");


        LocalCacheFactory.init(getApplicationContext());
        PhotoSize.init(getApplicationContext());

        Log.d(TAG, "MainViewActivity is now fully initialized");
    }


    private int[] createSelectedIndicatorColorForTabLayout() {
        int homeColor = ContextCompat.getColor(this, R.color.tab_home_underline);
        int searchColor = ContextCompat.getColor(this, R.color.tab_search_underline);
        int cameraColor = ContextCompat.getColor(this, R.color.tab_camera_underline);
        int notifColor = ContextCompat.getColor(this, R.color.tab_notification_underline);
        int profileColor = ContextCompat.getColor(this, R.color.tab_profile_underline);

        int[] selectors = new int[]{homeColor, searchColor, cameraColor, notifColor, profileColor};
        return selectors;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            accessLocation();
        }

    }

    private void accessLocation() {

        @SuppressLint("MissingPermission")
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            getViewModel().storeUserLocation(mLastLocation);
        } else {
            //Toast.makeText(this, R.string.location_unavailable, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.

        checkLocationPermission();
    }


    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    accessLocation();
                } else {
                    Toast.makeText(this, R.string.allow_access_location, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void setBackground(int colorCodeID) {
        mainLayout.setBackgroundResource(colorCodeID);
        viewPager.setPageMarginDrawable(colorCodeID);
    }

    @Override
    public Class<ActivityViewModel> getViewModelClass() {
        return ActivityViewModel.class;
    }


    public class MainViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();

        public MainViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragmentList = fragments;
        }

        public int getPageIconResID(int position) {
            if (position == 0) {
                return R.drawable.home;
            } else if (position == 1) {
                return R.drawable.search;
            } else if (position == 2) {
                return R.drawable.camera;
            } else if (position == 3) {
                return R.drawable.bell;
            } else {
                return R.drawable.account;
            }
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }


}
