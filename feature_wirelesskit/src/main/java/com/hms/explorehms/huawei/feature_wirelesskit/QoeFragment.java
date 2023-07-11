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

package com.hms.explorehms.huawei.feature_wirelesskit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.os.RemoteException;
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
import com.huawei.hms.wireless.IQoeCallBack;
import com.huawei.hms.wireless.IQoeService;
import com.huawei.hms.wireless.NetworkQoeClient;
import com.huawei.hms.wireless.WirelessClient;
import com.huawei.hms.wireless.WirelessResult;

public class QoeFragment extends Fragment {

    public QoeFragment() {
        // Required empty public constructor
    }

    private static final String TAG = "networkQoe";
    private static final int NETWORK_QOE_INFO_TYPE = 0;

    private Button getQoeButton;
    private Button registerButton;
    private Button unRegisterButton;
    private Button showQoeDetailButton;
    private Button unbindQoeButton;
    private EditText getQoeStateText;
    private EditText registerStateText;
    private EditText unregisterStateText;
    private EditText showQoeDetailsText;
    private EditText callQoeDetails;
    private TextView explanationTextView;
    private TextView titleTextView2;
    private final int[] channelIndex = new int[4];
    private final int[] uLRtt = new int[4];
    private final int[] dLRtt = new int[4];
    private final int[] uLBandwidth = new int[4];
    private final int[] dLBandwidth = new int[4];
    private final int[] uLRate = new int[4];
    private final int[] dLRate = new int[4];
    private final int[] netQoeLevel = new int[4];
    private final int[] uLPkgLossRate = new int[4];
    private IQoeService qoeService = null;

    private final ServiceConnection srcConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            qoeService = IQoeService.Stub.asInterface(service);
            getQoeStateText.setText(getString(R.string.connected));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            qoeService = null;
            Log.i(TAG, "onServiceDisConnected.");
            getQoeStateText.setText(getString(R.string.disconnected));
            unregisterStateText.setText("");
            registerStateText.setText("");
        }
    };

    private final IQoeCallBack callBack = new IQoeCallBack.Stub() {
        @Override
        public void callBack(int type, Bundle qoeInfo) throws RemoteException {
            if (qoeInfo == null || type != NETWORK_QOE_INFO_TYPE) {
                Log.e(TAG, "callback failed.type:" + type);
                return;
            }

            StringBuilder channelQoe = buildChannelQoe(qoeInfo);

            Log.i(TAG, channelQoe.toString());
            callQoeDetails.setText(channelQoe.toString());
        }
    };

    private StringBuilder buildChannelQoe(Bundle qoeInfo){
        int channelNum = 0;
        if (qoeInfo.containsKey("channelNum")) {
            channelNum = qoeInfo.getInt("channelNum");
        }

        StringBuilder channelQoe = new StringBuilder(String.valueOf(channelNum));
        for (int i = 0; i < channelNum; i++) {
            uLRtt[i] = qoeInfo.getInt("uLRtt" + i);
            dLRtt[i] = qoeInfo.getInt("dLRtt" + i);
            uLBandwidth[i] = qoeInfo.getInt("uLBandwidth" + i);
            dLBandwidth[i] = qoeInfo.getInt("dLBandwidth" + i);
            uLRate[i] = qoeInfo.getInt("uLRate" + i);
            dLRate[i] = qoeInfo.getInt("dLRate" + i);
            netQoeLevel[i] = qoeInfo.getInt("netQoeLevel" + i);
            uLPkgLossRate[i] = qoeInfo.getInt("uLPkgLossRate" + i);
            channelIndex[i] = qoeInfo.getInt("channelIndex" + i);
            channelQoe.append(",").append(channelIndex[i]).append(",")
                    .append(uLRtt[i]).append(",").append(dLRtt[i]).append(",")
                    .append(uLBandwidth[i]).append(",").append(dLBandwidth[i])
                    .append(",").append(uLRate[i]).append(",").append(dLRate[i])
                    .append(",").append(netQoeLevel[i]).append(",").append(uLPkgLossRate[i]);
        }

        return channelQoe;
    }

    private void initWidget(View view) {
        getQoeButton = view.findViewById(R.id.ConnectQoe);
        registerButton = view.findViewById(R.id.registerQoe);
        unRegisterButton = view.findViewById(R.id.unRegisterQoe);
        showQoeDetailButton = view.findViewById(R.id.getQoeData);
        unbindQoeButton = view.findViewById(R.id.unbindQoeButton);
        getQoeStateText = view.findViewById(R.id.ConnectQoeState);
        registerStateText = view.findViewById(R.id.registerState);
        unregisterStateText = view.findViewById(R.id.unregisterState);
        showQoeDetailsText = view.findViewById(R.id.getQoeDataContext);
        callQoeDetails = view.findViewById(R.id.callQoeDetails);
        explanationTextView = view.findViewById(R.id.explanationTextView);
        titleTextView2 = view.findViewById(R.id.titleTextView2);
    }

    private void bindQoeService() {
        getQoeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                    getActivity().bindService(intent, srcConn, Context.BIND_AUTO_CREATE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception exception) {
                                    if (exception instanceof ApiException) {
                                        ApiException ex = (ApiException) exception;
                                        int errCode = ex.getStatusCode();
                                        Log.e(TAG, "Get intent failed:" + errCode);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void showRealTimeQoeInfo() {
        showQoeDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if (qoeService != null) {
                    try {
                        Bundle qoeInfo = qoeService.queryRealTimeQoe("com.hms.explorehms.huawei.feature_wirelesskit");
                        if (qoeInfo == null) {
                            Log.e(TAG, "queryRealTimeQoe is empty.");
                            callQoeDetails.setText(R.string.check_below);
                            return;
                        }

                        StringBuilder channelQoe = buildChannelQoe(qoeInfo);

                        Log.i(TAG, channelQoe.toString());
                        if(channelQoe.toString().equals("0")){
                            showQoeDetailsText.setText(R.string.unregistered);
                            registerStateText.setText("");
                            unregisterStateText.setText("");
                            callQoeDetails.setText("");
                        }
                        else{
                            showQoeDetailsText.setText(channelQoe.toString());
                        }
                    } catch (RemoteException exception) {
                        Log.e(TAG, "no unregisterNetQoeCallback api");
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view2 = inflater.inflate(R.layout.fragment_qoe, container, false);


        initWidget(view2);

        titleTextView2.setText(getString(R.string.network_qoe_long));

        // Bind QoeService
        bindQoeService();

        // Register network qoe callback
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                int ret = 0;
                if (qoeService != null) {
                    try {
                        ret = qoeService.registerNetQoeCallBack("com.hms.explorehms.huawei.feature_wirelesskit", callBack);

                        if(ret == 0){
                            //successfully registered
                            registerStateText.setText(R.string.success);
                            unregisterStateText.setText("");
                            showQoeDetailsText.setText("");
                            callQoeDetails.setText("");
                        }
                        else{
                            registerStateText.setText(R.string.failed);
                            unregisterStateText.setText("");
                            showQoeDetailsText.setText("");
                            callQoeDetails.setText("");
                        }

                    } catch (RemoteException ex) {
                        Log.e(TAG, "no registerNetQoeCallback api");
                    }
                }
            }
        });

        // Unregister network qoe callback
        unRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                int ret = 0;
                if (qoeService != null) {
                    try {
                        ret = qoeService.unRegisterNetQoeCallBack("com.hms.explorehms.huawei.feature_wirelesskit", callBack);
                        if(ret == 0){
                            //successfully unregistered
                            unregisterStateText.setText(R.string.success);
                            registerStateText.setText("");
                            showQoeDetailsText.setText("");
                            callQoeDetails.setText("");
                        }
                        else{
                            unregisterStateText.setText(R.string.failed);
                            registerStateText.setText("");
                            showQoeDetailsText.setText("");
                            callQoeDetails.setText("");
                        }
                    } catch (RemoteException ex) {
                        Log.e(TAG, "no unregisterNetQoeCallback api");
                    }
                }
            }
        });

        // Query real time qoe information
        showRealTimeQoeInfo();

        explanationTextView.setText(R.string.explanation_qoe);

        // Unbind QoeService
        unbindQoeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                if (qoeService != null) {
                    getActivity().unbindService(srcConn);
                    qoeService = null;
                    getQoeStateText.setText(getString(R.string.disconnected));
                    callQoeDetails.setText("");
                    showQoeDetailsText.setText("");
                    unregisterStateText.setText("");
                    registerStateText.setText("");
                }
            }
        });

        return view2;
    }
}