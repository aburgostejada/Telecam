package com.abs.telecam.gui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.abs.telecam.R;

import java.util.List;


public class Preferences extends PreferenceActivity {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onBuildHeaders(List<Header> target) {
            loadHeadersFromResource(R.xml.preference_headers, target);
        }
}
