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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.RemoteException;
import android.util.Log;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.wireless.IDualWifiService;
import com.huawei.hms.wireless.INetworkCallback;
import com.huawei.hms.wireless.WirelessClient;
import com.huawei.hms.wireless.WirelessResult;
import com.huawei.hms.wireless.wifi.DualWifiClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DualWifiFragment extends Fragment {

    private static final String TAG = "DualWifiFragment";
    private static final String PACKAGE_NAME = "com.genar.hmssandbox.huawei.feature_wirelesskit";
    private static final String RSSI_CHANGED_ACTION = "huawei.net.slave_wifi.RSSI_CHANGED";
    private static final String WIFI_STATE_CHANGED_ACTION = "huawei.net.slave_wifi.WIFI_STATE_CHANGED";

    private IDualWifiService mDualWifiService = null;
    private Button mBindServiceButton;
    private Button mEnableDualWifiButton;
    private Button mDisableDualWifiButton;
    private Button mGetWifiConnectionInfoButton;
    private Button mGetLinkPropertiesButton;
    private Button mGetNetworkInfoButton;
    private Button mUnbindServiceButton;
    private BroadcastReceiver receiver = null;
    private TextView showDualWifiText;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDualWifiService = IDualWifiService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDualWifiService = null;
        }
    };

    public DualWifiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view2= inflater.inflate(R.layout.fragment_dual_wifi, container, false);

        initWidget(view2);
        if(!checkDeviceProcessor()){
            showDualWifiText.setText(R.string.check_below);
        }
        startListener();
        bindDualWifiService();

        mEnableDualWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDualWifiService != null) {
                    try {
                        showDualWifiText.setText("");
                        mDualWifiService.enableDualWifi(PACKAGE_NAME, callBack);
                        Log.e(TAG, "enable dual wifi");
                    } catch (RemoteException e) {
                        Log.e(TAG, "no unregisterDualWifiCallback api when enable dual wifi");
                    }
                }
            }
        });

        getWifiConnectionInfoButton();

        getLinkproperties();

        getNetworkInfo();

        disableDualWifi();

        unBindDualWifiService();

        return view2;
    }

    @Override
    public void onDestroy() {
        requireActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initWidget(View view) {
        mBindServiceButton = view.findViewById(R.id.bindService);
        mEnableDualWifiButton = view.findViewById(R.id.enableDualWifi);
        mDisableDualWifiButton = view.findViewById(R.id.disableDualWifi);
        mGetWifiConnectionInfoButton = view.findViewById(R.id.getWifiConnectionInfo);
        mGetLinkPropertiesButton = view.findViewById(R.id.getLinkproperties);
        mGetNetworkInfoButton = view.findViewById(R.id.getNetworkInfo);
        mUnbindServiceButton = view.findViewById(R.id.unBindService);
        showDualWifiText = view.findViewById(R.id.wifiinfo);
    }





    private void getWifiConnectionInfoButton() {
        mGetWifiConnectionInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDualWifiService != null) {
                    try {
                        String connectionInfo = mDualWifiService.getSlaveWifiConnectionInfo();
                        showDualWifiText.setText(connectionInfo);
                    } catch (RemoteException e) {
                        Log.e(TAG, "no unregisterDualWifiCallback api when get wifiConnectionInfo");
                    }
                }
            }
        });
    }

    private void getLinkproperties() {
        mGetLinkPropertiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDualWifiService != null) {
                    try {
                        LinkProperties mLinkProperties = mDualWifiService.getLinkPropertiesForSlaveWifi();
                        if (mLinkProperties != null) {
                            showDualWifiText.setText(mLinkProperties.toString());
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "no unregisterDualWifiCallback api when get getLinkProperties for slave wifi");
                    }
                }
            }
        });
    }

    private void getNetworkInfo() {
        mGetNetworkInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDualWifiService != null) {
                    try {
                        NetworkInfo mNetworkInfo = mDualWifiService.getNetworkInfoForSlaveWifi();
                        if (mNetworkInfo != null) {
                            showDualWifiText.setText(mNetworkInfo.toString());
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "no unregisterDualWifiCallback api when get getNetworkInfo for slave wifi");
                    }
                }
            }
        });
    }

    private void disableDualWifi() {
        mDisableDualWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDualWifiService != null) {
                    try {
                        mDualWifiService.disableDualWifi(PACKAGE_NAME, callBack);
                        showDualWifiText.setText("");
                    } catch (RemoteException e) {
                        Log.e(TAG, "no unregisterDualWifiCallback api when disable dual wifi");
                    }
                }
            }
        });
    }

    private void bindDualWifiService() {
        mBindServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DualWifiClient dualWifiClient = WirelessClient.getDualWifiClient(requireActivity());
                dualWifiClient.getDualWifiServiceIntent()
                        .addOnSuccessListener(new OnSuccessListener<WirelessResult>() {
                            @Override
                            public void onSuccess(WirelessResult wirelessResult) {
                                Intent intent = wirelessResult.getIntent();
                                if (intent == null) {
                                    Log.i(TAG, "onSuccess: intent is null");
                                }
                               /* boolean isBind = DualWifiActivity.this.bindService(intent, mServiceConnection,
                                        Context.BIND_AUTO_CREATE);*/
                               /* Log.d(TAG, "isBind: " + isBind);*/
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
        });
    }

    private void unBindDualWifiService() {
        mUnbindServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDualWifiService != null) {
                    requireActivity().unbindService(mServiceConnection);
                    mDualWifiService = null;
                }
            }
        });
    }

    private void startListener() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (RSSI_CHANGED_ACTION.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive: get RSSI_CHANGED_ACTION");
                } else if (WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                    Log.d(TAG, "onReceive: get WIFI_STATE_CHANGED_ACTION");
                }
            }
        };

        final IntentFilter filter = new IntentFilter();
        filter.addAction(RSSI_CHANGED_ACTION);
        filter.addAction(WIFI_STATE_CHANGED_ACTION);
        requireActivity().registerReceiver(receiver, filter);
    }

    private INetworkCallback callBack = new INetworkCallback.Stub() {
        @Override
        public void onAvailable(Network network) {
            if (network == null) {
                return;
            }
        }

        @Override
        public void onUnavailable() {
            Log.d(TAG, "onUnavailable: DualWifiActivity receive");
        }

        @Override
        public void onLost(Network network) {
            if (network == null) {
                return;
            }
            Log.d(TAG, "onLost: DualWifiActivity receive");
        }
    };

    public static Boolean checkDeviceProcessor(){

        boolean processorOK = false;
        String hardware = "Hardware";
        try(BufferedReader br = new BufferedReader (new FileReader("/proc/cpuinfo"))) {

            String str;

            Map<String, String> output = new HashMap<>();

            while ((str = br.readLine ()) != null) {

                String[] data = str.split (":");

                if (data.length > 1) {

                    String key = data[0].trim ().replace (" ", "_");
                    if (key.equals(hardware)){
                        output.put(key, data[1].trim ());
                        break;
                    }
                }
            }

            /**
             * Farklı şekillerde kullanılabilir
             */

            if(output.get(hardware) != null && !Objects.equals(output.get(hardware), "")){
                String processorHardware = output.get(hardware);

                processorOK = processorHardware != null && (
                        processorHardware.contains("Kirin970") ||
                                processorHardware.contains("Kirin990") );

            }
            br.close();

        }catch (IOException e) {
            Log.e("ProcessorInfo",e.toString());
            processorOK = false;
        }

        return processorOK;
    }


}