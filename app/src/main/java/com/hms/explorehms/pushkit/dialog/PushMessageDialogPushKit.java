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

package com.hms.explorehms.pushkit.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hms.explorehms.R;
import com.hms.explorehms.pushkit.adapter.NotificationDataAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huawei.hms.push.RemoteMessage;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PushMessageDialogPushKit extends Dialog {


    private final Context context;
    private View dialogView;

    private ListView lvMessageData;

    private Button btnExit;

    private TextView collapse;
    private TextView from;
    private TextView to;
    private TextView messageId;
    private TextView originalUrgency;
    private TextView urgency;
    private TextView sendTime;
    private TextView messageType;
    private TextView ttl;

    private TextView imageUrl;
    private TextView title;
    private TextView titleLocKey;
    private TextView titleLocArgs;
    private TextView body;
    private TextView bodyLocKey;
    private TextView bodyLocArgs;
    private TextView icon;
    private TextView sound;
    private TextView tag;
    private TextView color;
    private TextView clickAction;
    private TextView channelId;
    private TextView link;
    private TextView notifyId;


    /**
     * Remote message object that received from messaging service
     */
    private final RemoteMessage message;

    /**
     * @param context context
     * @param message remote message object from messaging service
     */
    public PushMessageDialogPushKit(Context context, RemoteMessage message){
        super(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        this.context = context;
        this.message = message;

        initUI();
        initListeners();
        initMessageInfo();

        setContentView(dialogView);
    }

    /**
     * Initialize UI Elements
     */
    @SuppressLint("InflateParams")
    private void initUI(){
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        dialogView = layoutInflater.inflate(R.layout.dialog_show_msg_pushkit,null);

        lvMessageData = dialogView.findViewById(R.id.lv_msg_data_push_kit);

        btnExit = dialogView.findViewById(R.id.btn_exit_push_info);

        //Message Info
        collapse = dialogView.findViewById(R.id.tv_push_info_collapse);
        from = dialogView.findViewById(R.id.tv_push_info_from);
        to = dialogView.findViewById(R.id.tv_push_info_to);
        messageId = dialogView.findViewById(R.id.tv_push_info_message_id);
        originalUrgency = dialogView.findViewById(R.id.tv_push_info_org_urgency);
        urgency = dialogView.findViewById(R.id.tv_push_info_urgency);
        sendTime = dialogView.findViewById(R.id.tv_push_info_send_time);
        messageType = dialogView.findViewById(R.id.tv_push_info_message_type);
        ttl = dialogView.findViewById(R.id.tv_push_info_ttl);

        //Notification Info
        imageUrl = dialogView.findViewById(R.id.tv_push_ntf_img_url);
        title = dialogView.findViewById(R.id.tv_push_ntf_title);
        titleLocKey = dialogView.findViewById(R.id.tv_push_ntf_title_loc_key);
        titleLocArgs = dialogView.findViewById(R.id.tv_push_ntf_title_loc_args);
        body = dialogView.findViewById(R.id.tv_push_ntf_body);
        bodyLocKey = dialogView.findViewById(R.id.tv_push_ntf_body_loc_key);
        bodyLocArgs = dialogView.findViewById(R.id.tv_push_ntf_body_loc_args);
        icon = dialogView.findViewById(R.id.tv_push_ntf_icon);
        sound = dialogView.findViewById(R.id.tv_push_ntf_sound);
        tag = dialogView.findViewById(R.id.tv_push_ntf_tag);
        color = dialogView.findViewById(R.id.tv_push_ntf_color);
        clickAction = dialogView.findViewById(R.id.tv_push_ntf_click_action);
        channelId = dialogView.findViewById(R.id.tv_push_ntf_channel_id);
        link = dialogView.findViewById(R.id.tv_push_ntf_link);
        notifyId = dialogView.findViewById(R.id.tv_push_ntf_notify_id);

    }

    /**
     * Initialize Listeners of UI Elements
     */
    private void initListeners(){

        btnExit.setOnClickListener(v -> dismiss());
    }

    /**
     * set remote message info to UI elements
     */
    private void initMessageInfo(){

        initDataMessage();

        if(message != null){
            //message info
            setMessageInfo();

            //message notification info

            RemoteMessage.Notification notification = message.getNotification();

            if(notification != null){
                setNotificationInfo(notification);
            }
        }
    }

    private void setMessageInfo(){
        collapse.setText(message.getCollapseKey() == null ? "-" : message.getCollapseKey());
        from.setText(message.getFrom() == null ? "-" : message.getFrom());
        to.setText(message.getTo() == null ? "-" : message.getCollapseKey());
        messageId.setText(message.getMessageId() == null ? "-" : message.getCollapseKey());
        originalUrgency.setText(String.valueOf(message.getOriginalUrgency()));
        urgency.setText(String.valueOf(message.getUrgency()));
        sendTime.setText(message.getSentTime() == 0 ? "0" : (new Date(message.getSentTime()* 1000)).toString());
        messageType.setText(message.getMessageType() == null ? "-" : message.getCollapseKey());
        ttl.setText(String.valueOf(message.getTtl()));
    }

    private void setNotificationInfo(RemoteMessage.Notification notification){
        imageUrl.setText(notification.getImageUrl() == null ? "-" : notification.getImageUrl().toString());
        title.setText(notification.getTitle() == null ? "-" : message.getCollapseKey());
        titleLocKey.setText(notification.getTitleLocalizationKey() == null ? "-" : message.getCollapseKey());
        titleLocArgs.setText(Arrays.toString((notification.getTitleLocalizationArgs())) );
        body.setText(notification.getBody() == null ? "-" : message.getCollapseKey());
        bodyLocKey.setText(notification.getBodyLocalizationKey() == null ? "-" : message.getCollapseKey());
        bodyLocArgs.setText(Arrays.toString(notification.getBodyLocalizationArgs()));
        icon.setText(notification.getIcon() == null ? "-" : message.getCollapseKey());
        sound.setText(notification.getSound() == null ? "-" : message.getCollapseKey());
        tag.setText(notification.getTag() == null ? "-" : message.getCollapseKey());
        color.setText(notification.getColor() == null ? "-" : message.getCollapseKey());
        clickAction.setText(notification.getClickAction() == null ? "-" : message.getCollapseKey());
        channelId.setText(notification.getChannelId() == null ? "-" : message.getCollapseKey());
        link.setText(notification.getLink() == null ? "-" : notification.getLink().toString());
        notifyId.setText(String.valueOf(notification.getNotifyId()));
    }

    private void initDataMessage(){

        if(this.message != null){
            String jsonString = message.getData();

            Map<String, String> retMap = new Gson().fromJson(
                    jsonString, new TypeToken<HashMap<String, String>>() {}.getType()
            );

            NotificationDataAdapter adapter = new NotificationDataAdapter(context,retMap);

            lvMessageData.setAdapter(adapter);
        }
    }
}
