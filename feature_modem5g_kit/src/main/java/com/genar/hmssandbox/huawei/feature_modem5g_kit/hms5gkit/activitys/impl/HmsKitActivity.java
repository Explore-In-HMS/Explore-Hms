/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.genar.hmssandbox.huawei.feature_modem5g_kit.R;
import com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.IHmsKitActivity;
import com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.common.LoadingDialogCenter;
import com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.constants.QueryParamsEnum;
import com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.presenter.HmsKitPresenter;
import com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.utils.TimeStampUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HmsKitActivity extends HmsKitBaseActivity implements IHmsKitActivity {
    private static final String TAG = "[5ghmskit] HmsKitActivity";
    public static final int TEXT_LENGTH = 2048;

    // Collection object used to store selected items
    // After query, all selected will not be cancelled
    List<String> selected = new CopyOnWriteArrayList<>();

    @BindView(R.id.registerBtn)
    Button regBtn;

    @BindView(R.id.queryBtn)
    Button queryBtn;

    @BindView(R.id.unRegister)
    Button unRegisterBtn;

    @BindView(R.id.textOutput)
    TextView outputText;

    private HmsKitPresenter mHmsKitPresenter;
    List<CheckBox> mCheckBoxList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        ButterKnife.bind(this);
        Context context = getApplicationContext();
        mHmsKitPresenter = new HmsKitPresenter(context, this);
    }

    @Override
    public void initView() {
        super.initView();
        regBtn.setOnClickListener(view -> mHmsKitPresenter.registerCallback());
        queryBtn.setOnClickListener(view -> {
            if (!mHmsKitPresenter.getConnectStatus()) {
                hasNotRegister();
                return;
            }
            if (selected.size() == 0) {
                //ToastUtil.toast("No Data Selected");
                return;
            }
            LoadingDialogCenter.getInstance().showLoadingDialog(this, "Querying...");
            mHmsKitPresenter.queryModem(selected);
        });
        unRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHmsKitPresenter.unRegisterCallback();
                outputText.setText("");
            }
        });
        initCheckBoxes();
        outputText.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public void showQueryResult() {
        LoadingDialogCenter.getInstance().dismissLoadingDialog();
    }

    @Override
    public void showUnRegisterResult() {
        cancelAll();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showDataResult(String result) {
        if (outputText != null) {
            runOnUiThread(() -> {
                String output = outputText.getText().toString();
                if (output.length() > TEXT_LENGTH) {
                    output = "";
                }
                outputText.setText(result + System.lineSeparator() + System.lineSeparator() + output);
            });
        }
    }

    private CompoundButton.OnCheckedChangeListener mChangeListener = (buttonView, isChecked) -> {
        int resourceId = buttonView.getId();
        String selectStr = QueryParamsEnum.getResourceId2QueryNameMap().getOrDefault(resourceId, "");
        if (isChecked) {
            // Save a Map (key: resourceId, value: queryName),
            // Add the selected text to the list,
            if (!"".equals(selectStr)) {
                selected.add(selectStr);
            }
        } else {
            // The text to be canceled is deleted from the selected list
            selected.remove(selectStr);
        }
    };

    private void hasNotRegister() {
        String content = TimeStampUtils.getCurDateStr() + " aidl has not register";
        showDataResult(content);
        Log.i(TAG, content);
    }

    private void initCheckBoxes() {
        CheckBox[] checkBoxes = new CheckBox[]{
                lteCbox, lteArfcnCbox, ltePhyCellIdCbox, lteDlFreqCbox, lteBandCbox,
                lteMimoCbox, lteDlBandWidthCbox, lteModeTypeCbox, lteTrackAreaCodeCbox,
                lteCellIdentityCbox, lteMccCbox, lteMncCbox, lteMeasCellCbox, lteMeasCellCellIdCbox,
                lteMeasCellRsrpCbox, lteMeasCellRsrqCbox, lteMeasCellSinrCbox, lteScellCbox,
                lteScellArfcnCbox, lteScellPhyCellIdCbox, lteScellDlFreqCbox, lteScellBandCbox,
                lteScellMimoCbox, lteScellDlBandWidthCbox, lteScellRsrpCbox, lteScellRsrqCbox,
                lteScellSinrCbox,
                nrCbox, nrServCellInfoCbox, nrServCellInfoSsbArfchCbox,
                nrServCellInfoPhyCellIdCbox, nrServCellInfoBandCbox, nrServCellInfoCgTypeCbox,
                nrServCellInfoCellTypeCbox, nrServCellInfoScsTypeCbox, nrServCellInfoDlMimoLayersCbox,
                nrServCellInfoDssTypeCbox, nrServCellInfoSsbRsrpCbox, nrServCellInfoSsbRsrqCbox,
                nrServCellInfoSsbSinrCbox, nrServCellInfoCsiRsrpCbox, nrServCellInfoCsiRsrqCbox,
                nrServCellInfoCsiSinrCbox,
                bearerCbox, bearerDrbInfoCbox, bearerDrbInfoRbIdCbox,
                bearerDrbInfoPdcpVersionCbox, bearerDrbInfoBearerTypeCbox,
                bearerDrbInfoDataSplitThresholdCbox
        };
        mCheckBoxList = new ArrayList<>(Arrays.asList(checkBoxes));
        for (CheckBox checkBox : mCheckBoxList) {
            checkBox.setOnCheckedChangeListener(mChangeListener);
        }
    }

    public void cancelAll() {
        for (CheckBox checkBox : mCheckBoxList) {
            checkBox.setChecked(false);
            textBlack(checkBox);
        }
    }


    private void textBlack(CompoundButton buttonView) {
        buttonView.setTextColor(getApplicationContext().getColor(R.color.blackFont));
    }
}
