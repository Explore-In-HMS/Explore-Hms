/*
 *
 *   Copyright 2020. Explore in HMS. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   You may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.genar.hmssandbox.huawei;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.genar.hmssandbox.huawei.baseapp.adapter.KitAdapter;
import com.genar.hmssandbox.huawei.baseapp.library.ProgressDialogScreen;
import com.genar.hmssandbox.huawei.baseapp.library.ZoomableImageView;
import com.genar.hmssandbox.huawei.baseapp.listeners.IDownloadListener;
import com.genar.hmssandbox.huawei.baseapp.model.KitModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.internal.$Gson$Preconditions;
import com.huawei.hms.common.util.Logger;
import com.huawei.hms.feature.install.FeatureInstallManager;
import com.huawei.hms.feature.install.FeatureInstallManagerFactory;
import com.huawei.hms.feature.listener.InstallStateListener;
import com.huawei.hms.feature.model.FeatureInstallException;
import com.huawei.hms.feature.model.FeatureInstallRequest;
import com.huawei.hms.feature.model.FeatureInstallSessionStatus;
import com.huawei.hms.feature.model.InstallState;
import com.huawei.hms.feature.tasks.FeatureTask;
import com.huawei.hms.feature.tasks.listener.OnFeatureCompleteListener;
import com.huawei.hms.feature.tasks.listener.OnFeatureFailureListener;
import com.huawei.hms.feature.tasks.listener.OnFeatureSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

public class Util {

    private static final String TAG = "HmsSandbox.Util";

    private Util() {
        throw new IllegalStateException("Utility class");
    }

    public static void startActivity(Activity activity, Class<?> c){
        activity.startActivity(new Intent(activity,c));
    }

    public static boolean isNetworkAvailable(Context context) {
        int[] networkTypes = {TYPE_MOBILE, TYPE_WIFI};
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null &&
                        activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static void requestFeature(Activity activity, String dynamicFeatureName, KitAdapter.KitViewHolder viewHolder, IDownloadListener listener){
        FeatureInstallManager mFeatureInstallManager;
        mFeatureInstallManager = FeatureInstallManagerFactory.create(activity);

        FeatureInstallRequest request = FeatureInstallRequest.newBuilder()
                // Add a feature name.
                .addModule(dynamicFeatureName)
                .build();

        // Check whether a feature has been installed. If not, call the installFeature() method.
        if (!mFeatureInstallManager.getAllInstalledModules().contains(dynamicFeatureName)){

            ProgressDialogScreen pds = new ProgressDialogScreen(activity);
            pds.showProgressDialog();

            mFeatureInstallManager.registerInstallListener(state -> {
                if (state == null) {
                    Log.d(TAG, "onStateUpdate: state is null");
                    return;
                }
                if (state.status() == FeatureInstallSessionStatus.UNKNOWN) {
                    Log.e(TAG,"installed in unknown status");
                    return;
                }
                if (state.status() == FeatureInstallSessionStatus.INSTALLED) {
                    Log.i(TAG,"installed success ,can use new feature");
                    listener.onSuccessfullyDownloaded(viewHolder.getAdapterPosition());

                    Util.showInfoDialog(activity, activity.getResources().getString(R.string.loadFeatureOnSuccess), true);

                    return;
                }
                if (state.status() == FeatureInstallSessionStatus.FAILED) {
                    Log.e(TAG,"installed failed, errorcode : " + state.errorCode());
                    return;
                }
                if(state.status() == FeatureInstallSessionStatus.REQUIRES_PERSON_AGREEMENT){
                    try {
                        mFeatureInstallManager.triggerUserConfirm(state,activity,1);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG,e.toString());
                    }
                }
                pds.dismissProgressDialog();
            });

            FeatureTask<Integer> task = mFeatureInstallManager.installFeature(request);

            task.addOnListener(new OnFeatureSuccessListener<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    Toast.makeText(activity, "Feature is installing!",Toast.LENGTH_LONG);
                    //SessionID is equal to integer. sessionId = integer;
                }
            });
            task.addOnListener(new OnFeatureFailureListener<Integer>() {
                @Override
                public void onFailure(Exception exception) {
                    if (exception instanceof FeatureInstallException) {
                        int errorCode = ((FeatureInstallException) exception).getErrorCode();
                        Log.d(TAG, activity.getResources().getString(R.string.loadFeatureOnFailure) + errorCode);
                        Util.showInfoDialog(activity,"Error", activity.getResources().getString(R.string.loadFeatureOnFailure), true);

                    } else {
                        Log.e(TAG, activity.getResources().getString(R.string.error) , exception);
                    }
                    pds.dismissProgressDialog();
                }
            });
        }
    }

    public static boolean isFeatureInstalled(Context context, String dynamicFeatureName){
        FeatureInstallManager mFeatureInstallManager;
        mFeatureInstallManager = FeatureInstallManagerFactory.create(context);

        if(mFeatureInstallManager == null){
            mFeatureInstallManager = FeatureInstallManagerFactory.create(context);
        }
        Set<String> moduleNames = mFeatureInstallManager.getAllInstalledModules();
        return moduleNames.contains(dynamicFeatureName);
    }

    public static void openWebPage(Activity activity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }


    public static void showFeatureInstallDialog(Activity activity, KitModel feature,KitAdapter.KitViewHolder viewHolder, IDownloadListener listener){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        //Setting message manually and performing action on button click
        alertBuilder.setMessage("Do you want to install '" + feature.getKitName() + "' module?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> Util.requestFeature(activity,feature.getFeatureName(), viewHolder, listener))
                .setNegativeButton("No", (dialog, id) -> {
                    //  Action for 'NO' Button
                    dialog.cancel();
                });
        //Creating dialog box
        AlertDialog alert = alertBuilder.create();
        //Setting the title manually
        alert.setTitle("Module Install");
        alert.show();
    }

    public static void showFeatureUninstallDialog(Context ctx, KitModel feature, KitAdapter.KitViewHolder viewHolder){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctx);

        alertBuilder.setMessage("Do you want to uninstall '" + feature.getKitName() + "' module?")
                .setCancelable(false)
                .setPositiveButton("Uninstall", (dialog, id) -> Util.uninstallFeature(ctx, feature, viewHolder))
                .setNegativeButton("No", (dialog, id) -> {
                    //  Action for 'NO' Button
                    dialog.cancel();
                });
        //Creating dialog box
        AlertDialog alert = alertBuilder.create();
        //Setting the title manually
        alert.setTitle("Module Install");
        alert.show();
    }

    private static void uninstallFeature(Context context, KitModel kit, KitAdapter.KitViewHolder viewHolder){
        FeatureInstallManager mFeatureInstallManager;
        mFeatureInstallManager = FeatureInstallManagerFactory.create(context);

        List<String> features = new ArrayList<>();
        features.add(kit.getFeatureName());
        FeatureTask<Void> task = mFeatureInstallManager.delayedUninstallFeature(features);
        task.addOnListener(new OnFeatureCompleteListener<Void>() {
            @Override
            public void onComplete(FeatureTask<Void> featureTask) {
                if (featureTask.isComplete()) {
                    if (featureTask.isSuccessful()) {
                        viewHolder.refreshUI();
                        Util.showInfoDialog(context,kit.getKitName() + " is uninstalled successfully", true);
                    } else {
                        Util.showInfoDialog(context,"Error", kit.getKitName() + " could not be deleted! Try again later", true);

                        Exception exception = featureTask.getException();
                        Logger.e(TAG, exception.toString());
                    }
                }
            }
        });
    }

    public static void showInfoDialog(Context context, String infoMessage, boolean cancellable){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setMessage(infoMessage)
                .setCancelable(cancellable)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        //Creating dialog box
        AlertDialog alert = alertBuilder.create();
        //Setting the title manually
        alert.setTitle("Info");
        alert.show();
    }
    public static void showInfoDialog(Context context, String dialogTitle, String infoMessage, boolean cancellable){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setMessage(infoMessage)
                .setCancelable(cancellable)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        //Creating dialog box
        AlertDialog alert = alertBuilder.create();
        //Setting the title manually
        alert.setTitle(dialogTitle);
        alert.show();
    }

    public static void alertDialog(Context context, String title, String message){

        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Continue with delete operation
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    public static Dialog dialogImagePeekView;

    public static void showDialogImagePeekView(Activity activity,Context context, ImageView iv_source) {
        Bitmap bitmap = ((BitmapDrawable)iv_source.getDrawable()).getBitmap();

        dialogImagePeekView = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogImagePeekView.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.transparent)));
        dialogImagePeekView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogImagePeekView.setCanceledOnTouchOutside(true);
        dialogImagePeekView.setCancelable(true);
        View dialogLayout = View.inflate(new ContextThemeWrapper(context, R.style.AppTheme), R.layout.dialog_image_peek_view_base,null);
        ZoomableImageView imageView = dialogLayout.findViewById(R.id.imageZoomable); // change
        MaterialButton button = dialogLayout.findViewById(R.id.btn_back_peek_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImagePeekView.dismiss();
            }
        });

        imageView.setImageBitmap(bitmap);
        dialogImagePeekView.setContentView(dialogLayout);
        dialogImagePeekView.show();
    }

    public static void setToolbar(Activity activity,Toolbar toolbar, String url){
        toolbar.post(() -> {
           toolbar.inflateMenu(R.menu.menu_information);
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.btn_doc){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(url));
                activity.startActivity(browserIntent);
            }
            return false;
        });
    }
}
