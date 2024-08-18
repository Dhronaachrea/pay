package com.skilrock.paypr.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

public interface AppPermissions {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static boolean checkPermission(FragmentActivity fragmentActivity) {
        return ContextCompat.checkSelfPermission(fragmentActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void requestPermission(AppCompatActivity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.requestPermissions(new String[]{Manifest.permission.CAMERA}, 200);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static boolean checkPermissionFoStorage(FragmentActivity fragmentActivity, String[] permissions) {
        boolean requirePermission = false;
        for (String permission : permissions) {
            if (PermissionChecker.checkSelfPermission(fragmentActivity, permission) != PermissionChecker.PERMISSION_GRANTED) {
                requirePermission = true;
            }
        }

        return requirePermission;
    }

    static void requestPermissionForStorageAndCamera(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }
    }
}
