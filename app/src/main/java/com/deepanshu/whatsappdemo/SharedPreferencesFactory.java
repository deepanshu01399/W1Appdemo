package com.deepanshu.whatsappdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesFactory<T> {

    private static SharedPreferencesFactory mInstance;
    private final Context mContext;
    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    //    private T targetType;

    private SharedPreferencesFactory(Context context) {
        super();
        this.mContext = context;
    }

    public static synchronized SharedPreferencesFactory getInstance(Context context) {
        if (mInstance == null)
            mInstance = new SharedPreferencesFactory(context);

        return mInstance;
    }

    //this is so you don't need to pass context each time
    private static synchronized SharedPreferencesFactory getInstance() {
        if (null == mInstance) {
            throw new IllegalStateException(SharedPreferencesFactory.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return mInstance;
    }

    /**
     * This is a method which will return SharedPreferences object w.r.t. input mode
     * @param PREF_MODE is the mode for Shared Preferences
     * @return
     */
    public SharedPreferences getSharedPreferences(int PREF_MODE) {
        if (mSharedPrefs == null) {
            mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            mEditor = mSharedPrefs.edit();
        }

        return mSharedPrefs;
    }

    public void removeSharedPreferences(String prefsKey)
    {
        mSharedPrefs.edit().remove(prefsKey).apply();
    }

    /**
     * This is a method which will return SharedPreferences.Editor object
     * @param context
     * @return
     */
    public SharedPreferences.Editor getPrefsEditor(Context context) {
        if (mEditor==null)
            getSharedPreferences(MODE_PRIVATE);

        return mEditor;
    }

    // string
    public void writePreferenceValue(String prefsKey, String prefsValue) {
        SharedPreferences.Editor editor = getPrefsEditor(mContext);
        editor.putString(prefsKey, prefsValue);
        editor.apply();
    }

    // string
    public String getPreferenceValue(String prefsKey) {
        return getSharedPreferences(MODE_PRIVATE).getString(prefsKey,"");
    }

    // string
    public void writePreferenceBoolValue(String prefsKey, boolean prefsValue) {
        SharedPreferences.Editor editor = getPrefsEditor(mContext);
        editor.putBoolean(prefsKey, prefsValue);
        editor.apply();
    }

    // string
    public boolean getPreferenceBoolValue(String prefsKey) {
        return getSharedPreferences(MODE_PRIVATE).getBoolean(prefsKey,false);
    }

    // string
    public void writePreferenceIntValue(String prefsKey, int prefsValue) {
        SharedPreferences.Editor editor = getPrefsEditor(mContext);
        editor.putInt(prefsKey, prefsValue);
        editor.apply();
    }
    // string
    public void writePreferenceLongValue(String prefsKey, long prefsValue) {
        SharedPreferences.Editor editor = getPrefsEditor(mContext);
        editor.putLong(prefsKey, prefsValue);
        editor.apply();
    }

    // string
    public int getPreferenceIntValue(String prefsKey) {
        return getSharedPreferences(MODE_PRIVATE).getInt(prefsKey,-1);
    }
    // string
    public long getPreferenceLongValue(String prefsKey) {
        return getSharedPreferences(MODE_PRIVATE).getLong(prefsKey,-1L);
    }



    public void writePreferencesObject(Object object, String KEY) {
        //########### Save object via Gson
        Gson gson = new Gson();
        String json = gson.toJson(object); // myObject - instance of MyObject
        mEditor.putString(KEY, json);
        mEditor.commit();
    }

    public T getPreferencesObject(Class<T> targetClass, String KEY) {
        Gson gson = new Gson();
        String json = getSharedPreferences(MODE_PRIVATE).getString(KEY, null);
        T targetObject = gson.fromJson(json, targetClass);

        return targetObject;
    }

}
