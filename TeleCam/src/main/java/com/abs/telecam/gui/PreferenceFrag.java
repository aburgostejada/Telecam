package com.abs.telecam.gui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.abs.telecam.R;


public class PreferenceFrag extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }
}
