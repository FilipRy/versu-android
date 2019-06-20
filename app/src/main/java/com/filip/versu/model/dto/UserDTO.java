package com.filip.versu.model.dto;


import android.content.SharedPreferences;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.filip.versu.model.Location;
import com.filip.versu.model.Searchable;
import com.filip.versu.model.dto.abs.AbsBaseEntityDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO extends AbsBaseEntityDTO<Long> implements Searchable {

    public static final String PREF_KEY_ID = "id";
    public static final String PREF_KEY_NAME = "username";
    public static final String PREF_KEY_EMAIL = "email";
    public static final String PREF_KEY_REGISTRATION_TIME = "registration_time";
    public static final String PREF_KEY_PHOTO_URL = "profile_photo_url";
    public static final String PREF_KEY_QUOTE = "quote_key";


    /**
     * The UTC time, when this user was registered
     */
    public long registrationTime;

    public String username;
    public String email;
    public String password;

    public String quote;

    public String profilePhotoURL;

    /**
     * represent last known location of this user (which is set manually by user)
     */
    public Location location;


    /**
     * this is myLocation, represents the exact location used at refreshing the "nearby" feed.
     */
    public Location myLocation;

    @JsonIgnore
    public JWTWrapperDTO jwtWrapper;


    public UserDTO() {
        super();
        this.myLocation = new Location();
    }

    public UserDTO(UserDTO other) {
        super(other);
        this.registrationTime = other.registrationTime;
        this.username = other.username;
        this.email = other.email;
        this.password = other.password;
        this.profilePhotoURL = other.profilePhotoURL;
        this.location = other.location;
        this.myLocation = other.myLocation;
        this.quote = other.quote;
    }

    @Override
    public String getEntryName() {
        return username;
    }

    public SharedPreferences.Editor sharedPreferencesEditorAdapter(SharedPreferences.Editor editor) {
        editor.putLong(PREF_KEY_ID, super.getId());
        editor.putString(PREF_KEY_EMAIL, email);
        editor.putString(PREF_KEY_NAME, username);
        editor.putString(PREF_KEY_PHOTO_URL, profilePhotoURL);
        editor.putLong(PREF_KEY_REGISTRATION_TIME, registrationTime);
        editor.putString(PREF_KEY_QUOTE, quote);

        editor = Location.sharedPreferencesEditorAdapter(editor, myLocation, true);

        editor = Location.sharedPreferencesEditorAdapter(editor, location, false);

        editor = JWTWrapperDTO.sharedPreferencesEditorAdapter(editor, jwtWrapper);

        return editor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserDTO userDTO = (UserDTO) o;

        if (username != null ? !username.equals(userDTO.username) : userDTO.username != null)
            return false;
        return email != null ? email.equals(userDTO.email) : userDTO.email == null;

    }


    /**
     * Updating user information (caused by external changes).
     *
     * @param other
     * @return
     */
    public boolean updateProperties(UserDTO other) {
        boolean updated = false;
        if (!username.equals(other.username)) {
            updated = true;
            username = other.username;
        }
        if (!email.equals(other.email)) {
            updated = true;
            email = other.email;
        }
        if ((profilePhotoURL != null && !profilePhotoURL.equals(other.profilePhotoURL)) ||
                (profilePhotoURL == null && other.profilePhotoURL != null)) {
            updated = true;
            profilePhotoURL = other.profilePhotoURL;
        }
        if ((location != null && !location.equals(other.location)) ||
                (location == null && other.location != null)) {
            updated = true;
            location = other.location;
        }

        boolean isOtherLocationNonEqual = myLocation != null && !myLocation.equals(other.myLocation);
        boolean isOtherLocationUnknown = other.myLocation == null || other.myLocation.isUnknown();
        boolean forceUpdateMyLocation = (myLocation == null && other.myLocation != null);

        if((isOtherLocationNonEqual && !isOtherLocationUnknown) || forceUpdateMyLocation) {
            updated = true;
            myLocation = other.myLocation;
        }

        if((quote != null && !quote.equals(other.quote) ||
                (quote == null && other.quote != null))) {
            updated = true;
            quote = other.quote;
        }

        return updated;
    }


    public static UserDTO retrieveFromSharedPreferences(SharedPreferences sharedPreferences) {

        Long userID = sharedPreferences.getLong(PREF_KEY_ID, -1l);
        if (userID == -1l) {
            return null;
        }

        String name = sharedPreferences.getString(PREF_KEY_NAME, "");
        if (name.equals("")) {
            return null;
        }
        String email = sharedPreferences.getString(PREF_KEY_EMAIL, "");
        if (email.equals("")) {
            return null;
        }
        String photoURL = sharedPreferences.getString(PREF_KEY_PHOTO_URL, "");

        Long registrationTime = sharedPreferences.getLong(PREF_KEY_REGISTRATION_TIME, -1l);
        if (registrationTime == -1l) {
            return null;
        }
        String quote = sharedPreferences.getString(PREF_KEY_QUOTE, "");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userID);
        userDTO.username = name;
        userDTO.email = email;
        userDTO.profilePhotoURL = photoURL;
        userDTO.registrationTime = registrationTime;
        userDTO.quote = quote;

        /**
         * in this case, location is used to persist the exact location of current loged in user.
         */
        userDTO.myLocation = Location.retrieveFromSharedPreferences(sharedPreferences, true);

        userDTO.location = Location.retrieveFromSharedPreferences(sharedPreferences, false);

        userDTO.jwtWrapper = JWTWrapperDTO.retrieveFromSharedPreferences(sharedPreferences);

        return userDTO;
    }
}
