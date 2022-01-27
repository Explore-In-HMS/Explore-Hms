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

package com.genar.hmssandbox.huawei.feature_wirelesskit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.wireless.IQoeService;
import com.huawei.hms.wireless.NetworkQoeClient;
import com.huawei.hms.wireless.WirelessClient;
import com.huawei.hms.wireless.WirelessResult;

import java.text.SimpleDateFormat;

public class AppQualityFragment extends Fragment {

    private static final String TAG = "appQuality";
    private static final String PACKAGE_NAME = "pkgName";
    private static final String EVENT_ID = "eventId";
    private static final String NETWORK_REASON = "netReason";
    private static final String APP_VERSION = "version";
    private static final String DIRECTION = "direction";
    private static final String HAPPEN_TIME = "errorTime";

    private EditText eventIdDetail;
    private EditText netReasonDetail;
    private EditText directionDetail;
    private EditText sendResult;
    private Context context;
    private IQoeService qoeService = null;

    public AppQualityFragment() {
        // Required empty public constructor
    }

    private final ServiceConnection srcConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            int ret = 0;
            qoeService = IQoeService.Stub.asInterface(service);
            Log.i(TAG, "bind success.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            qoeService = null;
            Log.i(TAG, "bind failed.");
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void reportAppNetworkQuality() {
        Bundle data = new Bundle();
        String inputStr = eventIdDetail.getText().toString();
        if (!TextUtils.isEmpty(inputStr)) {
            int eventId = Integer.parseInt(inputStr);
            data.putInt(EVENT_ID, eventId);
        }

        inputStr = netReasonDetail.getText().toString();
        if (!TextUtils.isEmpty(inputStr)) {
            data.putString(NETWORK_REASON, inputStr);
        }

        inputStr = directionDetail.getText().toString();
        if (!TextUtils.isEmpty(inputStr)) {
            int direction = Integer.parseInt(inputStr);
            data.putInt(DIRECTION, direction);
        }

        data.putString(PACKAGE_NAME, getActivity().getPackageName());
        data.putString(APP_VERSION, Integer.toString(packageCode(context)));

        long currentTime = System.currentTimeMillis();
        String timeNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
        data.putString(HAPPEN_TIME, timeNow);
        Log.i(TAG, "begin send msg :" + data);
        if (qoeService != null) {
            try {
                int ret = qoeService.reportAppQuality(data);
                Log.i(TAG, "send msg end:" + data + ",ret = " + ret);
                sendResult.setText(Integer.toString(ret));
            } catch (RemoteException ex) {
                Log.i(TAG, "no api");
            }
        } else {
            Log.i(TAG, "qoeService is null");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = (int) info.getLongVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return code;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view4 = inflater.inflate(R.layout.fragment_app_quality, container, false);

        context = getActivity().getApplicationContext();
        eventIdDetail = view4.findViewById(R.id.eventIdDetail);
        netReasonDetail = view4.findViewById(R.id.netReasonDetail);
        directionDetail = view4.findViewById(R.id.directionDetail);
        Button sendButton = view4.findViewById(R.id.button);
        sendResult = view4.findViewById(R.id.sendResult);
        TextView explanationAppQualityTextView = view4.findViewById(R.id.explanationAppQualityTextView);
        TextView titleTextView4 = view4.findViewById(R.id.titleTextView4);
        titleTextView4.setText(getString(R.string.report_app_quality));
        explanationAppQualityTextView.setText(R.string.explanation_app_quality);

        // Bind QoeService
        NetworkQoeClient networkQoeClient = WirelessClient.getNetworkQoeClient(getActivity());
        if (networkQoeClient != null) {
            networkQoeClient.getNetworkQoeServiceIntent()
                    .addOnSuccessListener(new OnSuccessListener<WirelessResult>() {
                        @Override
                        public void onSuccess(WirelessResult wirelessResult) {
                            Intent intent = wirelessResult.getIntent();
                            if (intent == null) {
                                Log.i(TAG, "intent is null.");
                                return;
                            }
                            Log.i(TAG, "intent:" + intent);
                            getActivity().bindService(intent, srcConn, Context.BIND_AUTO_CREATE);
                            Log.i(TAG, "bind service.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            if (e instanceof ApiException) {
                                ApiException ex = (ApiException) e;
                                int errCode = ex.getStatusCode();
                                Log.e(TAG, "Get intent failed:" + errCode);
                            }
                        }
                    });
        }

        // Click sendButton to report app quality
        sendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick");
                reportAppNetworkQuality();
            }
        });

        return view4;
    }
}