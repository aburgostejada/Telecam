package com.abs.telecam.gui;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import com.abs.telecam.R;
import com.abs.telecam.helpers.gui.DialogHelper;

import java.io.File;

public class PhotoDetail extends Activity{

    public static  final String PHOTO = "photo";
    private static final int EDIT_PHOTO = 1;
    private String photoUri;
    DialogHelper dialogHelper;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        dialogHelper = new DialogHelper(this);
        loadImage();
    }

    private void loadImage(){
        final String path = getIntent().getStringExtra(PHOTO);
        photoUri = path;
        if(!(path != null && path.equals(""))){
            WebView photoDetail = (WebView) findViewById(R.id.photoDetail);
            photoDetail.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            photoDetail.getSettings().setAllowFileAccess(true);
            photoDetail.getSettings().setBuiltInZoomControls(true);
            WebSettings webSettings = photoDetail.getSettings();
            photoDetail.setInitialScale(30);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            String imagePath = "file://"+ path;
            String imageCenter = "<img style='position: absolute; top: 25%;' src='"+ imagePath + "'>";
            String html = "<html><head></head><body style='background:black;'>"+imageCenter+"</body></html>";
            photoDetail.loadDataWithBaseURL("", html, "text/html","utf-8", "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_detail_actions, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        if (item != null) {
            setShareIntent((ShareActionProvider) item.getActionProvider());
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_photo:
                dialogHelper.setYesNoDialog(R.string.delete_action_dialog_title, R.string.delete_action_dialog_message, new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        boolean success = deletePhoto();
                        finish();
                        return success;
                    }
                },new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            return true;
                        }
                 });
                return true;
            case R.id.edit_photo:
                 final Intent i = new Intent(Intent.ACTION_EDIT);
                 i.setDataAndType(Uri.fromFile(new File(photoUri)), "image/png");
                 i.putExtra("return-data", true);
                 startActivityForResult(i, EDIT_PHOTO);
                return true;
            case android.R.id.home:
                finish();
                return true;

        }
        return false;
    }

    private boolean deletePhoto(){
        File photo = new File(photoUri);
        return photo.delete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_PHOTO) {
            finish();
        }
    }

    private void setShareIntent(ShareActionProvider mShareActionProvider) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri uri = Uri.fromFile(new File(photoUri));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.share_subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT,R.string.share_extra);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}