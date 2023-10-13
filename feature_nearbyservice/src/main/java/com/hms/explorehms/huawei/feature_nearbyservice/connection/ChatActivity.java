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

package com.hms.explorehms.huawei.feature_nearbyservice.connection;

import static com.huawei.hms.nearby.Nearby.setAgcRegion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.SimpleArrayMap;
import androidx.core.app.ActivityCompat;

import com.hms.explorehms.huawei.feature_nearbyservice.BuildConfig;
import com.hms.explorehms.huawei.feature_nearbyservice.R;
import com.hms.explorehms.huawei.feature_nearbyservice.connection.utils.FileUtil;
import com.hms.explorehms.huawei.feature_nearbyservice.connection.utils.ToastUtil;
import com.hms.explorehms.huawei.feature_nearbyservice.permission.PermissionHelper;
import com.hms.explorehms.huawei.feature_nearbyservice.permission.PermissionInterface;
import com.google.android.material.textfield.TextInputLayout;
import com.hms.explorehms.modelingkit3d.ui.activity.MainActivity;
import com.huawei.hms.framework.common.ContextCompat;
import com.huawei.hms.image.vision.bean.ResultCode;
import com.huawei.hms.nearby.Nearby;
import com.huawei.hms.nearby.StatusCode;
import com.huawei.hms.nearby.beacon.BeaconPicker;
import com.huawei.hms.nearby.beacon.GetBeaconOption;
import com.huawei.hms.nearby.beacon.TriggerOption;
import com.huawei.hms.nearby.common.RegionCode;
import com.huawei.hms.nearby.discovery.BroadcastOption;
import com.huawei.hms.nearby.discovery.ConnectCallback;
import com.huawei.hms.nearby.discovery.ConnectInfo;
import com.huawei.hms.nearby.discovery.ConnectResult;
import com.huawei.hms.nearby.discovery.DiscoveryEngine;
import com.huawei.hms.nearby.discovery.Policy;
import com.huawei.hms.nearby.discovery.ScanEndpointCallback;
import com.huawei.hms.nearby.discovery.ScanEndpointInfo;
import com.huawei.hms.nearby.discovery.ScanOption;
import com.huawei.hms.nearby.transfer.Data;
import com.huawei.hms.nearby.transfer.DataCallback;
import com.huawei.hms.nearby.transfer.TransferEngine;
import com.huawei.hms.nearby.transfer.TransferStateUpdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ChatActivity extends AppCompatActivity implements PermissionInterface, View.OnClickListener{

    private static final int TIMEOUT_MILLISECONDS = 10000;
    private static final int REQUEST_OPEN_DOCUMENT = 20;
    private static final String TAG = "NearbyConnectionDemo";
    private static ChatActivity mService = null;
    private final SimpleArrayMap<Long, Data> incomingFilePayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, Data> completedFilePayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, String> filePayloadFilenames = new SimpleArrayMap<>();
    private List<Long> sendPayloadIds = new ArrayList<>();
    private List<Long> receivePayloadIds = new ArrayList<>();

    private static final String MANUALLY_INPUT = ":manually input";

    private TransferEngine mTransferEngine = null;
    private DiscoveryEngine mDiscoveryEngine = null;

    private int mDiscoveryEngine2;

    private PermissionHelper mPermissionHelper;

    private EditText myNameEt;
    private EditText friendNameEt;
    private EditText msgEt;

    private ListView messageListView;

    private List<MessageBean> msgList;

    private ChatAdapter adapter;

    private RadioButton rb1, rb2, rb3, rb4;

    private Button connectBtn;

    private ImageButton menuBtn;

    private int connectTaskResult;

    private String myNameStr;
    private String friendNameStr;
    private String myServiceId;
    private String mEndpointId;
    private String msgStr;

    private ChatActivity.ReceivedFileListener receivedFileListener;

    /**
     * get MainActivity.class
     *
     * @return MainActivity
     */
    public static ChatActivity getService() {
        if (mService == null) {
            syncInit();
        }
        return mService;
    }

    private static synchronized void syncInit() {
        if (mService == null) {
            mService = new ChatActivity();
        }
    }

    /**
     * public constructor
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        permissionrequest(this);
        initView();
        setupToolbar();
        msgEt.setEnabled(false);
        menuBtn.setEnabled(false);
    }

    private void registerScanTask(){

        TriggerOption triggerOption = new TriggerOption.Builder()
                // Set the notification mode (TriggerMode).
                .setTriggerMode(1)
                // Set the class name to Broadcast or Service, which should be consistent with TriggerMode.
                .setTriggerClassName(SampleService.class.getName())
                .build();
        Intent intent = new Intent();
        intent.putExtra(GetBeaconOption.KEY_TRIGGER_OPTION, triggerOption);

        BeaconPicker beaconPicker = new BeaconPicker.Builder()
                // Filter beacons based on NamespaceType.
                .includeNamespaceType("dev91050203040506", "HMS")
                // Filter beacons based on the beacon ID prefix and NamespaceType.
                .includeNamespaceType("dev91050203040506", "HMS", "6bff00f723fdf7471402", BeaconPicker.BEACON_TYPE_IBEACON)
                // Filter beacons based on beacon ID prefix.
                .includeBeaconId("6bff00f723fdf7471402", BeaconPicker.BEACON_TYPE_IBEACON)
                .build();
        GetBeaconOption getOption = new GetBeaconOption.Builder().picker(beaconPicker).build();
        Nearby.getBeaconEngine(this).registerScanTask(intent, getOption)
                .addOnSuccessListener(unused -> Log.i(TAG, "registerScanTask success"))
                .addOnFailureListener(e -> Log.i(TAG, "registerScanTask: " + e.getMessage()));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_nearbyservice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Called when the user presses the "back" button in the toolbar.
     * It handles the behavior for navigation.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void initView() {
        myNameEt = findViewById(R.id.et_my_name);
        friendNameEt = findViewById(R.id.et_friend_name);
        msgEt = findViewById(R.id.et_msg);
        connectBtn = findViewById(R.id.btn_connect);
        rb1 = findViewById(R.id.rb1_nearby);
        rb2 = findViewById(R.id.rb2_nearby);
        rb3 = findViewById(R.id.rb3_nearby);
        rb4 = findViewById(R.id.rb4_nearby);


        menuBtn = findViewById(R.id.btnPopupMenu);

        TextInputLayout layTextInput = findViewById(R.id.laySendPictRece);

        layTextInput.setEndIconOnClickListener(view -> {
            if (checkMessage()) {
                return;
            }
            sendMessage();
        });

        connectBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
        messageListView = findViewById(R.id.lv_chat);
        msgList = new ArrayList<>();
        adapter = new ChatAdapter(this, msgList);
        messageListView.setAdapter(adapter);
        connectTaskResult = StatusCode.STATUS_ENDPOINT_UNKNOWN;
    }

    /**
     * Handle timeout function
     */
    @SuppressLint("HandlerLeak")
    private Handler handler =
            new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    handler.removeMessages(0);
                    if (connectTaskResult != StatusCode.STATUS_SUCCESS) {
                        ToastUtil.showShortToastTop(getApplicationContext(),
                                "Connection timeout, make sure your friend is ready and try again.");
                        if (myNameStr.compareTo(friendNameStr) > 0) {
                            mDiscoveryEngine.stopScan();
                        } else {
                            mDiscoveryEngine.stopBroadcasting();
                        }
                        myNameEt.setEnabled(true);
                        friendNameEt.setEnabled(true);
                        connectBtn.setEnabled(true);
                    }
                }
            };


    @Override
    public int getPermissionsRequestCode() {
        return 10086;
    }

    /**
     * Permission for this app
     */
    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
        Log.d(TAG, "request permissions success.");
    }

    @Override
    public void requestPermissionsFail() {
        Toast.makeText(this, StatusCode.STATUS_SELECT_CLOUD_POLICY_IS_AUTO, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect: {
                connectButtonActions(view);
                registerScanTask();
                break;
            }
            case R.id.btnPopupMenu: {
                popMenuClickActions();
                break;
            }
            default: {
                break;
            }
        }
    }

    private void connectButtonActions(View view) {
        if (checkName()) {
            return;
        }
        connect(view);
        handler.sendEmptyMessageDelayed(0, TIMEOUT_MILLISECONDS);
    }

    private void popMenuClickActions() {
        PopupMenu popup = new PopupMenu(ChatActivity.this, menuBtn);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.chat_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.sendPictureMenu: {
                    onSendFileButtonClicked();
                    break;
                }
                case R.id.receivedFilesMenu: {
                    Intent intent = new Intent(getApplicationContext(), ReceivedPhotoActivity.class);
                    startActivity(intent);
                    break;
                }
                default: //default state
                    break;
            }
            return true;
        });
        popup.show();//showing popup menu

    }

    /**
     * Check input message
     */
    private boolean checkMessage() {
        if (TextUtils.isEmpty(msgEt.getText())) {
            ToastUtil.showShortToastTop(getApplicationContext(), "Please input data you want to send.");
            return true;
        }
        return false;
    }

    /**
     * Check input name
     */
    private boolean checkName() {
        if (TextUtils.isEmpty(myNameEt.getText())) {
            ToastUtil.showShortToastTop(getApplicationContext(), "Please input your name.");
            return true;
        }
        if (TextUtils.isEmpty(friendNameEt.getText())) {
            ToastUtil.showShortToastTop(getApplicationContext(), "Please input your friend's name.");
            return true;
        }
        if (TextUtils.equals(myNameEt.getText().toString(), friendNameEt.getText().toString())) {
            ToastUtil.showShortToastTop(getApplicationContext(), "Please input two different names.");
            return true;
        }
        friendNameStr = friendNameEt.getText().toString();
        myNameStr = myNameEt.getText().toString();
        getServiceId();
        return false;
    }

    /**
     * Send message function
     */
    private void sendMessage() {
        msgStr = msgEt.getText().toString() + MANUALLY_INPUT;
        Data data = Data.fromBytes(msgStr.getBytes(Charset.defaultCharset()));
        Log.d(TAG, "myEndpointId " + mEndpointId);
        mTransferEngine.sendData(mEndpointId, data).addOnCompleteListener(task -> {
            task.addOnSuccessListener(su -> Log.i(TAG, "sendData [Message] success. Message:" + msgStr))
                    .addOnFailureListener(e -> Log.e(TAG, "sendData [Message] failed, Message:" + msgStr + e));
        });
        MessageBean item = new MessageBean();
        item.setMyName(myNameStr);
        item.setFriendName(friendNameStr);
        item.setMsg(msgStr.split(":")[0]);
        item.setType(MessageBean.TYPE_SEND_TEXT);
        msgList.add(item);
        adapter.notifyDataSetChanged();
        msgEt.setText("");
        messageListView.setSelection(messageListView.getBottom());
    }

    /**
     * Receive message function
     */
    private void receiveMessage(Data data) {
        msgStr = new String(data.asBytes(), UTF_8);
        Log.d(TAG, "onReceived [Message] success. msgStr-------->>>>" + msgStr);
        if (!msgStr.endsWith(MANUALLY_INPUT)) {
            return;
        }
        msgStr = msgStr.split(":")[0];
        MessageBean item = new MessageBean();
        item.setMyName(myNameStr);
        item.setFriendName(friendNameStr);
        item.setMsg(msgStr);
        item.setType(MessageBean.TYPE_RECEIVE_TEXT);
        msgList.add(item);
        adapter.notifyDataSetChanged();
        messageListView.setSelection(messageListView.getBottom());
    }

    private void connect(View view) {
        ToastUtil.showShortToastTop(getApplicationContext(), "Connecting to your friend.");
        connectBtn.setEnabled(false);
        myNameEt.setEnabled(false);
        friendNameEt.setEnabled(false);
        Context context = getApplicationContext();
        mDiscoveryEngine = Nearby.getDiscoveryEngine(context);
        setAgcRegion(context, initRegionCode());
        if (myNameStr.compareTo(friendNameStr) > 0) {
            doStartScan(view);
        } else {
            doStartBroadcast(view);
        }
    }

    public RegionCode initRegionCode(){
        if (rb1.isChecked()) {
            return RegionCode.CN;
        } else if (rb2.isChecked()) {
            return RegionCode.RU;
        } else if (rb3.isChecked()) {
            return RegionCode.DE;
        } else if (rb4.isChecked()) {
            return RegionCode.SG;
        } else {
            ToastUtil.showShortToastTop(getApplicationContext(), String.valueOf(StatusCode.STATUS_NOT_SET_CLOUD_POLICY));
        }
        return RegionCode.DE;
    }

    /**
     * Broadcast function.
     *
     * @param view Android view
     */
    public void doStartBroadcast(View view) {
        BroadcastOption.Builder advBuilder = new BroadcastOption.Builder();
        advBuilder.setPolicy(Policy.POLICY_STAR);
        mDiscoveryEngine.startBroadcasting(myNameStr, myServiceId, mConnCb, advBuilder.build());
    }

    private void getServiceId() {
        if (myNameStr.compareTo(friendNameStr) > 0) {
            myServiceId = myNameStr + friendNameStr;
        } else {
            myServiceId = friendNameStr + myNameStr;
        }
    }

    /**
     * Scan function.
     *
     * @param view Android view
     */
    public void doStartScan(View view) {
        ScanOption.Builder discBuilder = new ScanOption.Builder();
        discBuilder.setPolicy(Policy.POLICY_STAR);
        mDiscoveryEngine.startScan(myServiceId, mDiscCb, discBuilder.build());
    }

    private ConnectCallback mConnCb =
            new ConnectCallback() {
                @Override
                public void onEstablish(String endpointId, ConnectInfo connectionInfo) {
                    mTransferEngine = Nearby.getTransferEngine(getApplicationContext());
                    mDiscoveryEngine.acceptConnect(endpointId, mDataCb);
                    ToastUtil.showShortToastTop(getApplicationContext(), "Let's chat!");
                    msgEt.setEnabled(true);
                    menuBtn.setEnabled(true);
                    connectBtn.setEnabled(false);
                    connectTaskResult = StatusCode.STATUS_SUCCESS;
                    if (myNameStr.compareTo(friendNameStr) > 0) {
                        mDiscoveryEngine.stopScan();
                    } else {
                        mDiscoveryEngine.stopBroadcasting();
                    }
                }

                @Override
                public void onResult(String endpointId, ConnectResult resolution) {
                    mEndpointId = endpointId;
                }

                @Override
                public void onDisconnected(String endpointId) {
                    ToastUtil.showShortToastTop(getApplicationContext(), "Disconnect.");
                    connectTaskResult = StatusCode.STATUS_NOT_CONNECTED;
                    connectBtn.setEnabled(true);
                    msgEt.setEnabled(false);
                    myNameEt.setEnabled(true);
                    friendNameEt.setEnabled(true);
                    menuBtn.setEnabled(false);

                }
            };

    private ScanEndpointCallback mDiscCb =
            new ScanEndpointCallback() {
                @Override
                public void onFound(String endpointId, ScanEndpointInfo discoveryEndpointInfo) {
                    mEndpointId = endpointId;
                    mDiscoveryEngine.requestConnect(myNameStr, mEndpointId, mConnCb);
                }

                @Override
                public void onLost(String endpointId) {
                    Log.d(TAG, "Nearby Connection Demo app: Lost endpoint: " + endpointId);
                }
            };

    private DataCallback mDataCb =
            new DataCallback() {
                @Override
                public void onReceived(String string, Data data) {
                    Log.d(TAG, "onReceived, Data.Type = " + data.getType());
                    Log.d(TAG, "onReceived, string ======== " + string);
                    switch (data.getType()) {
                        case Data.Type.BYTES:
                            String str = new String(data.asBytes(), UTF_8);
                            if (str.endsWith(MANUALLY_INPUT)) {
                                receiveMessage(data);
                            } else {
                                Log.i(TAG, "onReceived [Filename] success, Data.Type.BYTES, payloadFilename ===" + str);
                                addPayloadFilename(str);
                            }
                            break;
                        case Data.Type.FILE:
                            incomingFilePayloads.put(data.getId(), data);
                            completedFilePayloads.put(data.getId(), data);
                            processFilePayload(data.getId());
                            Log.i(TAG, "onReceived [FilePayload] success, Data.Type.FILE, payloadId ===" + data.getId());
                            break;
                        default:
                            Log.i(TAG, "the other Unknown data type, please check the uploaded file.");
                    }
                }

                @Override
                public void onTransferUpdate(String string, TransferStateUpdate update) {
                    long transferredBytes = update.getBytesTransferred();
                    long totalBytes = update.getTotalBytes();
                    long payloadId = update.getDataId();
                    Log.d(TAG, "onTransferUpdate, payloadId============" + payloadId);
                    switch (update.getStatus()) {
                        case TransferStateUpdate.Status.TRANSFER_STATE_SUCCESS:
                            filePayloadFilenames.remove(payloadId);
                            updateProgress(transferredBytes, totalBytes, 100, payloadId, false);
                            Log.d(TAG, "onTransferUpdate.Status============success.");
                            Data payload = incomingFilePayloads.remove(payloadId);

                            if (payload != null) {
                                if (payload.getType() == Data.Type.FILE) {
                                    sendPayloadIds.remove(payloadId);
                                    receivePayloadIds.remove(payloadId);
                                    ToastUtil.showLongToast(getApplicationContext(),
                                            "Your friend shares a file with you. Tap [RECE] to find it.");
                                }
                                Log.d(TAG, "onTransferUpdate, payload.Type " + payload.getType());
                                completedFilePayloads.put(payloadId, payload);
                            }
                            break;
                        case TransferStateUpdate.Status.TRANSFER_STATE_IN_PROGRESS:
                            Log.d(TAG, "onTransferUpdate.Status==========transfer in progress.");
                            if (!sendPayloadIds.contains((payloadId)) && !receivePayloadIds.contains(payloadId)) {
                                if (TextUtils.isEmpty(filePayloadFilenames.get(payloadId))) {
                                    return;
                                }
                                receivePayloadIds.add(payloadId);
                                if (!FileUtil.isImage(filePayloadFilenames.get(payloadId))) {
                                    updateListViewItem(update.getDataId(),
                                            null, filePayloadFilenames.get(payloadId), totalBytes);
                                }
                            }
                            int progress = (int) (transferredBytes * 100 / totalBytes);
                            updateProgress(transferredBytes, totalBytes, progress, payloadId, true);
                            break;
                        default:
                            Log.d(TAG, "onTransferUpdate.Status=======" + update.getStatus());

                    }
                }
            };

    private void updateProgress(long transferredBytes, long totalBytes,
                                int progress, long payloadId, boolean isSending) {
        for (MessageBean item : msgList) {
            if (item.getPayloadId() == payloadId) {
                item.setTransferredBytes(transferredBytes);
                item.setTotalBytes(totalBytes);
                item.setSending(isSending);
                item.setProgress(progress);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * open picture gallery, and select a photo.
     */
    public void onSendFileButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        this.startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);
    }

    /**
     * select a photo and begin to send it to peed device.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQUEST_OPEN_DOCUMENT && resultCode == Activity.RESULT_OK && resultData != null) {
            Uri uri = resultData.getData();
            Data filePayload;
            try {
                ParcelFileDescriptor pfd = getApplicationContext().getContentResolver().openFileDescriptor(uri, "r");
                filePayload = Data.fromFile(pfd);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File not found, cause: ", e);
                return;
            }
            sendFilePayload(filePayload, mEndpointId, uri);
        }
    }

    /**
     * begin to send photo.
     *
     * @param filePayload photo we've selected.
     * @param endpointID  peer device that we are sending our photo to.
     * @param uri         image uri
     */
    private void sendFilePayload(Data filePayload, String endpointID, Uri uri) {
        String fileName = FileUtil.getFileRealNameFromUri(this, uri);
        String filenameMessage = filePayload.getId() + "@" + fileName;
        Data filenameBytesPayload = Data.fromBytes(filenameMessage.getBytes(StandardCharsets.UTF_8));
        mTransferEngine.sendData(endpointID, filenameBytesPayload).addOnCompleteListener(task -> {
            task.addOnSuccessListener(su -> {
                Log.i(TAG, "sendData [Filename] success. filename:" + filenameMessage);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "sendData [Filename] failed, filename:" + filenameMessage + e);
            });
        });
        mTransferEngine.sendData(endpointID, filePayload).addOnCompleteListener(task -> {
            task.addOnSuccessListener(su -> {
                Log.i(TAG, "sendData [FilePayload] success. payloadId:" + filePayload.getId());
            }).addOnFailureListener(e -> {
                Log.e(TAG, "sendData [FilePayload] failed,  payloadId:" + filePayload.getId() + e);
            });
        });
        sendPayloadIds.add(filePayload.getId());
        updateListViewItem(filePayload.getId(), uri, fileName, filePayload.asFile().getSize());
    }

    private void updateListViewItem(long payloadId, Uri uri, String fileName, long totalBytes) {
        if (TextUtils.isEmpty(fileName)) {
            Log.e(TAG, "filename is null.");
            return;
        }
        MessageBean item = new MessageBean();
        item.setMyName(myNameStr);
        item.setFriendName(friendNameStr);

        if (sendPayloadIds.contains(payloadId)) {
            //send
            if (FileUtil.isImage(fileName)) {
                item.setType(MessageBean.TYPE_SEND_IMAGE);
            } else {
                item.setType(MessageBean.TYPE_SEND_FILE);
            }
        } else {
            //receive
            if (FileUtil.isImage(fileName)) {
                item.setType(MessageBean.TYPE_RECEIVE_IMAGE);
            } else {
                item.setType(MessageBean.TYPE_RECEIVE_FILE);
            }
        }

        item.setSending(true);
        item.setFileUri(uri);
        item.setFileName(fileName);
        item.setTotalBytes(totalBytes);
        item.setPayloadId(payloadId);
        Log.d(TAG, "updateListViewItem, payloadId============" + payloadId);
        msgList.add(item);
        adapter.notifyDataSetChanged();
        messageListView.setSelection(messageListView.getBottom());
    }

    private void processFilePayload(long payloadId) {
        Log.d(TAG, "processFilePayload, payloadId=========" + payloadId);
        Data filePayload = completedFilePayloads.get(payloadId);
        String filename = filePayloadFilenames.get(payloadId);
        Log.d(TAG, "received file: " + filename);
        if (filePayload != null && filename != null) {
            completedFilePayloads.remove(payloadId);
            File payloadFile = filePayload.asFile().asJavaFile();
            Log.d(TAG, "processFilePayload, payloadFile name------>>>>>>: " + payloadFile.getName());
            // Rename the file.
            File targetFileName = new File(payloadFile.getParentFile(), filename);

            boolean result = payloadFile.renameTo(targetFileName);
            if (result) {
                if (FileUtil.isImage(filename)) {
                    updateListViewItem(payloadId, Uri.fromFile(targetFileName), filename, targetFileName.length());
                }
            } else {
                Log.e(TAG, "rename the file failed.");
            }
            // inform UI
            if (receivedFileListener != null) {
                receivedFileListener.receivedFile(targetFileName);
            }
        }
    }

    private long addPayloadFilename(String payloadFilenameMessage) {
        Log.d(TAG, "addPayloadFilename, payloadFilenameMessage======== " + payloadFilenameMessage);
        String[] parts = payloadFilenameMessage.split("@");
        long payloadId = Long.parseLong(parts[0]);
        String filename = parts[1];
        filePayloadFilenames.put(payloadId, filename);
        return payloadId;
    }

    public void setReceivedFileListener(ChatActivity.ReceivedFileListener receivedFileListener) {
        this.receivedFileListener = receivedFileListener;
    }

    /**
     * ReceivedFileListener interface.
     */

    public interface ReceivedFileListener {
        /**
         * Receive file function.
         *
         * @param file File we received.
         */
        void receivedFile(File file);
    }

    private void gotoSettingsForLocation() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Permission necessary")
                .setMessage("Location permission is necessary")
                .setIcon(com.hms.explorehms.R.drawable.icon_settings_loc)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ChatActivity.this, "Moving to Settings", Toast.LENGTH_SHORT).show();
                        try {
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(ChatActivity.this, "failed to open Settings\n" + e, Toast.LENGTH_LONG).show();
                            Log.d("error", e.toString());
                        }

                    }
                }).create().show();
    }
    private void gotoSettingsForStorage() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Permission necessary")
                .setMessage("External storage permission is necessary")
                .setIcon(com.hms.explorehms.R.drawable.icon_settings_loc)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ChatActivity.this, "Moving to Settings", Toast.LENGTH_SHORT).show();
                        try {
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(ChatActivity.this, "failed to open Settings\n" + e, Toast.LENGTH_LONG).show();
                            Log.d("error", e.toString());
                        }

                    }
                }).create().show();
    }
    private void gotoSettings() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Permission necessary")
                .setMessage("External storage and Location permission is necessary")
                .setIcon(com.hms.explorehms.R.drawable.icon_settings_loc)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ChatActivity.this, "Moving to Settings", Toast.LENGTH_SHORT).show();
                        try {
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(ChatActivity.this, "failed to open Settings\n" + e, Toast.LENGTH_LONG).show();
                            Log.d("error", e.toString());
                        }

                    }
                }).create().show();
    }

    public void permissionrequest(Context context) {
        ActivityResultLauncher<String[]> permissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            Boolean writeExternalStorage = result.getOrDefault(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, false);
                            Boolean readExternalStorage = result.getOrDefault(
                            Manifest.permission.READ_EXTERNAL_STORAGE,false);
                            if (writeExternalStorage != null && !writeExternalStorage && readExternalStorage != null && !readExternalStorage && fineLocationGranted != null && !fineLocationGranted && coarseLocationGranted != null && !coarseLocationGranted) {
                               gotoSettings();
                            }
                            else if(writeExternalStorage != null && !writeExternalStorage && readExternalStorage != null && !readExternalStorage){
                                gotoSettingsForStorage();
                            }
                            else if(fineLocationGranted != null && !fineLocationGranted && coarseLocationGranted != null && !coarseLocationGranted){
                                gotoSettingsForLocation();
                            }else{
                                showToast("Permission granted! You can use Nearby Connection.");
                            }
                        }
                );
        permissionRequest.launch(new String[] {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        });
}
    private void showToast(String toastText) {
        Toast.makeText(ChatActivity.this, toastText, Toast.LENGTH_SHORT).show();
    }


    }

