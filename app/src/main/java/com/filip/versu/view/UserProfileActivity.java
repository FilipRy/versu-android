package com.filip.versu.view;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.filip.versu.R;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.fragment.UserProfileFragment;
import com.filip.versu.view.viewmodel.ActivityViewModel;
import com.filip.versu.view.viewmodel.callback.IActivityViewModel;

import eu.inloop.viewmodel.base.ViewModelBaseActivity;

public class UserProfileActivity extends ViewModelBaseActivity<IActivityViewModel.IActivityViewModelCallback, ActivityViewModel> implements IActivityViewModel.IActivityViewModelCallback {

    public static final String USER_KEY = "USER_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar appBar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        UserDTO userDTO = null;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            userDTO = (UserDTO) extras.getSerializable(USER_KEY);
        }

        UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(userDTO);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.add(R.id.profile_frame, userProfileFragment, UserProfileFragment.TAG).commit();

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
