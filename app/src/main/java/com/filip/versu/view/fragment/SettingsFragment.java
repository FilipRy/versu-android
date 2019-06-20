package com.filip.versu.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.filip.versu.R;
import com.filip.versu.model.Location;
import com.filip.versu.model.dto.UserDTO;
import com.filip.versu.service.IUserSession;
import com.filip.versu.service.impl.UserSession;
import com.filip.versu.view.SettingsActivity;
import com.filip.versu.view.adapter.GooglePlaceArrayAdapter;
import com.filip.versu.view.viewmodel.SettingsViewModel;
import com.filip.versu.view.viewmodel.callback.ISettingsViewModel;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;


public class SettingsFragment
        extends ViewModelBasePreferenceFragment<ISettingsViewModel.ISettingsViewModelCallback, SettingsViewModel>
        implements ISettingsViewModel.ISettingsViewModelCallback {


    public static final String TAG = SettingsFragment.class.getSimpleName();

    private IUserSession userSession = UserSession.instance();

    private EditTextPreference usernamePreference;
    private EditTextPreference emailPreference;
    private EditTextPreference quotePreference;

    private Preference locationPreference;

    private ProgressDialog createItemProgressDialog;

    private GooglePlaceArrayAdapter mPlaceArrayAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.empty_preferences);

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        PreferenceCategory profileSettings = new PreferenceCategory(preferenceScreen.getContext());
        profileSettings.setTitle(R.string.my_profile);
        profileSettings.setKey("some key");
        preferenceScreen.addPreference(profileSettings);

        usernamePreference = new EditTextPreference(preferenceScreen.getContext());
        usernamePreference.setTitle(R.string.username);
        usernamePreference.setKey("username_key");
        usernamePreference.setSummary(R.string.change_username_prompt);
        usernamePreference.setText(userSession.getLogedInUser().username);

        usernamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                getViewModel().updateUsername((String) o);
                return false;
            }
        });

        emailPreference = new EditTextPreference(preferenceScreen.getContext());
        emailPreference.setTitle(R.string.email);
        emailPreference.setKey("email_key");
        emailPreference.setSummary(R.string.change_email_prompt);
        emailPreference.setText(userSession.getLogedInUser().email);


        emailPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                getViewModel().updateEmail((String) o);
                return false;
            }
        });


        quotePreference = new EditTextPreference(preferenceScreen.getContext());
        quotePreference.setTitle(R.string.quote);
        quotePreference.setKey("quote_key");
        if (userSession.getLogedInUser().quote == null || userSession.getLogedInUser().quote.isEmpty()) {
            quotePreference.setSummary(R.string.tap_to_add_quote);
        } else {
            quotePreference.setSummary(userSession.getLogedInUser().quote);
        }
        quotePreference.setText(userSession.getLogedInUser().quote);

        quotePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                getViewModel().updateQuote((String) o);
                return false;
            }
        });


        locationPreference = new Preference(getContext());
        locationPreference.setTitle(R.string.location);
        if (userSession.getLogedInUser().location == null) {
            locationPreference.setSummary(R.string.location_preference_desc);
        } else {
            locationPreference.setSummary(userSession.getLogedInUser().location.name);
        }

        locationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onLocationPreferenceClicked();
                return false;
            }
        });


        Preference logoutPreference = new Preference(getContext());
        logoutPreference.setTitle(R.string.log_out);

        logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                askUserToLogout();
                return false;
            }
        });

        setModelView(this);

        profileSettings.addPreference(usernamePreference);
        profileSettings.addPreference(emailPreference);
        profileSettings.addPreference(locationPreference);
        profileSettings.addPreference(quotePreference);
        profileSettings.addPreference(logoutPreference);


        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
        mPlaceArrayAdapter = new GooglePlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, null, filter, SettingsActivity.TAG);

    }

    private void onLocationPreferenceClicked() {

        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_update_location);
        dialog.setTitle(R.string.location);


        final AutoCompleteTextView completeTextView = (AutoCompleteTextView) dialog.findViewById(R.id.locationTextView);

        UserDTO logedInUser = userSession.getLogedInUser();
        if (logedInUser.location != null) {
            completeTextView.setText(logedInUser.location.name);
        }

        completeTextView.setThreshold(3);

        completeTextView.setAdapter(mPlaceArrayAdapter);
        completeTextView.setOnItemClickListener(mAutocompleteClickListener);

        ImageView cnclLocation = (ImageView) dialog.findViewById(R.id.cancelLocationBtn);
        cnclLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getViewModel().setLocationToPersist(null);
                completeTextView.setText("");
            }
        });

        Button dialogButton = (Button) dialog.findViewById(R.id.okBtn);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewModel().updateLocation();
                dialog.dismiss();
            }
        });

        Button cancelButton = (Button) dialog.findViewById(R.id.cnclBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Location item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.googleID);
            Log.i(TAG, "Selected: " + item.name);

            getViewModel().setLocationToPersist(item);

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

            Location location = new Location(place);

            getViewModel().setLocationToPersist(location);

            //userDTO.location = new Location(place);
            CharSequence attributions = places.getAttributions();

        }
    };

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Nullable
    @Override
    public Class<SettingsViewModel> getViewModelClass() {
        return SettingsViewModel.class;
    }

    @Override
    public void displayMessage(int msgResID) {
        displayMessage(getString(msgResID));
    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            createItemProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.updating), true);
        } else {
            if (createItemProgressDialog != null) {
                createItemProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void onUpdateEmailCallback() {
        syncPreferencesWithLogedInUser();
    }

    @Override
    public void onUpdateUsernameCallback() {
        syncPreferencesWithLogedInUser();
    }

    @Override
    public void onUpdateQuoteCAllback() {
        syncPreferencesWithLogedInUser();
    }

    @Override
    public void onUpdateLocationCallback() {
        syncPreferencesWithLogedInUser();
    }

    private void syncPreferencesWithLogedInUser() {
        UserDTO userDTO = userSession.getLogedInUser();
        usernamePreference.setText(userDTO.username);
        emailPreference.setText(userDTO.email);
        quotePreference.setText(userDTO.quote);
        if (userDTO.location == null) {
            locationPreference.setSummary(R.string.location_preference_desc);
        } else {
            locationPreference.setSummary(userDTO.location.name);
        }
        if(userDTO.quote == null || userDTO.quote.isEmpty()) {
            quotePreference.setSummary(R.string.tap_to_add_quote);
        } else {
            quotePreference.setSummary(userDTO.quote);
        }
    }

    @Override
    public void displayMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void askUserToLogout() {
        //if user wants to create vote-no -> delete vote-yes
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.log_out);
        builder.setMessage(R.string.log_out_dialog_question);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                getViewModel().logoutUser(getActivity());
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog = builder.create();
        dialog.show();
    }


}
