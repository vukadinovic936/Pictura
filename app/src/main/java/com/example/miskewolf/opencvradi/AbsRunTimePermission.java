package com.example.miskewolf.opencvradi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.View;

/**
 * Created by MiskeWolf on 1/8/2018.
 */

public abstract class AbsRunTimePermission extends Activity {
    private SparseIntArray mErrorString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mErrorString = new SparseIntArray();

    }
    public abstract void onPermissioGranted(int requestCode);

    public void requestAppPermissions(final String[] reqestedPermissions, final int stringId, final int requestedCode) {
        mErrorString.put(requestedCode, stringId);

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean showRequestedPermissions = false;
        for (String permissions : reqestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permissions);
            showRequestedPermissions = showRequestedPermissions || ActivityCompat.shouldShowRequestPermissionRationale(this, permissions);

        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (showRequestedPermissions) {
                Snackbar.make(findViewById(android.R.id.content), stringId, Snackbar.LENGTH_INDEFINITE).setAction("GRANT",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(AbsRunTimePermission.this, reqestedPermissions, requestedCode);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this, reqestedPermissions, requestedCode);
            }
        } else {
            onPermissioGranted(requestedCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionCheck= PackageManager.PERMISSION_GRANTED;
        for(int permission:grantResults){
            permissionCheck = permissionCheck + permission;
        }
        if(grantResults.length>0&& PackageManager.PERMISSION_DENIED==permissionCheck){
    onPermissioGranted(requestCode);
        }else{
            //DISPLAY MESSAGE IF DANGEROUS PERMISSION
            Snackbar.make(findViewById(android.R.id.content),mErrorString.get(requestCode),
                    Snackbar.LENGTH_INDEFINITE).setAction("ENABLE", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(Uri.parse("package:" +getPackageName()));
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                }
            }).show();
        }
    }
}


