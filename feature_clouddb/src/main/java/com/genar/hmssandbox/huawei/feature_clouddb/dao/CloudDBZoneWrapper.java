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
package com.genar.hmssandbox.huawei.feature_clouddb.dao;

import android.content.Context;
import android.util.Log;

import com.genar.hmssandbox.huawei.feature_clouddb.model.BookComment;
import com.genar.hmssandbox.huawei.feature_clouddb.model.ObjectTypeInfoHelper;
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

    private static final String TAG = "WRAPPER";

    private static AGConnectCloudDB cloudDB;
    private static CloudDBZone cloudDbZone;

    public static void initCloudDBZone(Context context) {
        AGConnectOptions agcConnectOptions = new AGConnectOptionsBuilder().setRoutePolicy(AGCRoutePolicy.GERMANY).build(context);
        AGConnectInstance instance = AGConnectInstance.buildInstance(agcConnectOptions);
        cloudDB = AGConnectCloudDB.getInstance(instance, AGConnectAuth.getInstance(instance));
        createObjectType();
        openCloudDBZone();
    }

    public static void createObjectType() {
        try {
            cloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public static void openCloudDBZone() {
        CloudDBZoneConfig cloudDBZoneConfig = new CloudDBZoneConfig("BookComments",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);

        cloudDBZoneConfig.setPersistenceEnabled(true);
        try {
            cloudDbZone = cloudDB.openCloudDBZone(cloudDBZoneConfig, true);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public static void getAllBookComment(final IListCallback<BookComment> iGetListRequest) {
        try {
            Task<CloudDBZoneSnapshot<BookComment>> cloudDBZoneQuery = cloudDbZone.executeQuery(
                    CloudDBZoneQuery.where(BookComment.class),
                    CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);

            cloudDBZoneQuery.addOnSuccessListener(bookCommentCloudDBZoneSnapshot -> {
                CloudDBZoneObjectList<BookComment> bookInfoCursor = bookCommentCloudDBZoneSnapshot.getSnapshotObjects();

                try {
                    ArrayList<BookComment> allBooksComments = new ArrayList<>();
                    while (bookInfoCursor.hasNext()) {
                        BookComment book1 = (BookComment) bookInfoCursor.next();
                        allBooksComments.add(book1);
                    }

                    iGetListRequest.onResultCallBack(allBooksComments);

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

    public static void upsertData(BookComment comment) {
        try {
            Task<Integer> upsertTask = cloudDbZone.executeUpsert(comment);
            upsertTask.addOnSuccessListener(integer -> Log.i(TAG, "Upserted data")).addOnFailureListener(e -> Log.e(TAG, e.toString()));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    public static void deleteData(BookComment comment) {

        try {
            Task<Integer> deleteTask = cloudDbZone.executeDelete(comment);
            deleteTask.addOnSuccessListener(integer -> Log.i(TAG, "deleted item")).addOnFailureListener(e -> Log.e(TAG, e.toString()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void searchCommentByBookName(IListCallback listCallback, String bookName) {
        try {
            Task<CloudDBZoneSnapshot<BookComment>> cloudQueryForBookName = cloudDbZone.executeQuery(
                    CloudDBZoneQuery.where(BookComment.class).contains("BookName", bookName),
                    CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
            cloudQueryForBookName.addOnSuccessListener(bookCommentCloudDBZoneSnapshot -> {
                CloudDBZoneObjectList<BookComment> bookInfoCursor = bookCommentCloudDBZoneSnapshot.getSnapshotObjects();

                try {
                    ArrayList<BookComment> allBooksComments = new ArrayList<>();
                    while (bookInfoCursor.hasNext()) {
                        BookComment book1 = (BookComment) bookInfoCursor.next();
                        allBooksComments.add(book1);
                    }
                    listCallback.onResultCallBack(allBooksComments);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }).addOnFailureListener((OnFailureListener) e -> Log.e(TAG, e.toString()));


        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}
