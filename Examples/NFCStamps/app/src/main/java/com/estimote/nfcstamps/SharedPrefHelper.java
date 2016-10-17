package com.estimote.nfcstamps;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {

    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPrefHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_key), Context.MODE_PRIVATE);
    }

    public void increaseStampAmount() {
        int currentValue = sharedPreferences.getInt(context.getString(R.string.shared_pref_stamps_amount), 0);
        sharedPreferences.edit().putInt(context.getString(R.string.shared_pref_stamps_amount), currentValue + 1).apply();
    }

    public void saveStampAmountValue(int stampAmount) {
        sharedPreferences.edit().putInt(context.getString(R.string.shared_pref_stamps_amount), stampAmount).apply();
    }

    public int getStampAmountValue() {
        return sharedPreferences.getInt(context.getString(R.string.shared_pref_stamps_amount), 0);
    }
}
