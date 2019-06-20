package com.filip.versu.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.filip.versu.BuildConfig;
import com.filip.versu.R;
import com.filip.versu.view.viewmodel.EntryAppViewModel;
import com.filip.versu.view.viewmodel.callback.IEntryAppViewModel.IEntryAppViewModelCallback;

import eu.inloop.viewmodel.base.ViewModelBaseActivity;
import io.fabric.sdk.android.Fabric;


/**
 * This is an entry point to the app
 */
public class EntryActivity extends ViewModelBaseActivity<IEntryAppViewModelCallback, EntryAppViewModel> implements IEntryAppViewModelCallback {

    public static final String TAG = EntryActivity.class.getSimpleName();

    /**
     * says if application was started after click on notification tray.
     */
    private boolean launchedFromNotification = false;

    private TextView sloganTextView;
    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric fabric = new Fabric.Builder(this).debuggable(BuildConfig.DEBUG).kits(new Crashlytics()).build();
        Fabric.with(fabric);
        setContentView(R.layout.activity_entry);

        setupSplashScreen();

        setModelView(this);
        getViewModel().runStartAppTask(getApplicationContext());

        Intent intent = getIntent();
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                Log.d(TAG, "Bundle is non-null");
                String userDTO = bundle.getString("userDTO");
                if(userDTO != null) {
                    launchedFromNotification = true;
                }
            }
        }

    }

    private void setupSplashScreen() {

        sloganTextView = findViewById(R.id.sloganTextView);
        logoImageView = findViewById(R.id.logoView);

        sloganTextView.setText(R.string.app_slogan, TextView.BufferType.SPANNABLE);
        Spannable span = (Spannable) sloganTextView.getText();
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.tab_home_underline)), 0, 14, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.tab_search_underline)), 14, 22, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.tab_camera_underline)), 22, 32, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.tab_notification_underline)), 32, sloganTextView.getText().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    @Override
    public Class<EntryAppViewModel> getViewModelClass() {
        return EntryAppViewModel.class;
    }

    @Override
    public void continueToApp() {
        Intent intent = new Intent(this, MainViewActivity.class);
        intent.putExtra(MainViewActivity.LAUNCH_FROM_NOTIFICATION_KEY, launchedFromNotification);

        startActivity(intent);
        finish();
    }

    @Override
    public void continueToLogin() {
        Intent intent = new Intent(this, StartupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void continueToRegistration() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}
