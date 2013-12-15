/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abs.telecam.gui;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.abs.telecam.R;
import com.abs.telecam.TeleCam;
import com.abs.telecam.adapters.DeviceBluetoothAdapter;
import com.abs.telecam.helpers.gui.DialogHelper;
import com.abs.telecam.helpers.gui.ToastHelper;
import com.abs.telecam.util.Images;
import com.abs.telecam.util.Utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.ArrayList;


public class ControllerViewer extends Fragment implements AdapterView.OnItemClickListener{
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    private static int CONTROLLER = 0;
    private static int GALLERY = 1;

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    private ControllerBindings controllerBindings;
    private GalleryBindings galleryBindings;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */


    public static ControllerViewer create(int pageNumber) {
        ControllerViewer fragment = new ControllerViewer();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        TeleCam.controllerViewers[pageNumber] = fragment;
        return fragment;
    }

    public ControllerViewer() {

    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                TeleCam.dismissProgressDialog();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    TeleCam.newDevicesArrayAdapter.add(device);
                    TeleCam.newDevicesArrayAdapter.notifyDataSetChanged();
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (TeleCam.newDevicesArrayAdapter.getCount() == 0) {
                    TeleCam.dismissProgressDialog();
                }
            }
        }
    };





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        controllerBindings = new ControllerBindings(getActivity());
        galleryBindings = new GalleryBindings(getActivity());
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        TeleCam.newDevicesArrayAdapter = new DeviceBluetoothAdapter(getActivity(), android.R.layout.select_dialog_singlechoice);
        getActivity().registerReceiver(mReceiver, filter);
    }


    @Override
    public void onStop() {
        super.onStop();
        TeleCam.bluetoothHelper.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isGallery()){
            galleryBindings.mImageFetcher.closeCache();
        }else {
            TeleCam.bluetoothHelper.onDestroy();
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isGallery()){
            galleryRefresh();
        }else {
            controllerBindings.updateDevicesListForCurrentView();
            TeleCam.bluetoothHelper.onStart();
        }
    }

    public void galleryRefresh(){
        if(isGallery()){
            galleryBindings.mImageFetcher.setExitTasksEarly(false);
            galleryBindings.mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isGallery()){
            galleryBindings.mImageFetcher.setPauseWork(false);
            galleryBindings.mImageFetcher.setExitTasksEarly(true);
            galleryBindings. mImageFetcher.flushCache();
        }
    }

    private boolean isGallery(){
        return mPageNumber == GALLERY;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isGallery()){
            inflater.inflate(R.menu.clear_cache, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_cache:
                galleryBindings.mImageFetcher.clearCache();
                ToastHelper.showLong(getActivity(), R.string.cachedCleared);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView;
        if(mPageNumber == CONTROLLER){
            rootView = (ViewGroup) inflater.inflate(R.layout.controller, container, false);
            controllerBindings.setUpEventsForController(rootView);
            TeleCam.bluetoothHelper.onStart();
        }else{
            rootView = (ViewGroup) inflater.inflate(R.layout.gallery, container, false);
            setHasOptionsMenu(true);
            galleryBindings.setUpEventsForGallery(this, rootView);
        }

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        String uri = Images.getPhotos()[(int)id];
        final Intent i = new Intent(getActivity(), PhotoDetail.class);
        i.putExtra(PhotoDetail.PHOTO, uri);
        if(isGallery()){
            if (Utils.hasJellyBean()) {
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
                getActivity().startActivity(i, options.toBundle());
            } else {
                startActivity(i);
            }
        }
    }
}

