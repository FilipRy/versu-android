package com.filip.versu.view;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.filip.versu.R;
import com.filip.versu.view.fragment.SettingsFragment;
import com.filip.versu.view.viewmodel.ActivityViewModel;
import com.filip.versu.view.viewmodel.callback.IActivityViewModel;

import eu.inloop.viewmodel.base.ViewModelBaseActivity;

public class SettingsActivity extends ViewModelBaseActivity<IActivityViewModel.IActivityViewModelCallback, ActivityViewModel> implements IActivityViewModel.IActivityViewModelCallback {

    public static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar appBar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.settingsLayout, new SettingsFragment()).commit();

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
