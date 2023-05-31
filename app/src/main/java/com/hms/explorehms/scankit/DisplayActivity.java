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
package com.hms.explorehms.scankit;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.hms.explorehms.Util;
import com.hms.explorehms.scankit.action.CalendarEventAction;
import com.hms.explorehms.scankit.action.ContactInfoAction;
import com.hms.explorehms.scankit.action.DialAction;
import com.hms.explorehms.scankit.action.EmailAction;
import com.hms.explorehms.scankit.action.LocationAction;
import com.hms.explorehms.scankit.action.SMSAction;
import com.hms.explorehms.scankit.action.WifiAdmin;

import com.hms.explorehms.R;
import com.huawei.hms.common.util.Logger;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;

import java.util.Objects;

public class DisplayActivity extends AppCompatActivity {
    private final static String TAG =  "DisplayActivity";

    private Button copyButton;
    private TextView codeFormat;
    private TextView resultType;
    private TextView rawResult;
    private ImageView icon;
    private TextView iconText;
    private TextView resultTypeTitle;
    private HmsScan.WiFiConnectionInfo wiFiConnectionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        setupToolbar();

        codeFormat = findViewById(R.id.barcode_type);
        icon = findViewById(R.id.diplay_icon);
        iconText = findViewById(R.id.diplay_text);
        resultType = findViewById(R.id.barcode_type_mon);
        rawResult = findViewById(R.id.barcode_rawValue);
        resultTypeTitle = findViewById(R.id.result_t);
        copyButton = findViewById(R.id.button_operate);

        //Obtain the scanning result.
        HmsScan obj = (HmsScan) getIntent().getParcelableArrayExtra(ScanUtil.RESULT)[0];
        try {
            valueFillIn(obj);
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }
        Vibrator vibrator = (Vibrator)this.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_result);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.scan_kit_link_documentation_link));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @SuppressLint("QueryPermissionsNeeded")
    private void valueFillIn(final HmsScan hmsScan) {
        rawResult.setText(hmsScan.getOriginalValue());
        if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
            codeFormat.setText(R.string.qr_code);
        } else if (hmsScan.getScanType() == HmsScan.AZTEC_SCAN_TYPE) {
            codeFormat.setText(R.string.AZTEC_code);
        } else if (hmsScan.getScanType() == HmsScan.DATAMATRIX_SCAN_TYPE) {
            codeFormat.setText(R.string.DATAMATRIX_code);
        } else if (hmsScan.getScanType() == HmsScan.PDF417_SCAN_TYPE) {
            codeFormat.setText(R.string.PDF417_code);
        } else if (hmsScan.getScanType() == HmsScan.CODE93_SCAN_TYPE) {
            codeFormat.setText(R.string.CODE93);
        } else if (hmsScan.getScanType() == HmsScan.CODE39_SCAN_TYPE) {
            codeFormat.setText(R.string.CODE39);
        } else if (hmsScan.getScanType() == HmsScan.CODE128_SCAN_TYPE) {
            codeFormat.setText(R.string.CODE128);

        } else if (hmsScan.getScanType() == HmsScan.EAN13_SCAN_TYPE) {
            codeFormat.setText(R.string.EAN13_code);

        } else if (hmsScan.getScanType() == HmsScan.EAN8_SCAN_TYPE) {
            codeFormat.setText(R.string.EAN8_code);

        } else if (hmsScan.getScanType() == HmsScan.ITF14_SCAN_TYPE) {
            codeFormat.setText(R.string.ITF14_code);

        } else if (hmsScan.getScanType() == HmsScan.UPCCODE_A_SCAN_TYPE) {
            codeFormat.setText(R.string.UPCCODE_A);

        } else if (hmsScan.getScanType() == HmsScan.UPCCODE_E_SCAN_TYPE) {
            codeFormat.setText(R.string.UPCCODE_E);

        } else if (hmsScan.getScanType() == HmsScan.CODABAR_SCAN_TYPE) {
            codeFormat.setText(R.string.CODABAR);
        }

        //Show the barcode result.
        if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
            resultType.setVisibility(View.VISIBLE);
            resultTypeTitle.setVisibility(View.VISIBLE);
            if (hmsScan.getScanTypeForm() == HmsScan.PURE_TEXT_FORM) {
                icon.setImageResource(R.drawable.text);
                iconText.setText(R.string.text);
                resultType.setText(R.string.text);
                copyButton.setText(getText(R.string.copy));
                copyButton.setOnClickListener(v -> {
                    if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(DisplayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.EVENT_INFO_FORM) {
                icon.setImageResource(R.drawable.event);
                iconText.setText(R.string.event);
                resultType.setText(R.string.event);
                copyButton.setText(getText(R.string.add_calendar));
                copyButton.setOnClickListener(v -> {
                    startActivity(CalendarEventAction.getCalendarEventIntent(hmsScan.getEventInfo()));
                    DisplayActivity.this.finish();
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.CONTACT_DETAIL_FORM) {
                icon.setImageResource(R.drawable.contact);
                iconText.setText(R.string.contact);
                resultType.setText(R.string.contact);
                copyButton.setText(getText(R.string.add_contact));
                copyButton.setOnClickListener(v -> {
                    startActivity(ContactInfoAction.getContactInfoIntent(hmsScan.getContactDetail()));
                    DisplayActivity.this.finish();
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.DRIVER_INFO_FORM) {
                icon.setImageResource(R.drawable.text);
                iconText.setText(R.string.text);
                resultType.setText(R.string.licence);
                copyButton.setText(getText(R.string.copy));
                copyButton.setOnClickListener(v -> {
                    if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(DisplayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.EMAIL_CONTENT_FORM) {
                icon.setImageResource(R.drawable.email);
                iconText.setText(R.string.email);
                resultType.setText(R.string.email);
                copyButton.setText(getText(R.string.send_email));
                copyButton.setOnClickListener(v -> {
                    startActivity(Intent.createChooser(EmailAction.getEmailInfo(hmsScan.getEmailContent()), "Select email application."));
                    //startActivity(EmailAction.getContactInfoIntent(result.getEmailContent()));
                    DisplayActivity.this.finish();
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.LOCATION_COORDINATE_FORM) {
                icon.setImageResource(R.drawable.location);
                iconText.setText(R.string.location);
                resultType.setText(R.string.location);
                if (LocationAction.checkMapAppExist(getApplicationContext())) {
                    copyButton.setText(getText(R.string.navigation));
                    copyButton.setOnClickListener(v -> {
                        try {
                            startActivity(LocationAction.getLocationInfo(hmsScan.getLocationCoordinate()));
                            DisplayActivity.this.finish();
                        } catch (Exception e) {
                            Logger.e(TAG, "Error", e);
                        }
                    });
                } else {
                    copyButton.setText(getText(R.string.copy));
                    copyButton.setOnClickListener(v -> {
                        if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                            cm.setPrimaryClip(mClipData);
                            Toast.makeText(DisplayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.TEL_PHONE_NUMBER_FORM) {
                icon.setImageResource(R.drawable.tel);
                iconText.setText(R.string.tel);
                resultType.setText(R.string.tel);
                copyButton.setText(getText(R.string.call));
                copyButton.setOnClickListener(v -> {
                    try {
                        startActivity(DialAction.getDialIntent(hmsScan.getTelPhoneNumber()));
                        DisplayActivity.this.finish();
                    } catch (Exception e) {
                        Logger.e(TAG, "Error", e);
                    }
                });

            } else if (hmsScan.getScanTypeForm() == HmsScan.SMS_FORM) {
                icon.setImageResource(R.drawable.sms);
                iconText.setText(R.string.sms);
                resultType.setText(R.string.sms);
                copyButton.setText(getText(R.string.send_sms));
                copyButton.setOnClickListener(v -> {
                    startActivity(SMSAction.getSMSInfo(hmsScan.getSmsContent()));
                    DisplayActivity.this.finish();
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.WIFI_CONNECT_INFO_FORM) {
                icon.setImageResource(R.drawable.wifi);
                iconText.setText(R.string.wifi);
                resultType.setText(R.string.wifi);
                copyButton.setText(getText(R.string.connect_network));
                wiFiConnectionInfo = hmsScan.wifiConnectionInfo;
                copyButton.setOnClickListener(v -> {
                    String permissionWifi = Manifest.permission.ACCESS_WIFI_STATE;
                    String permissionWifi2 = Manifest.permission.CHANGE_WIFI_STATE;
                    String[] permission = new String[]{permissionWifi, permissionWifi2};
                    ActivityCompat.requestPermissions(DisplayActivity.this, permission, CalendarEvent);
                });

            } else if (hmsScan.getScanTypeForm() == HmsScan.URL_FORM) {
                icon.setImageResource(R.drawable.website);
                iconText.setText(R.string.website);
                resultType.setText(R.string.website);
                copyButton.setText(getText(R.string.open_browser));
                copyButton.setOnClickListener(v -> {
                    Uri webpage = Uri.parse(hmsScan.getOriginalValue());
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                });
                resultType.setText(R.string.website);
            } else {
                icon.setImageResource(R.drawable.text);
                iconText.setText(R.string.text);
                resultType.setText(R.string.text);
                copyButton.setText(getText(R.string.copy));
                copyButton.setOnClickListener(v -> {
                    if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(DisplayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (hmsScan.getScanType() == HmsScan.EAN13_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ISBN_NUMBER_FORM) {
                icon.setImageResource(R.drawable.isbn);
                iconText.setText(R.string.isbn);
                resultType.setVisibility(View.VISIBLE);
                resultType.setVisibility(View.VISIBLE);
                resultTypeTitle.setVisibility(View.VISIBLE);
                resultType.setText(R.string.isbn);
            } else if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                icon.setImageResource(R.drawable.product);
                iconText.setText(R.string.product);
                resultType.setVisibility(View.VISIBLE);
                resultTypeTitle.setVisibility(View.VISIBLE);
                resultType.setText(R.string.product);
            } else {
                icon.setImageResource(R.drawable.text);
                iconText.setText(R.string.text);
                resultType.setVisibility(View.GONE);
                resultTypeTitle.setVisibility(View.GONE);
            }
            copyButton.setText(getText(R.string.copy));
            copyButton.setOnClickListener(v -> {
                if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                    //Obtain the clipboard manager.
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(DisplayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (hmsScan.getScanType() == HmsScan.EAN8_SCAN_TYPE || hmsScan.getScanType() == HmsScan.UPCCODE_A_SCAN_TYPE
                || hmsScan.getScanType() == HmsScan.UPCCODE_E_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                icon.setImageResource(R.drawable.product);
                iconText.setText(R.string.product);
                resultType.setVisibility(View.VISIBLE);
                resultTypeTitle.setVisibility(View.VISIBLE);
                resultType.setText(R.string.product);
            } else {
                icon.setImageResource(R.drawable.text);
                iconText.setText(R.string.text);
                resultType.setVisibility(View.GONE);
                resultTypeTitle.setVisibility(View.GONE);
            }
            copyButton.setText(getText(R.string.copy));
            copyButton.setOnClickListener(v -> {
                if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(DisplayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            icon.setImageResource(R.drawable.text);
            iconText.setText(R.string.text);
            copyButton.setText(getText(R.string.copy));
            copyButton.setOnClickListener(v -> {
                if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(DisplayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                }
            });
            resultType.setVisibility(View.GONE);
            resultTypeTitle.setVisibility(View.GONE);
        }
    }

    final int CalendarEvent = 0x3300;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CalendarEvent) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (wiFiConnectionInfo != null) {
                    new WifiAdmin(DisplayActivity.this).connect(wiFiConnectionInfo.getSsidNumber(),
                            wiFiConnectionInfo.getPassword(), wiFiConnectionInfo.getCipherMode());
                    DisplayActivity.this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    DisplayActivity.this.finish();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
