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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.textTranslation;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.langdetect.MLDetectedLang;
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory;
import com.huawei.hms.mlsdk.langdetect.cloud.MLRemoteLangDetector;
import com.huawei.hms.mlsdk.langdetect.cloud.MLRemoteLangDetectorSetting;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TextTranslationOnlineActivity extends AppCompatActivity {


    //region variablesAndObjects
    private static final String TAG = TextTranslationOnlineActivity.class.getSimpleName();

    private Unbinder unbinder;


    @Nullable
    @BindView(R.id.spSourceTypeOnline)
    Spinner spSourceTypeOnline;

    @Nullable
    @BindView(R.id.spDestTypeOnline)
    Spinner spDestTypeOnline;

    @Nullable
    @BindView(R.id.et_inputOnline)
    EditText etInputStringOnline;

    @Nullable
    @BindView(R.id.tv_src_lang_char_count_Online)
    TextView tvInputLangOnline;

    @Nullable
    @BindView(R.id.tv_outputOnline)
    TextView tvOutputStringOnline;

    @Nullable
    @BindView(R.id.tv_dest_lang_char_count_Online)
    TextView tvOutputLenOnline;

    @Nullable
    @BindView(R.id.btn_translatorOnline)
    Button btbTranslatorOnline;

    @Nullable
    @BindView(R.id.btn_identificationOnline)
    Button btnIdentificationOnline;

    @Nullable
    @BindView(R.id.tv_timeOnline)
    TextView tv_timeOnline;

    @Nullable
    @BindView(R.id.progressBarOnline)
    ProgressBar progressBarOnline;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    private String srcLanguage = "Auto";
    private String dstLanguage = "TR";
    public static final String EN = "en";

    private ArrayAdapter<String> spSourceAdapter;
    private ArrayAdapter<String> spDestAdapter;

    private MLRemoteTranslator mlRemoteTranslator;
    private MLRemoteLangDetectorSetting mlRemoteLangDetectorSetting;
    private MLRemoteLangDetector mlRemoteLangDetector;

    //region languageVariables

    private static final List<String> CODE_LIST = new ArrayList<>(Arrays.asList(
            "zh", "en", "fr", "th", "ja", "de", "ru", "es",
            "ar", "tr", "pt", "it", "ro", "no", "ms", "pl", "sv", "fi", "da", "ko",
            "vi", "id", "cs", "he", "el", "hi", "tl", "sr", "ro", "my",
            "km", "nl", "et", "fa", "lv", "sk", "ta", "hu", "sq", "cy", "ga", "ht"));

    private static final List<String> LANGUAGE_LIST = new ArrayList<>(Arrays.asList(
            "Chinese", "English", "French", "Thai", "Japanese", "German", "Russian", "Spanish",
            "Arabic", "Turkish", "Portuguese", "Italian", "Romanian", "Norwegian", "Malaysian", "Polish"
            , "Swedish", "Finnish", "Danish", "Korean", "Vietnamese", "Indonesian",
            "Czech", "Hebrew", "Greece", "Hindi", "Filipino", "Serbian", "Myanmar",
            "Khmer", "Netherlands", "Estonian", "Persian", "Latvian", "Slovak", "Tamil", "Hungarian", "Albanian", "Welsh", "Irish", "Haitian"));

    private static final String[] SOURCE_LANGUAGE_CODE = new String[]{"Auto",
            "ZH", "EN", "FR", "TH", "JA", "DE", "RU", "ES",
            "AR", "TR", "PT", "IT", "PL", "MS", "SV", "FI", "NO", "DA", "KO", "VI", "ID", "CS", "HE", "EL", "HI", "TL",
            "SR", "RO", "TA", "HU", "NL", "FA", "SK", "ET", "LV", "KM", "SQ", "CY", "GA", "HT"};

    private static final String[] DEST_LANGUAGE_CODE = new String[]{
            "ZH", "EN", "FR", "TH", "JA", "DE", "RU", "ES",
            "AR", "TR", "PT", "IT", "PL", "MS", "SV", "FI", "NO", "DA", "KO", "VI", "ID", "CS", "HE", "EL", "HI", "TL",
            "SR", "RO", "TA", "HU", "NL", "FA", "SK", "ET", "LV", "KM", "KM", "SQ", "CY", "GA", "HT"};

    private static final List<String> SP_SOURCE_LIST_EN = new ArrayList<>(Arrays.asList("Auto",
            "Chinese", "English", "French", "Thai", "Japanese", "German", "Russian", "Spanish",
            "Arabic", "Turkish", "Portuguese", "Italian", "Polish", "Malaysian", "Swedish", "Finnish", "Norwegian", "Danish", "Korean", "Vietnamese",
            "Indonesian", "Czech", "Hebrew", " Greek", "Hindi", "Filipino", "Serbian",
            "Romanian", "Tamil", "Hungarian", "Netherlands", "Persian", "Slovak", "Estonian",
            "Latvian", "Khmer", "Albanian", "Welsh", "Irish", "Haitian"));

    private static final List<String> SP_DEST_LIST_EN = new ArrayList<>(Arrays.asList(
            "Chinese", "English", "French", "Thai", "Japanese", "German", "Russian", "Spanish",
            "Arabic", "Turkish", "Portuguese", "Italian", "Polish", "Malaysian", "Swedish", "Finnish", "Norwegian", "Danish", "Korean", "Vietnamese",
            "Indonesian", "Czech", "Hebrew", " Greek", "Hindi", "Filipino", "Serbian",
            "Romanian", "Tamil", "Hungarian", "Netherlands", "Persian", "Slovak", "Estonian",
            "Latvian", "Khmer", "Albanian", "Welsh", "Irish", "Haitian"));

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_text_translation_online);

        unbinder = ButterKnife.bind(this);

        Utils.setApiKeyForRemoteMLApplication(this);
        setupToolbar();

        createAndSetSpinner();

        updateLength(tvInputLangOnline, etInputStringOnline.getText().length());

        etInputStringOnline.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                //This method will be triggered before the text changed.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.i(TAG, "onTextChanged start : " + start + " : before : " + before + " : count : " + count);
                updateLength(tvInputLangOnline, charSequence.toString().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged editable : " + editable.toString() + " : length : " + editable.toString().length());
            }
        });
    }


    @OnClick({R.id.btn_SwitchLangOnline, R.id.btn_translatorOnline, R.id.btn_identificationOnline})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_SwitchLangOnline:
                hideSoftKeyboard(this, etInputStringOnline);
                doLanguageSwitch();
                break;
            case R.id.btn_translatorOnline:
                hideSoftKeyboard(this, etInputStringOnline);
                if (Utils.haveNetworkConnection(this)) {
                    doTranslate();
                } else {
                    DialogUtils.showDialogNetworkWarning(
                            this,
                            "NEED NETWORK PERMISSION",
                            "Would You Like To Go To Settings To Open Network?",
                            R.drawable.icon_settings,
                            "You can not use Remote Operation without Internet Connection!",
                            "YES GO", "CANCEL");
                }
                break;
            case R.id.btn_identificationOnline:
                hideSoftKeyboard(this, etInputStringOnline);
                if (Utils.haveNetworkConnection(this)) {
                    doLanguageRecognition();
                } else {
                    DialogUtils.showDialogNetworkWarning(
                            this,
                            "NEED NETWORK PERMISSION",
                            "Would You Like To Go To Settings To Open Network?",
                            R.drawable.icon_settings,
                            "You can not use Remote Operation without Internet Connection!",
                            "YES GO", "CANCEL");
                }
                break;
            default:
                break;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_lrs_rt_tt));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showProgress() {
        progressBarOnline.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBarOnline.setVisibility(View.GONE);
    }

    private void createAndSetSpinner() {
        // now, just only English
        spSourceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SP_SOURCE_LIST_EN);
        spDestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SP_DEST_LIST_EN);

        spSourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSourceTypeOnline.setAdapter(spSourceAdapter);

        spDestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDestTypeOnline.setAdapter(spDestAdapter);
        // set default destination lang to Turkish
        spDestTypeOnline.setSelection(9);

        spSourceTypeOnline.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                srcLanguage = SOURCE_LANGUAGE_CODE[position];
                Log.i(TAG, "createAndSetSpinner srcLanguage : " + srcLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This method will be triggered when nothing is selected.
            }
        });


        spDestTypeOnline.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dstLanguage = DEST_LANGUAGE_CODE[position];
                Log.i(TAG, "createAndSetSpinner dstLanguage : " + dstLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This method will be triggered when nothing is selected.
            }
        });
    }

    // check and remove for chinese lang
    public boolean isEngLanguage() {
        Locale locale = Locale.getDefault();
        String strLan = locale.getLanguage();
        return strLan != null && EN.equals(strLan);
    }


    /* ------------------------------------------------------------------------------------------ */
    //region Language Selection Operations

    private void doLanguageSwitch() {
        String str = srcLanguage;
        srcLanguage = dstLanguage;
        dstLanguage = str;
        updateSourceLanguage(srcLanguage);
        updateDestLanguage(dstLanguage);
        String inputStr = tvOutputStringOnline.getText().toString();
        String outputStr = etInputStringOnline.getText().toString();
        updateInputText(inputStr);
        updateOutputText(outputStr);

    }

    private void updateSourceLanguage(String code) {
        int count = spSourceAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (getLanguageName(code).equalsIgnoreCase(spSourceAdapter.getItem(i))) {
                spSourceTypeOnline.setSelection(i, true);
                return;
            }
        }
        spSourceTypeOnline.setSelection(0, true);
    }

    // edit and change this method.
    private void updateDestLanguage(String code) {
        if (code.equalsIgnoreCase(SOURCE_LANGUAGE_CODE[0]) || code.equalsIgnoreCase(SP_SOURCE_LIST_EN.get(0))) {
            dstLanguage = DEST_LANGUAGE_CODE[0];
            return;
        }
        int count = spDestAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (getLanguageName(code).equalsIgnoreCase(spDestAdapter.getItem(i))) {
                spDestTypeOnline.setSelection(i, true);
                return;
            }
        }
        spDestTypeOnline.setSelection(0, true);
    }

    private String getLanguageName(String code) {
        int index = 0;
        for (int i = 0; i < SOURCE_LANGUAGE_CODE.length; i++) {
            if (code.equalsIgnoreCase(SOURCE_LANGUAGE_CODE[i])) {
                index = i;
                break;
            }
        }
        return spSourceAdapter.getItem(index);
    }


    private void updateInputText(final String text) {
        if (text == null || text.isEmpty()) {
            Log.w(TAG, "updateInputText: text is empty");
            return;
        }

        runOnUiThread(() -> {
            etInputStringOnline.setText(text);
            updateLength(tvInputLangOnline, text.length());
        });
    }

    private void updateOutputText(final String text) {
        if (text == null || text.isEmpty()) {
            Log.w(TAG, "updateOutputText: text is empty");
            return;
        }

        runOnUiThread(() -> {
            displaySuccessAnalyseResults(text);
            updateLength(tvOutputLenOnline, text.length());
        });

        hideProgress();
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    /* ------------------------------------------------------------------------------------------ */
    //region Display Result Operations

    private void displaySuccessAnalyseResults(String text) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        tvOutputStringOnline.setText(text);
        Log.i(TAG, "Success Translation Results : " + text);
        resultLogs.setText("Success Translation Results : with " + text.length() + " characters :\n" + text);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Failure Translation Results : " + msg);
        resultLogs.setText("Failure Translation Results : \n" + msg);
        tvOutputStringOnline.setText("");
        Utils.showToastMessage(getApplicationContext(), "Translation Process was Failed : " + msg);
    }

    private void updateLength(TextView view, int length) {
        view.setText(String.format(Locale.ENGLISH, "%d words", length));
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    /* ------------------------------------------------------------------------------------------ */
    //region Translation Operations
    private void doTranslate() {
        showProgress();
        // Translating, get data, and update output boxes.
        String sourceText = getInputText();
        String sourceLang = getSourceType();
        String targetLang = getDestType();

        updateInputText(sourceText);

        MLRemoteTranslateSetting mlRemoteTranslateSetting = new MLRemoteTranslateSetting.Factory()
                .setSourceLangCode(sourceLang)
                .setTargetLangCode(targetLang)
                .create();
        mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(mlRemoteTranslateSetting);
        final long startTime = System.currentTimeMillis();
        Task<String> task = mlRemoteTranslator.asyncTranslate(sourceText);
        task.addOnSuccessListener(text -> {
            Log.d(TAG, "onSuccess MLRemoteTranslator : " + text);
            long endTime = System.currentTimeMillis();
            updateOutputText(text);
            updateTime(endTime - startTime);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure MLRemoteTranslator : ", e);
            displayFailureAnalyseResults(e.getMessage());
        });

        autoUpdateSourceLanguage();
    }

    private void autoUpdateSourceLanguage() {
        mlRemoteLangDetectorSetting = new MLRemoteLangDetectorSetting.Factory().setTrustedThreshold(0.01f).create();
        mlRemoteLangDetector = MLLangDetectorFactory.getInstance().getRemoteLangDetector(mlRemoteLangDetectorSetting);

        Task<List<MLDetectedLang>> probabilityDetectTask = mlRemoteLangDetector.probabilityDetect(getInputText());
        probabilityDetectTask.addOnSuccessListener(result -> {
            MLDetectedLang recognizedLang = result.get(0);
            String langCode = recognizedLang.getLangCode();
            updateSourceLanguage(langCode);
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure MLRemoteLangDetector : ", e));
    }

    private String getInputText() {
        return etInputStringOnline.getText().toString();
    }

    private String getSourceType() {
        return srcLanguage;
    }

    private String getDestType() {
        return dstLanguage;
    }

    private void updateTime(long time) {
        tv_timeOnline.setText(time + " ms");
    }


    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    /* ------------------------------------------------------------------------------------------ */
    //region Lang Detection Operations
    private void doLanguageRecognition() {
        showProgress();
        MLRemoteLangDetectorSetting setting = new MLRemoteLangDetectorSetting.Factory()
                // Set the minimum confidence threshold for language detection.
                .setTrustedThreshold(0.01f)
                .create();
        MLRemoteLangDetector mlRemoteLangDetector = MLLangDetectorFactory.getInstance()
                .getRemoteLangDetector(setting);
        Task<List<MLDetectedLang>> probabilityDetectTask = mlRemoteLangDetector.probabilityDetect(getInputText());
        final long startTime = System.currentTimeMillis();
        probabilityDetectTask.addOnSuccessListener(result -> {
            long endTime = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            for (MLDetectedLang recognizedLang : result) {
                String langCode = recognizedLang.getLangCode();
                float probability = recognizedLang.getProbability();
                sb.append("Language=" + getEnLanguageName(langCode) + "(" + langCode + "), score=" + probability);
                sb.append(".");
            }
            updateOutputText(sb.toString());
            updateTime(endTime - startTime);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure MLRemoteLangDetector : ", e);
            displayFailureAnalyseResults(e.getMessage());
            mlRemoteLangDetector.stop();
        });

    }

    private String getEnLanguageName(String code) {
        int index = 0;
        for (int i = 0; i < CODE_LIST.size(); i++) {
            if (code.equalsIgnoreCase(CODE_LIST.get(i))) {
                index = i;
                return LANGUAGE_LIST.get(index);
            }
        }
        return code;

    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mlRemoteTranslator != null) {
            mlRemoteTranslator.stop();
        }
        if (mlRemoteLangDetector != null) {
            mlRemoteLangDetector.stop();
        }
    }


}