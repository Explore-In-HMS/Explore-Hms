package com.hms.explorehms.huawei.feature_awarenesskit.ui.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.hms.explorehms.huawei.feature_awarenesskit.R;
import com.hms.explorehms.huawei.feature_awarenesskit.databinding.FragmentDataDonationAwarenessBinding;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.DonateClient;
import com.huawei.hms.kit.awareness.donate.message.Content;
import com.huawei.hms.kit.awareness.donate.message.ContentData;
import com.huawei.hms.kit.awareness.donate.message.Message;
import com.huawei.hms.kit.awareness.donate.message.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;


public class DataDonationAwarenessFragment extends Fragment {


    private static final String TAG = "DataDonationAwareness";
    private FragmentDataDonationAwarenessBinding binding;
    EditText messageName, messageId, deviceCategory, deviceName, messageVersion, packageName;
    MaterialButton btnDonate;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDataDonationAwarenessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Data Donation");
        initialize();
        getDataDonation();

    }

    private void initialize() {
        messageName = binding.etMessageName;
        messageId = binding.etMessageName;
        deviceCategory = binding.etMessageName;
        deviceName = binding.etMessageName;
        messageVersion = binding.etMessageName;
        packageName = binding.etMessageName;
        btnDonate = binding.btnDataDonation;
    }

    private void getDataDonation() {
        btnDonate.setOnClickListener(t -> {
            if (messageName.getText().toString().equals("")
                    || messageId.getText().toString().equals("")
                    || deviceCategory.getText().toString().equals("")
                    || deviceName.getText().toString().equals("")
                    || messageVersion.getText().toString().equals("")
                    || packageName.getText().toString().equals("")) {
                Toast.makeText(requireContext(), "You have to fill all the information to post the data donation", Toast.LENGTH_LONG).show();
            } else {
                // 0. Create a client.
                DonateClient client = Awareness.getDonateClient(requireContext());
                // 1. Create a Session object to construct a Message object.
                Session session = new Session();
                session.setMessageName(messageName.getText().toString());
                session.setMessageId(messageId.getText().toString()); // The value of MessageId must be unique.
                session.setDeviceModel(Build.MODEL);
                session.setDeviceCategory(deviceCategory.getText().toString());
                session.setDeviceName(deviceName.getText().toString());
                session.setMessageVersion(messageVersion.getText().toString());
                session.setPackageName(packageName.getText().toString());
                JSONObject eventCommonInfo = new JSONObject();
                JSONObject faResource = new JSONObject();
                JSONObject predictInfo1 = new JSONObject();
                try {
                    // 2. Create an instance of eventCommonInfo to construct an object of contentData.
                    eventCommonInfo.put("timeZone", TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
                    eventCommonInfo.put("createTime", System.currentTimeMillis());
                    // 3. Create your custom Payload object to construct an object of contentData.
                    faResource.put("bundleName", "xxxx"); // Set the value of bundleName as needed.
                    faResource.put("moudleName", "yyyy"); // Set the value of moudleName as needed.
                    faResource.put("abilityName", "zzzz"); // Set the value of abilityName as needed.
                    faResource.put("formName", "aaaa"); // Set the value of formName as needed.
                    // 4. Set the time for feature prediction.
                    predictInfo1.put("featureName", "playListRecommend"); // Set the value of featureName as needed.
                    JSONArray timelineInfo = new JSONArray();
                    JSONObject timelineInfoItem1 = new JSONObject();
                    timelineInfoItem1.put("startTime", System.currentTimeMillis()); // Set the value of startTime as needed.
                    timelineInfoItem1.put("endTime", System.currentTimeMillis() + 30 * 60 * 1000); // The time range is from the current time to 7 days later.
                    timelineInfo.put(timelineInfoItem1);
                    predictInfo1.put("timelineInfo", timelineInfo);
                } catch (JSONException e) {
                    Log.w(String.valueOf(TAG), "dataReport JSONException");
                }
                // 5. Create an object of ContentData to construct a Message object.
                ContentData contentData = new ContentData();
                // Set Header for constructing an object of contentData.
                contentData.setHeader("namespace", "event");
                contentData.setHeader("name", "featurePrediction");
                // Set Payload for constructing an object of contentData.
                contentData.setPayload("eventCommonInfo", eventCommonInfo);
                contentData.setPayload("predictInfo", predictInfo1);
                contentData.setPayload("faResource", faResource);
                List<ContentData> contentDatas = new ArrayList<>();
                contentDatas.add(contentData);
                // 6. Create an object of Content to construct an object of Message.
                Content content = new Content();
                content.setContentVersion("2.1");
                content.setContentData(contentDatas);
                // 7. Create a Message object for calling the Donate API.
                Message message = new Message();
                message.setSession(session);
                message.setContent(content);
                // 8. Report data and process the result.
                client.sendMessage(message).addOnSuccessListener(donateResponse -> {
                    // Data donation is successful.
                    Toast.makeText(requireContext(), "sendMessage success!", Toast.LENGTH_SHORT).show();
                    btnDonate.setBackgroundColor(Color.GREEN);
                    clear();
                }).addOnFailureListener(e -> {
                    // Data donation failed.
                    Toast.makeText(requireContext(), "sendMessage failed!", Toast.LENGTH_SHORT).show();
                    btnDonate.setBackgroundColor(Color.GRAY);
                });
            }
        });

    }

    public void clear() {
        messageName.setText("");
        messageId.setText("");
        deviceCategory.setText("");
        deviceName.setText("");
        messageVersion.setText("");
        packageName.setText("");
        messageName.requestFocus();
    }
}