package com.techart.cidrz.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Validates entries on UI components
 * Created by Kelvin on 17/09/2017.
 */

public final class EditorUtils {

    private EditorUtils() {
    }

    public static boolean editTextValidator(@NonNull String stringValue, @NonNull EditText textView, String message) {
        if (stringValue.isEmpty()){
            textView.setError(message);
            return false;
        } else {
            textView.setError(null);
            return true;
        }
    }


    public static boolean imagePathValdator(@NonNull Context context, @NonNull String stringValue) {
        if (stringValue == null || stringValue.isEmpty()){
            Toast.makeText(context,"Tap to upload sample image",Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}
