package com.filip.versu.service.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.filip.versu.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public final class ConfigurationReader {

    private static final String TAG = "ConfigurationReader";

    private static Map<String, String> configProperties;

    public static String getConfigValue(String key) {
        return configProperties.get(key);
    }

    public static void init(Context context) {

        configProperties = new HashMap<>();

        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            Set<Object> keys = properties.keySet();

            for (Object prop: keys) {
                String key = (String) prop;
                configProperties.put(key, properties.getProperty(key));
            }

        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

    }

}
