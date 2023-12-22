/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.hms.explorehms.pushkit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.github.clemp6r.futuroid.Async;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hms.explorehms.R;
import com.hms.explorehms.Util;
import com.hms.explorehms.pushkit.dialog.GetNotificationDialog;
import com.hms.explorehms.pushkit.dialog.PushMessageDialogPushKit;
import com.huawei.agconnect.AGConnectOptions;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.aaid.entity.AAIDResult;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;
import com.huawei.hms.push.RemoteMessage;

import java.util.Objects;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class MainActivityPushKit extends AppCompatActivity {

    private static final String TAG = "PUSH_KIT";
    private static final int PERMISSION_REQUEST = 99;
    private Thread AIDThread;
    private Thread subjectTokenThread;
    private Thread deleteTokenWithStringThread;
    private Thread deleteTokenThread;


    /**
     * ViewFlipper screen index
     */
    private int currentIndex = 0;

    /**
     * Base Views
     */
    private Button btnPrevious;
    private Button btnNext;
    private Button btnGetNotification;
    private ViewFlipper viewFlipper;
    private AlertDialog dialog;

    /**
     * AAID related views
     */
    private RadioButton rbAAIDAutoApply;
    private RadioButton rbAAIDManApply;
    private Button btnGetAAID;
    private Button btnDeleteAAID;
    private TextView tvAAIDResult;
    private TextView tvAAIDOprResult;

    /**
     * Token related views
     */
    private Button btnGetToken;
    private Button btnDeleteToken;
    private TextView tvTokenResult;
    private TextView tvTokenOprResult;

    /**
     * Push Preferences related views
     */
    private SwitchCompat switchPushOnOff;
    private Button btnSubTopic;
    private Button btnUnSubTopic;
    private SwitchCompat switchAutoInitOnOff;
    private TextView tvPushOnOffResult;
    private TextView tvSubUnsubResult;
    private TextView tvAutoInitOnOffResult;
    private TextView tvSubTopic;
    private Boolean isFirst = true;

    /**
     * Push Message related variables
     */
    private static final String EXPLOREHMS_ACTION = "com.hms.explorehms.feature_pushkit.action";
    private PushMessageDialogPushKit pushMessageDialog;
    private GetNotificationDialog getNotificationDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pushkit);
        setupToolbar();
        initUI();
        initListener();
        initReceiver();
        isPermissionGranted();

        //Enable push notification
        setReceiveNotifyMsg(false);
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_txt_pushkit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void isPermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST && (grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED))) {
            isPermissionGranted();
        }
    }

    /**
     * Initialize UI Elements
     */
    private void initUI() {

        viewFlipper = findViewById(R.id.vf_push_kit);

        btnPrevious = findViewById(R.id.btn_prev_func_push_kit);
        btnNext = findViewById(R.id.btn_next_func_push_kit);
        btnGetNotification = findViewById(R.id.btn_push_kit_send_notification);

        rbAAIDAutoApply = findViewById(R.id.rb_auto_apply_aaid_push);
        rbAAIDManApply = findViewById(R.id.rb_man_apply_aaid_push);
        btnGetAAID = findViewById(R.id.btn_pushk_get_aaid);
        btnDeleteAAID = findViewById(R.id.btn_pushk_delete_aaid);
        tvAAIDResult = findViewById(R.id.tv_push_aaid_result);
        tvAAIDOprResult = findViewById(R.id.tv_push_aaid_operation_result);

        btnGetToken = findViewById(R.id.btn_pushk_get_token);
        btnDeleteToken = findViewById(R.id.btn_pushk_delete_token);
        tvTokenResult = findViewById(R.id.tv_push_token_result);
        tvTokenOprResult = findViewById(R.id.tv_push_token_operation_result);

        switchPushOnOff = findViewById(R.id.swt_push_onoff);
        btnSubTopic = findViewById(R.id.btn_push_sub_unsub);
        btnUnSubTopic = findViewById(R.id.btn_push_unsub_unsub);
        switchAutoInitOnOff = findViewById(R.id.swt_push_auto_in_endis);

        tvPushOnOffResult = findViewById(R.id.tv_push_onoff_opr_result);
        tvSubUnsubResult = findViewById(R.id.tv_push_sub_opr_result);
        tvSubTopic = findViewById(R.id.tv_push_sub_topic);
        tvAutoInitOnOffResult = findViewById(R.id.tv_push_auto_in_opr_result);
    }

    /**
     * Initialize Listeners of UI Elements
     */

    private void initListener() {
        btnNext.setOnClickListener(v -> getNext());

        btnPrevious.setOnClickListener(v -> getPrevious());

        btnGetNotification.setOnClickListener(v -> openGetNotificationDialog());

        btnGetAAID.setOnClickListener(v -> setAAID(true));

        btnDeleteAAID.setOnClickListener(v -> setAAID(false));

        btnGetToken.setOnClickListener(v -> getToken());

        btnDeleteToken.setOnClickListener(v -> deleteToken());

        rbAAIDAutoApply.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                btnGetAAID.setEnabled(false);
                btnDeleteAAID.setEnabled(false);
            }
        });

        rbAAIDManApply.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                btnGetAAID.setEnabled(true);
                btnDeleteAAID.setEnabled(true);
            }
        });

        switchPushOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> setReceiveNotifyMsg(isChecked));

        btnSubTopic.setOnClickListener(v -> addTopic());

        btnUnSubTopic.setOnClickListener(v -> deleteTopic());

        switchAutoInitOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> setAutoInitEnabled(isChecked));

    }

    /**
     * ViewFlipper next screen action
     */
    private void getNext() {

        btnPrevious.setEnabled(true);
        currentIndex++;

        btnNext.setEnabled(currentIndex != 2);

        viewFlipper.showNext();
    }

    /**
     * ViewFlipper previous screen action
     */
    private void getPrevious() {

        btnNext.setEnabled(true);
        currentIndex--;

        btnPrevious.setEnabled(currentIndex != 0);

        viewFlipper.showPrevious();
    }

    private void openGetNotificationDialog() {
        if (getNotificationDialog != null && getNotificationDialog.isShowing()) {
            getNotificationDialog.dismiss();
            getNotificationDialog = null;
        }
        getNotificationDialog = new GetNotificationDialog(MainActivityPushKit.this);
        getNotificationDialog.show();
    }

    /**
     * getAAID(), This method is used to obtain an AAID in asynchronous mode. You need to add a listener to listen to the operation result.
     * deleteAAID(), delete a local AAID and its generation timestamp.
     *
     * @param isGet getAAID or deleteAAID
     */
    private void setAAID(boolean isGet) {
        if (isGet) {
            Task<AAIDResult> idResult = HmsInstanceId.getInstance(this).getAAID();
            idResult.addOnSuccessListener(aaidResult -> {
                String aaId = aaidResult.getId();
                Log.i(TAG, "getAAID success:" + aaId);

                tvAAIDResult.setText(aaId);
                tvAAIDOprResult.setText(getResources().getString(R.string.txt_opr_res_success_pushkit));
            }).addOnFailureListener(e -> {
                Log.e(TAG, "getAAID failed:" + e);
                tvAAIDOprResult.setText(getResources().getString(R.string.txt_opr_res_failed_pushkit));
            });
        } else {
            AIDThread = new Thread(() -> {
                try {
                    HmsInstanceId.getInstance(MainActivityPushKit.this).deleteAAID();
                    runOnUiThread(() -> {
                        tvAAIDResult.setText("");
                        tvAAIDOprResult.setText(getResources().getString(R.string.txt_opr_res_success_pushkit));
                    });
                } catch (Exception e) {
                    Log.e(TAG, "deleteAAID failed. " + e);
                    runOnUiThread(() -> tvAAIDOprResult.setText(getResources().getString(R.string.txt_opr_res_failed_pushkit)));
                }
            });
            AIDThread.start();
        }
    }

    /**
     * getToken(String appId, String scope), This method is used to obtain a token required for accessing HUAWEI Push Kit.
     * If there is no local AAID, this method will automatically generate an AAID when it is called because the Huawei Push server needs to generate a token based on the AAID.
     * This method is asynchronous method, and you cannot call it in the main thread. Otherwise, the main thread may be blocked.
     */
    private void getToken() {
        Async.submit(() -> {
            // read from agconnect-services.json
            AGConnectOptions agConnectOptionsBuilder = new AGConnectOptionsBuilder().build(getApplicationContext());
            String appId = agConnectOptionsBuilder.getString("client/app_id");
            String token = HmsInstanceId.getInstance(getApplicationContext()).getToken(appId, "HCM");
            Log.i(TAG, "get token:" + token);
            if (!TextUtils.isEmpty(token)) {
                sendRegTokenToServer(token);
            }
            tvTokenResult.setText(token);
            tvTokenOprResult.setText(getResources().getString(R.string.txt_opr_res_success_pushkit));

            return token;
        });

    }

    /*
        for multi sender scenario but can not tested need to sender
        for testing
   */
    private void getSubjectToken() {
        // Create a thread.
        subjectTokenThread = new Thread(() -> {
            try {
                // Apply for a token for the sender.
                AGConnectOptions agConnectOptionsBuilder = new AGConnectOptionsBuilder().build(getApplicationContext());
                String projectId = agConnectOptionsBuilder.getString("client/project_id");
                String token = HmsInstanceId.getInstance(MainActivityPushKit.this).getToken(projectId);
                Log.i(TAG, "get token:" + token);

                // Check whether the token is empty.
                if (!TextUtils.isEmpty(token)) {
                    sendRegTokenToServer(token);
                }
            } catch (ApiException e) {
                Log.e(TAG, "get token failed, " + e);
            }
        });
        subjectTokenThread.start();
    }

    /*
    using for multi sender scenario to delete the token with sender projectId
     */
    private void deleteToken(String projectID) {
        deleteTokenWithStringThread = new Thread(() -> {
            try {
                // read from agconnect-services.json
                HmsInstanceId.getInstance(getApplicationContext()).deleteToken(projectID);
                Log.i(TAG, "deleteToken multi sender success.");
                tvTokenResult.setText("");
                tvTokenOprResult.setText(getResources().getString(R.string.txt_opr_res_success_pushkit));
            } catch (ApiException e) {
                Log.e(TAG, "deleteToken multi sender failed." + e);
                tvTokenOprResult.setText(getResources().getString(R.string.txt_opr_res_failed_pushkit));
            }
        });
        deleteTokenWithStringThread.start();
    }


    /**
     * send token to server to store users' token information.
     * these tokens may be required to send notification from application server
     * dummy function
     *
     * @param token token from AGC service
     */
    private void sendRegTokenToServer(String token) {
        Log.i(TAG, "sending token to server. token:" + token);
    }

    /**
     * void deleteToken(String appId, String scope) throws ApiException
     * This method is used to obtain a token. After a token is deleted, the corresponding AAID will not be deleted.
     * This method is a synchronous method. Do not call it in the main thread. Otherwise, the main thread may be blocked.
     */
    private void deleteToken() {
        deleteTokenThread = new Thread(() -> {
            try {
                // read from agconnect-services.json
                AGConnectOptions agConnectOptionsBuilder = new AGConnectOptionsBuilder().build(getApplicationContext());
                String appId = agConnectOptionsBuilder.getString("client/app_id");
                HmsInstanceId.getInstance(getApplicationContext()).deleteToken(appId, "HCM");
                Log.i(TAG, "deleteToken success.");
                runOnUiThread(() -> {
                    tvTokenResult.setText("");
                    tvTokenOprResult.setText(getResources().getString(R.string.txt_opr_res_success_pushkit));
                });
            } catch (ApiException e) {
                Log.e(TAG, "deleteToken failed." + e);
                runOnUiThread(() -> {
                    tvTokenOprResult.setText(getResources().getString(R.string.txt_opr_res_failed_pushkit));
                });
            }
        });
        deleteTokenThread.start();
    }

    /**
     * Set up enable or disable the display of notification messages.
     *
     * @param enable enabled or not
     */
    private void setReceiveNotifyMsg(final boolean enable) {
        if (!enable) {
            HmsMessaging.getInstance(this).turnOnPush().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    tvPushOnOffResult.setText(getResources().getString(R.string.txt_push_on_pushkit));
                } else {
                    tvPushOnOffResult.setText(getResources().getString(R.string.txt_failed_pushkit));
                }
            });
        } else {
            HmsMessaging.getInstance(this).turnOffPush().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    tvPushOnOffResult.setText(getResources().getString(R.string.txt_push_off_pushkit));
                } else {
                    tvPushOnOffResult.setText(getResources().getString(R.string.txt_failed_pushkit));
                }
            });
        }
    }

    /**
     * to subscribe to topics in asynchronous mode.
     */
    private void addTopic() {
        createDialog(getResources().getString(R.string.txt_sub_name_pushkit), true);
    }

    private void createDialog(String text, boolean isAdd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_topic_pushkit, null);
        TextInputEditText edTopic;
        Button btnCancel;
        Button btnConfirm;
        btnConfirm = view.findViewById(R.id.btn_push_topic_confirm);
        btnCancel = view.findViewById(R.id.btn_push_topic_cancel);
        edTopic = view.findViewById(R.id.ed_push_topic);
        TextInputLayout edLayout = view.findViewById(R.id.et_Layout);
        edLayout.setHint(isAdd ? "Subscribe topic name" : "Unsubscribe topic name");
        //HmsMessaging messaging=HmsMessaging.getInstance(MainActivityPushKit.this);
        if (isFirst) {
            try {
                HmsMessaging.getInstance(MainActivityPushKit.this)
                        .subscribe("text")
                        .addOnCompleteListener(task -> {
                        });
            } catch (Exception e) {
                Log.i(TAG, "subscribe Failed" + e);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_topic_sub_exception), Toast.LENGTH_LONG).show();
            }
            isFirst = false;
        }
        btnConfirm.setOnClickListener(v -> {
            try {
                HmsMessaging.getInstance(MainActivityPushKit.this)
                        .subscribe(Objects.requireNonNull(edTopic.getText()).toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                tvSubUnsubResult.setText(getResources().getString(R.string.txt_success_pushkit));
                                tvSubTopic.setText(String.format(text, Objects.requireNonNull(edTopic.getText())));
                            } else {
                                tvSubUnsubResult.setText(getResources().getString(R.string.txt_failed_pushkit));
                            }
                        });
            } catch (Exception e) {
                Log.i(TAG, "subscribe Failed" + e);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_topic_sub_exception), Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    /**
     * to subscribe to topics in asynchronous mode.
     */
    private void deleteTopic() {
        createDialog(getResources().getString(R.string.txt_unsub_name_pushkit), false);
    }

    /**
     * to enable parameter indicates whether to enable automatically generating AAIDs and automatically applying for tokens
     *
     * @param enable enabled or not
     */
    private void setAutoInitEnabled(final boolean enable) {
        try {
            if (enable) {
                HmsMessaging.getInstance(this).setAutoInitEnabled(true);
                tvAutoInitOnOffResult.setText(getResources().getString(R.string.txt_enabled_pushkit));
            } else {
                HmsMessaging.getInstance(this).setAutoInitEnabled(false);
                tvAutoInitOnOffResult.setText(getResources().getString(R.string.txt_disabled_pushkit));
            }
        } catch (Exception e) {
            Log.e(TAG, "setAutoInitEnable Failed");
            tvAutoInitOnOffResult.setText(getResources().getString(R.string.txt_failed_pushkit));
        }
    }

    /**
     * to initialize receiver to fetch RemoteMessage from messaging service class
     */
    private void initReceiver() {
        BroadcastReceiver messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Object data = intent.getParcelableExtra("data");
                if (data != null) {

                    RemoteMessage message = null;

                    try {
                        message = (RemoteMessage) data;
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }

                    if (pushMessageDialog != null && pushMessageDialog.isShowing()) {
                        pushMessageDialog.dismiss();
                        pushMessageDialog = null;
                    }

                    pushMessageDialog = new PushMessageDialogPushKit(MainActivityPushKit.this, message);
                    pushMessageDialog.show();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(EXPLOREHMS_ACTION);
        registerReceiver(messageReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AIDThread != null) {
            AIDThread.interrupt();
        }
        if (subjectTokenThread != null) {
            subjectTokenThread.interrupt();
        }
        if (deleteTokenWithStringThread != null) {
            deleteTokenWithStringThread.interrupt();
        }
        if (deleteTokenThread != null) {
            deleteTokenThread.interrupt();
        }
    }
}