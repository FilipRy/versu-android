package com.filip.versu.model.dto;

import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.Objects;

public class JWTWrapperDTO implements Serializable {


    public static final String PREF_ACCESS_TOKEN_KEY = "access_token_key";
    public static final String PREF_TOKEN_TYPE_KEY = "token_type_key";
    public static final String PREF_REFRESHER_TOKEN_KEY = "refresher_token_key";
    public static final String PREF_EXPIRATION_TIMESTAMP_KEY = "PREF_EXPIRATION_TIMESTAMP_KEY";
    public static final String PREF_SCOPE_KEY = "scope_key";
    public static final String PREF_JTI_KEY = "jti_key";

    public String access_token;

    public String token_type;

    public String refresh_token;

    /**
     * This property is returned from authorization server within the JWT object. To determine if a JWT has expired use expirationTimestamp.
     */
    public int expires_in;

    public long expirationTimestamp;

    public String scope;

    public String jti;

    public JWTWrapperDTO() {
    }

    public static SharedPreferences.Editor sharedPreferencesEditorAdapter(SharedPreferences.Editor editor, JWTWrapperDTO jwtWrapperDTO) {

        editor.putString(PREF_ACCESS_TOKEN_KEY, jwtWrapperDTO.access_token);
        editor.putString(PREF_TOKEN_TYPE_KEY, jwtWrapperDTO.token_type);
        editor.putString(PREF_REFRESHER_TOKEN_KEY, jwtWrapperDTO.refresh_token);
        editor.putLong(PREF_EXPIRATION_TIMESTAMP_KEY, jwtWrapperDTO.expirationTimestamp);
        editor.putString(PREF_SCOPE_KEY, jwtWrapperDTO.scope);
        editor.putString(PREF_JTI_KEY, jwtWrapperDTO.jti);

        return editor;
    }

    public static JWTWrapperDTO retrieveFromSharedPreferences(SharedPreferences sharedPreferences) {

        String accessToken = sharedPreferences.getString(PREF_ACCESS_TOKEN_KEY, null);
        if (accessToken == null) {
            return null;
        }

        String refreshToken = sharedPreferences.getString(PREF_REFRESHER_TOKEN_KEY, null);
        if (refreshToken == null) {
            return null;
        }

        String tokenType = sharedPreferences.getString(PREF_TOKEN_TYPE_KEY, null);
        if (tokenType == null) {
            return null;
        }

        Long expirationTimestamp = sharedPreferences.getLong(PREF_EXPIRATION_TIMESTAMP_KEY, -1);
        if (expirationTimestamp == null) {
            return null;
        }

        String scope = sharedPreferences.getString(PREF_SCOPE_KEY, null);
        if (scope == null) {
            return null;
        }

        String jti = sharedPreferences.getString(PREF_JTI_KEY, null);
        if (jti == null) {
            return null;
        }

        JWTWrapperDTO jwtWrapperDTO = new JWTWrapperDTO();
        jwtWrapperDTO.access_token = accessToken;
        jwtWrapperDTO.refresh_token = refreshToken;
        jwtWrapperDTO.expirationTimestamp = expirationTimestamp;
        jwtWrapperDTO.jti = jti;
        jwtWrapperDTO.scope = scope;
        jwtWrapperDTO.token_type = tokenType;

        return jwtWrapperDTO;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JWTWrapperDTO that = (JWTWrapperDTO) o;
        return expires_in == that.expires_in &&
                Objects.equals(access_token, that.access_token) &&
                Objects.equals(token_type, that.token_type) &&
                Objects.equals(refresh_token, that.refresh_token) &&
                Objects.equals(scope, that.scope) &&
                Objects.equals(jti, that.jti);
    }

    @Override
    public int hashCode() {
        return Objects.hash(access_token, token_type, refresh_token, expires_in, scope, jti);
    }
}
