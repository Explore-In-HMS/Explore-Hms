package com.hms.explorehms.baseapp;

import android.content.Context;
import android.util.Log;
import com.hms.explorehms.baseapp.model.ObjectTypeInfoHelper;
import com.hms.explorehms.baseapp.model.UserInfo;
import com.huawei.agconnect.AGCRoutePolicy;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.AGConnectOptions;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.Task;
import java.util.ArrayList;

public class CloudDBZoneWrapper {

    private CloudDBZoneWrapper() {
        throw new IllegalStateException("Utility class");
    }

    private static final String TAG = "UserRequestLimit";

    private static AGConnectCloudDB cloudDB;
    private static AGConnectOptions agcConnectOptions;
    private static AGConnectInstance instance;
    private static CloudDBZone cloudDbZone;

    public static void initCloudDBZone(Context context) {
        agcConnectOptions = new AGConnectOptionsBuilder().setRoutePolicy(AGCRoutePolicy.CHINA).build(context);
        instance = AGConnectInstance.buildInstance(agcConnectOptions);
        cloudDB = AGConnectCloudDB.getInstance(instance,AGConnectAuth.getInstance());
        createObjectType();
        openCloudDBZone();
        Log.i(TAG,"initCloudDBZone");
    }

    public static void createObjectType() {
        try {
            cloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
            Log.i(TAG,"createObjectType");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public static CloudDBZone openCloudDBZone() {
        CloudDBZoneConfig cloudDBZoneConfig = new CloudDBZoneConfig("UserRequestLimit",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);

        cloudDBZoneConfig.setPersistenceEnabled(true);
        try {
            cloudDbZone = cloudDB.openCloudDBZone(cloudDBZoneConfig, true);
            Log.i(TAG,"openCloudDBZone");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return cloudDbZone;
    }

    public static void getAllUser() {
        try {
            Task<CloudDBZoneSnapshot<UserInfo>> cloudDBZoneQuery = cloudDbZone.executeQuery(
                    CloudDBZoneQuery.where(UserInfo.class),
                    CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);

            cloudDBZoneQuery.addOnSuccessListener(userInfoCloudDBZoneSnapshot -> {
                CloudDBZoneObjectList<UserInfo> userInfoCursor = userInfoCloudDBZoneSnapshot.getSnapshotObjects();

                try {
                    ArrayList<UserInfo> allUsers = new ArrayList<>();
                    while (userInfoCursor.hasNext()) {
                        UserInfo user = (UserInfo) userInfoCursor.next();
                        allUsers.add(user);
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, e.toString());
            });

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public static void upsertData(UserInfo user) {
        try {
            Task<Integer> upsertTask = cloudDbZone.executeUpsert(user);
            upsertTask.addOnSuccessListener(integer -> Log.i(TAG, "Upserted data")).addOnFailureListener(e -> Log.e(TAG, e.toString()));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public static void deleteData(UserInfo user) {

        try {
            Task<Integer> deleteTask = cloudDbZone.executeDelete(user);
            deleteTask.addOnSuccessListener(integer -> Log.i(TAG, "deleted item")).addOnFailureListener(e -> Log.e(TAG, e.toString()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void searchCommentByUserUid(IListCallback listCallback, String userUid, String kitName) {
        try {
            Task<CloudDBZoneSnapshot<UserInfo>> cloudQueryForBookName = cloudDbZone.executeQuery(
                    CloudDBZoneQuery.where(UserInfo.class).equalTo("userUid", userUid+kitName),
                    CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
            cloudQueryForBookName.addOnSuccessListener(userCommentCloudDBZoneSnapshot -> {
                CloudDBZoneObjectList<UserInfo> userInfoCursor = userCommentCloudDBZoneSnapshot.getSnapshotObjects();

                try {
                    ArrayList<UserInfo> allUsersComments = new ArrayList<>();
                    while (userInfoCursor.hasNext()) {
                        UserInfo user = (UserInfo) userInfoCursor.next();
                        allUsersComments.add(user);
                    }
                    listCallback.onResultCallBack(allUsersComments);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }).addOnFailureListener((OnFailureListener) e -> Log.e(TAG, e.toString()));


        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}
