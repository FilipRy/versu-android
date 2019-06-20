package com.filip.versu.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.filip.versu.R;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.view.adapter.GooglePlaceArrayAdapter;
import com.filip.versu.view.viewmodel.RegistrationViewModel;
import com.filip.versu.view.viewmodel.callback.IRegistrationViewModel.IRegistrationViewModelCallback;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import eu.inloop.viewmodel.base.ViewModelBaseActivity;

public class SignUpActivity
        extends ViewModelBaseActivity<IRegistrationViewModelCallback, RegistrationViewModel>
        implements IRegistrationViewModelCallback, View.OnClickListener {


    public static final String TAG = SignUpActivity.class.getSimpleName();
    private static final int GOOGLE_API_CLIENT_ID = 0;

    // UI references.
    private EditText mUsernameView;
    private EditText mEmailView;
    private EditText mPasswordView;

    private AutoCompleteTextView locationAutoCompleteView;
    private TextView errorMsgView;
    private View mProgressView;
    private View mLoginFormView;

    private TextView linkLoginView;

    private UserDTO userDTO;

    private GooglePlaceArrayAdapter mPlaceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userDTO = (UserDTO) bundle.getSerializable(StartupActivity.USER_KEY);
        } else {
            userDTO = new UserDTO();
        }

        // Set up the login form.
        mUsernameView = findViewById(R.id.username);
        mEmailView = findViewById(R.id.email);
        errorMsgView = findViewById(R.id.errorMsg);
        locationAutoCompleteView = findViewById(R.id.locationTextView);
        this.linkLoginView = findViewById(R.id.link_login);
        this.mPasswordView = findViewById(R.id.input_password);
        locationAutoCompleteView.setThreshold(3);


        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
        mPlaceArrayAdapter = new GooglePlaceArrayAdapter(this, android.R.layout.simple_list_item_1, null, filter, TAG);
        locationAutoCompleteView.setAdapter(mPlaceArrayAdapter);
        locationAutoCompleteView.setOnItemClickListener(mAutocompleteClickListener);

        Button mSignInButton = (Button) findViewById(R.id.create_account_btn);
        mSignInButton.setOnClickListener(this);
        mLoginFormView = findViewById(R.id.registration_form);
        mProgressView = findViewById(R.id.registration_progress);

        if (userDTO.username != null && userDTO.email != null) {
            mUsernameView.setText(userDTO.username);
            mEmailView.setText(userDTO.email);
        }

        this.linkLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        setModelView(this);
        getViewModel().restoreState();
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Location item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.googleID);
            Log.i(TAG, "Selected: " + item.name);

            userDTO.location = item;

//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(mGoogleApiClient, placeId);
//            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//            Log.i(TAG, "Fetching details for ID: " + item.googleID);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            userDTO.location = new Location(place);
            CharSequence attributions = places.getAttributions();

        }
    };

    @Override
    public Class<RegistrationViewModel> getViewModelClass() {
        return RegistrationViewModel.class;
    }

    @Override
    public void showErrorMessage(int resId) {
        errorMsgView.setVisibility(View.VISIBLE);
        errorMsgView.setText(resId);
    }

    @Override
    public void showErrorMessage(String msg) {
        errorMsgView.setVisibility(View.VISIBLE);
        errorMsgView.setText(msg);
    }

    @Override
    public void hideErrors() {
        // Reset errors.
        mUsernameView.setError(null);
        errorMsgView.setText("");
        errorMsgView.setVisibility(View.GONE);
    }

    @Override
    public void showProgress(final boolean show) {
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void continueToApp() {
        Intent intent = new Intent(this, MainViewActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //TODO add validators
//        String invalidUsernameMsg = Validator.validateUsername(username, getApplicationContext());
//        String invalidEmailMsg = Validator.validateEmail(email, getApplicationContext());

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid email.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            userDTO.username = username;
            userDTO.email = email;
            userDTO.password = password;
            getViewModel().attemptRegister(userDTO);
        }
    }


}
