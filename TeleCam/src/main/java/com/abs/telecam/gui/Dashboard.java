package com.abs.telecam.gui;
import android.app.ActionBar;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import com.abs.telecam.DeviceData;
import com.abs.telecam.R;
import com.abs.telecam.TeleCam;
import com.abs.telecam.absctract.PhotoHandlerActivity;
import com.abs.telecam.helpers.Bluetooth.BluetoothHelper;
import com.abs.telecam.helpers.ImageHelper;
import com.abs.telecam.helpers.gui.BlueToothActionsMenuHelper;
import com.abs.telecam.helpers.gui.ToastHelper;

import java.util.ArrayList;

public class Dashboard extends PhotoHandlerActivity
{
    public static final String TAG = "Controller";
    private static String PreferredPeer = "preferredPeer";
    private int photoAngleRotation = 0;
    private ImageHelper imageHelper;
    private static final int NUM_PAGES = 2;
    private ScreenSlidePagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TeleCam.bluetoothHelper = new BluetoothHelper(this);
        imageHelper = new ImageHelper(this);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
           
//            ActionBar.Tab controllerTab = actionBar.newTab();//.setText(getString(R.string.controller_tab));
//            controllerTab.setTabListener(new DashboardTabListener.TabListener<ControllerTab>(this, getString(R.string.controller_tab), ControllerTab.class));
//            controllerTab.setTag(ControllerTab.class.toString());
//            actionBar.addTab(controllerTab);
//
//            ActionBar.Tab galleryTab = actionBar.newTab();//.setText(getString(R.string.gallery_Tab));
//            galleryTab.setTabListener(new DashboardTabListener.TabListener<GalleryTab>(this, getString(R.string.gallery_Tab), GalleryTab.class));
//            galleryTab.setTag(GalleryTab.class.toString());
//            actionBar.addTab(galleryTab);
//
//            ViewPager pager = (ViewPager) findViewById(R.id.pager);
//
        }

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mPagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
//

        TeleCam.clientHandler =  TeleCam.bluetoothHelper.getClientHandlerFor(TAG);
        TeleCam.serverHandler = TeleCam.bluetoothHelper.getServerHandler(TAG);
    }



    private void setUpSpinner(ArrayList<DeviceData> deviceDataList){
        ArrayAdapter<DeviceData> deviceArrayAdapter = new ArrayAdapter<DeviceData>(this, android.R.layout.simple_spinner_item, deviceDataList);
        deviceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        deviceSpinner.setAdapter(deviceArrayAdapter);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Dashboard.this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Dashboard.PreferredPeer, position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Dashboard.this);
        int preferredPeer = settings.getInt(Dashboard.PreferredPeer, -1);
        if(preferredPeer > -1){
            deviceSpinner.setSelection(preferredPeer);
        }
    }


    private void sendTakePictureRequest(){
        TeleCam.bluetoothHelper.sendMessageToPair(BluetoothHelper.SHOOT, Dashboard.TAG);
    }

    private void sendSavePhotoRequest(){
        TeleCam.bluetoothHelper.sendMessageToPair(BluetoothHelper.SAVE_PHOTO, Dashboard.TAG);
    }

    private void setUpShooterButton() {
        Button clientButton = (Button) findViewById(R.id.shutterButton);
        clientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTakePictureRequest();
            }
        });
    }

    private void setUpSavePhotoButton() {
        Button saveButton = (Button) findViewById(R.id.savePhoto);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSavePhotoRequest();
            }
        });
        saveButton.setEnabled(false);
    }

    private void setUpRotateButtons(){
        Button rotatePhotoRight = (Button) findViewById(R.id.rotateRight);
        Button rotatePhotoLeft = (Button) findViewById(R.id.rotateLeft);
        rotatePhotoRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotatePhoto(-1);
            }
        });
        rotatePhotoLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotatePhoto(1);
            }
        });
    }

    private void rotatePhoto(int direction){
        ImageView imageView = (ImageView) findViewById(R.id.pic_preview);
        photoAngleRotation+=(90*direction);
        imageView.setRotation(photoAngleRotation);
    }

    @Override
    public void onStart() {
        super.onStart();
//        TeleCam.bluetoothHelper.onStart();
//        if (TeleCam.pairedDevices != null) {
//                ArrayList<DeviceData> deviceDataList = new ArrayList<DeviceData>();
//                for (BluetoothDevice device : TeleCam.pairedDevices) {
//                    deviceDataList.add(new DeviceData(device.getName(), device.getAddress()));
//                }
//                setUpSpinner(deviceDataList);
//                setUpShooterButton();
//            } else {
//                ToastHelper.showLong(this, R.string.bluetoothNotAvailableOrNotSupported);
//        }
//        setUpRotateButtons();
//        setUpSavePhotoButton();

    }




    @Override
    protected void onStop() {
        super.onStop();
        TeleCam.bluetoothHelper.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TeleCam.bluetoothHelper.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        TeleCam.bluetoothHelper.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bluetooth_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        BlueToothActionsMenuHelper helper = new BlueToothActionsMenuHelper(this);
        return helper.getActionForItem(item);
    }


    @Override
    public void takePicture() {
        //TODO Remove
    }

    @Override
    public void sendFullResPhoto() {
        //TODO Remove
    }

    @Override
    public void updatePreview(byte[] data) {
        Bitmap image = imageHelper.getImageFromData(data);
        ImageView imageView = (ImageView) findViewById(R.id.pic_preview);
        imageView.setImageBitmap(image);
        findViewById(R.id.savePhoto).setEnabled(true);
    }

    @Override
    public void savePhoto(byte[] messageBytes) {
        imageHelper.saveToFile(messageBytes);
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return ScreenSlidePageFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
