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
package com.hms.explorehms.scankit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.R;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;

import java.util.Objects;


public class DisplayMulActivity extends AppCompatActivity {

    private View view;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_display_mul);

        setupToolbar();

        LinearLayout scrollView = findViewById(R.id.scroll_item);
        Window window = getWindow();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        Vibrator vibrator = (Vibrator) this.getSystemService(android.content.Context.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        //Obtain the scanning result.
        Parcelable[] obj = getIntent().getParcelableArrayExtra(ScanUtil.RESULT);
        if (obj != null) {
            for (Parcelable parcelable : obj) {
                if (parcelable instanceof HmsScan && !TextUtils.isEmpty(((HmsScan) parcelable).getOriginalValue())) {
                    view = layoutInflater.inflate(R.layout.activity_display_item, null);
                    scrollView.addView(view);
                    valueFillIn((HmsScan) parcelable, view);
                }
            }
        }
        view.findViewById(R.id.line).setVisibility(View.GONE);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_result);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Display the scanning result in TextView.
     */
    private void valueFillIn(final HmsScan hmsScan, View view) {
        //Define a view.
        TextView codeFormat = view.findViewById(R.id.barcode_type);

        TextView resultType = view.findViewById(R.id.barcode_type_mon);
        TextView rawResult = view.findViewById(R.id.barcode_rawValue);

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
        if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.PURE_TEXT_FORM) {
                resultType.setText(R.string.text);
            } else if (hmsScan.getScanTypeForm() == HmsScan.EVENT_INFO_FORM) {
                resultType.setText(R.string.event);
            } else if (hmsScan.getScanTypeForm() == HmsScan.CONTACT_DETAIL_FORM) {
                resultType.setText(R.string.contact);
            } else if (hmsScan.getScanTypeForm() == HmsScan.DRIVER_INFO_FORM) {
                resultType.setText(R.string.licence);
            } else if (hmsScan.getScanTypeForm() == HmsScan.EMAIL_CONTENT_FORM) {
                resultType.setText(R.string.email);
            } else if (hmsScan.getScanTypeForm() == HmsScan.TEL_PHONE_NUMBER_FORM) {
                resultType.setText(R.string.tel);
            } else if (hmsScan.getScanTypeForm() == HmsScan.SMS_FORM) {
                resultType.setText(R.string.sms);

            } else if (hmsScan.getScanTypeForm() == HmsScan.WIFI_CONNECT_INFO_FORM) {
                resultType.setText(R.string.wifi);

            } else if (hmsScan.getScanTypeForm() == HmsScan.URL_FORM) {
                resultType.setText(R.string.website);

            } else if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType.setText(R.string.product);

            } else {
                resultType.setText(R.string.text);
            }
        } else if (hmsScan.getScanType() == HmsScan.EAN13_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ISBN_NUMBER_FORM) {
                resultType.setText(R.string.isbn);
            } else if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType.setText(R.string.product);
            } else {
                resultType.setText(R.string.text);
            }
        } else if (hmsScan.getScanType() == HmsScan.EAN8_SCAN_TYPE || hmsScan.getScanType() == HmsScan.UPCCODE_A_SCAN_TYPE
                || hmsScan.getScanType() == HmsScan.UPCCODE_E_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType.setText(R.string.product);
            } else {
                resultType.setText(R.string.text);
            }
        } else {
            resultType.setText(R.string.text);
        }
    }


}
