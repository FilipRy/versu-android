package com.filip.versu.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.filip.versu.R;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.fragment.IntroFragment;
import com.filip.versu.view.viewmodel.StartupViewModel;
import com.filip.versu.view.viewmodel.callback.IStartupViewModel;
import com.robohorse.pagerbullet.PagerBullet;

import java.util.ArrayList;
import java.util.List;

import eu.inloop.viewmodel.base.ViewModelBaseActivity;

public class StartupActivity extends ViewModelBaseActivity<IStartupViewModel.IStartupViewModelCallback, StartupViewModel> implements IStartupViewModel.IStartupViewModelCallback {

    private IUserSession userSession = UserSession.instance();

    public static final String USER_KEY = "user_data_serializable";

    // UI references.
    private View mProgressView;
    private View mLoginFormView;
    private Button continueToAppBtn;

    private PagerBullet viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        setModelView(this);

        // Set up the login form.
        continueToAppBtn = findViewById(R.id.continue_to_app);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        viewPager = findViewById(R.id.viewpager);


        continueToAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StartupActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();

            }
        });

        int pixelSizeMargin = getResources().getDimensionPixelSize(R.dimen.main_view_side_margin);
        viewPager.getViewPager().setPageMargin(pixelSizeMargin);
        viewPager.getViewPager().setPageMarginDrawable(R.color.app_bg);

        int activeColor = ContextCompat.getColor(this, R.color.tab_home_underline);
        int inactiveColor = ContextCompat.getColor(this, R.color.gray_light);
        viewPager.setIndicatorTintColorScheme(activeColor, inactiveColor);

        List<Fragment> introFragments = new ArrayList<>();
        introFragments.add(IntroFragment.newInstance(IntroFragment.IntroFragmentType.ASK));
        introFragments.add(IntroFragment.newInstance(IntroFragment.IntroFragmentType.VOTE));
        introFragments.add(IntroFragment.newInstance(IntroFragment.IntroFragmentType.DISCOVER));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), introFragments);

        viewPager.setAdapter(viewPagerAdapter);
    }


    @Override
    public Class<StartupViewModel> getViewModelClass() {
        return StartupViewModel.class;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragmentList = fragments;
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
