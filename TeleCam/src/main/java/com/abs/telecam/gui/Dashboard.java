package com.abs.telecam.gui;
import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import com.abs.telecam.R;
import com.abs.telecam.TeleCam;
import com.abs.telecam.absctract.PhotoHandlerActivity;
import com.abs.telecam.helpers.Bluetooth.BluetoothHelper;
import com.abs.telecam.helpers.ImageHelper;
import com.abs.telecam.helpers.gui.BlueToothActionsMenuHelper;

public class Dashboard extends PhotoHandlerActivity
{
    public static final String TAG = "Controller";
    public static String PreferredPeer = "preferredPeer";
    private ImageHelper imageHelper;
    private static final int NUM_PAGES = 2;
    private ScreenSlidePagerAdapter mPagerAdapter;
    ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        TeleCam.bluetoothHelper = new BluetoothHelper(this);
        imageHelper = new ImageHelper(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mPagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }
        });
        TeleCam.clientHandler =  TeleCam.bluetoothHelper.getClientHandlerFor(TAG);
        TeleCam.serverHandler = TeleCam.bluetoothHelper.getServerHandler(TAG);
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
    public void takePicture(String flashMode) {
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

//        if(TeleCam.photoAngleRotation != 0){ TODO fix memory issue
//            imageHelper.saveToFile(imageHelper.rotateImage(TeleCam.photoAngleRotation, imageHelper.getImageFromData(messageBytes)));
//        }else{
//        }
        for(ControllerViewer viewer : TeleCam.controllerViewers){
            viewer.galleryRefresh();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ControllerViewer.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
