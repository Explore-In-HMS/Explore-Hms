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
package com.hms.explorehms.huawei.feature_mlkit.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.hms.explorehms.huawei.feature_mlkit.R;

import static com.hms.explorehms.huawei.feature_mlkit.utils.Utils.showToastMessage;

public class DialogUtils {

    private DialogUtils(){

    }

    public static void showDialogPermissionWarning(Context context, String dialogTitle,
                                                   String dialogMessage, int iconId, String cancelMessage,
                                                   String positiveText, String negativeText) {
        showAlertDialog(context, dialogTitle, dialogMessage, iconId, positiveText, negativeText,
                (dialog, which) -> {

                    if(which == DialogInterface.BUTTON_POSITIVE){
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }else if(which == DialogInterface.BUTTON_NEUTRAL){
                        // proceed with logic by disabling the related features or quit the app.
                        showToastMessage(context, cancelMessage);
                    }
                });
    }

    /**
     * @param context
     * @param dialogTitle
     * @param dialogMessage
     * @param iconId        : such as R.drawable.icon_settings
     * @param cancelMessage
     * @param positiveText
     * @param negativeText
     */
    public static void showDialogHmsCorePermissionWarning(Context context, String dialogTitle,
                                                          String dialogMessage, int iconId, String cancelMessage,
                                                          String positiveText, String negativeText) {

        showAlertDialog(context, dialogTitle, dialogMessage, iconId, positiveText, negativeText,
                (dialog, which) -> {

                    if(which == DialogInterface.BUTTON_POSITIVE){
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", "com.huawei.hwid", null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }else if(which == DialogInterface.BUTTON_NEUTRAL ){
                        // proceed with logic by disabling the related features or quit the app.
                        showToastMessage(context, cancelMessage);
                    }

                });
    }


    /**
     * @param context
     * @param dialogTitle
     * @param dialogMessage
     * @param iconId        : such as R.drawable.icon_settings
     * @param cancelMessage
     * @param positiveText
     * @param negativeText
     */
    public static void showDialogNetworkWarning(Context context, String dialogTitle,
                                                String dialogMessage, int iconId, String cancelMessage,
                                                String positiveText, String negativeText) {
        showAlertDialog(context, dialogTitle, dialogMessage, iconId, positiveText, negativeText,
                (dialog, which) -> {
                    if(which == DialogInterface.BUTTON_POSITIVE){
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        // just mobile network settings
                        context.startActivity(intent);
                    }else if(which == DialogInterface.BUTTON_NEUTRAL){
                        // proceed with logic by disabling the related features or quit the app.
                        showToastMessage(context, cancelMessage);
                    }

                });
    }


    /**
     * @param activity
     * @param dialogTitle
     * @param dialogMessage
     * @param positiveText
     * @param negativeText
     * @param onlineClass
     * @param offlineClass
     */
    public static void showTranslateTypeDialog(Activity activity, String dialogTitle, String dialogMessage, int iconId,
                                               String positiveText, String negativeText, Class<?> onlineClass, Class<?> offlineClass) {
        showAlertDialog(activity, dialogTitle, dialogMessage, iconId, positiveText, negativeText,
                (dialog, which) -> {

                    if(which == DialogInterface.BUTTON_POSITIVE){
                        Utils.startActivity(activity, onlineClass);
                    }else if( which == DialogInterface.BUTTON_NEUTRAL ){
                        Utils.startActivity(activity, offlineClass);
                    }

                });
    }


    public static void showAlertDialog(Context context, String title, String message, int iconId,
                                       String positiveText, String negativeText,
                                       DialogInterface.OnClickListener okListener) {
        //new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme_Dialog))
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(iconId)
                .setPositiveButton(positiveText, okListener)
                .setNeutralButton(negativeText, okListener)
                .create()
                .show();
    }


    private static Dialog dialogImagePeekView = null;

    public static void showDialogImagePeekView(Activity activity, Context context, Bitmap bitmapImage) {
        dialogImagePeekView = new Dialog(activity);
        dialogImagePeekView.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.transparent)));
        dialogImagePeekView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogImagePeekView.setCanceledOnTouchOutside(true);
        dialogImagePeekView.setCancelable(true);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_image_peek_view, null);
        ZoomableImageView imageView = dialogLayout.findViewById(R.id.imageZoomable); // change
        imageView.setImageBitmap(bitmapImage);
        dialogImagePeekView.setContentView(dialogLayout);
        dialogImagePeekView.show();
    }

    /**
     * @param activity
     * @param dialogTitle
     * @param dialogMessage
     * @param iconId
     * @param positiveText
     * @param negativeText
     * @param positiveListener
     */
    public static void showInformationTipsDialog(Activity activity, String dialogTitle, String dialogMessage,int iconId,
                                                 String positiveText, String negativeText,
                                                 View.OnClickListener positiveListener) {

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_information_tips, null);

        TextView title = view.findViewById(R.id.tv_tips_title);
        TextView content = view.findViewById(R.id.tv_tips_detail);
        title.setText(dialogTitle);
        content.setText(dialogMessage);

        Button positiveButton = view.findViewById(R.id.btn_open_source_page);
        positiveButton.setText(positiveText);
        positiveButton.setOnClickListener(positiveListener);

        Button negativeButton = view.findViewById(R.id.btn_close_dialog);
        negativeButton.setText(negativeText);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(view)
                .setCancelable(true);

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);

        negativeButton.setOnClickListener(arg0 ->
            dialog.dismiss()
        );
    }


}
