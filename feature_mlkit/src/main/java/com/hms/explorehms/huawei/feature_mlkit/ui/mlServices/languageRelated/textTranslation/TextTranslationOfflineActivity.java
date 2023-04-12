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
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory;
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetector;
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetectorSetting;
import com.huawei.hms.mlsdk.model.download.MLLocalModelManager;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadListener;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadStrategy;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslateSetting;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslator;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslatorModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TextTranslationOfflineActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = TextTranslationOfflineActivity.class.getSimpleName();

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.spSourceTypeOffline)
    Spinner spSourceTypeOffline;

    @Nullable
    @BindView(R.id.spDestTypeOffline)
    Spinner spDestTypeOffline;

    @Nullable
    @BindView(R.id.btn_downloadSource)
    Button btnDownloadSourceLang;

    @Nullable
    @BindView(R.id.btn_downloadDest)
    Button btnDownloadDestLang;

    @Nullable
    @BindView(R.id.et_inputOffline)
    EditText etInputStringOffline;

    @Nullable
    @BindView(R.id.tv_src_lang_char_count_offline)
    TextView tvInputLangOffline;

    @Nullable
    @BindView(R.id.tv_outputOffline)
    TextView tvOutputStringOffline;

    @Nullable
    @BindView(R.id.tv_dest_lang_char_count)
    TextView tvOutputLenOffline;

    @Nullable
    @BindView(R.id.btn_translatorOffline)
    Button btnTranslatorOffline;

    @Nullable
    @BindView(R.id.btn_identificationOffline)
    Button btnIdentificationOffline;

    @Nullable
    @BindView(R.id.tv_timeOffline)
    TextView tvTimeOffline;

    @Nullable
    @BindView(R.id.progressBarOffline)
    ProgressBar progressBarOffline;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    private static final long M = 1024L * 1024;

    private static final int SOURCE_LANG_PACKAGE = 1;
    private static final int DEST_LANG_PACKAGE = 2;

    private String srcLanguage = "Auto";
    private String dstLanguage = "TR";
    private static final String EN = "en";

    private ArrayAdapter<String> spSourceAdapter;
    private ArrayAdapter<String> spDestAdapter;

    private MLLocalModelManager mlLocalModelManager;

    //region languageVariables

    private static List<String> CODE_LIST;

    private static List<String> LANGUAGE_LIST;

    private static final String[] SOURCE_LANGUAGE_CODE =
            new String[]{"Auto", "ZH", "EN", "ES", "DE", "RU", "FR", "IT", "PT", "TH", "AR", "TR", "JA", "SQ", "CY", "GA", "HT"};

    private static final String[] DEST_LANGUAGE_CODE =
            new String[]{"ZH", "EN", "ES", "DE", "RU", "FR", "IT", "PT", "TH", "AR", "TR", "JA", "SQ", "CY", "GA", "HT"};

    private static List<String> SP_SOURCE_LIST_EN;

    private static List<String> SP_DEST_LIST_EN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_text_translation_offline);

        CODE_LIST = new ArrayList<>(Arrays.asList(getString(R.string.zh), getString(R.string.en), getString(R.string.fr),
                getString(R.string.th), getString(R.string.ja), getString(R.string.de), getString(R.string.ru),
                getString(R.string.es), getString(R.string.ar), getString(R.string.tr), getString(R.string.pt),
                getString(R.string.it), getString(R.string.ro), getString(R.string.no), getString(R.string.sq), getString(R.string.cy), getString(R.string.ga), getString(R.string.ht)));
        LANGUAGE_LIST = new ArrayList<>(Arrays.asList(getString(R.string.chinese), getString(R.string.english), getString(R.string.french),
                getString(R.string.thai), getString(R.string.japanese), getString(R.string.german), getString(R.string.russian),
                getString(R.string.spanish), getString(R.string.arabic), getString(R.string.turkish), getString(R.string.portuguese),
                getString(R.string.italian), getString(R.string.romanian), getString(R.string.norwegian), getString(R.string.Albanian), getString(R.string.Welsh), getString(R.string.Irish), getString(R.string.Haitian)));
        SP_SOURCE_LIST_EN = new ArrayList<>(Arrays.asList(getString(R.string.auto), getString(R.string.chinese), getString(R.string.english),
                getString(R.string.spanish), getString(R.string.german), getString(R.string.russian),
                getString(R.string.french), getString(R.string.italian), getString(R.string.portuguese), getString(R.string.thai),
                getString(R.string.arabic), getString(R.string.turkish), getString(R.string.japanese), getString(R.string.Albanian), getString(R.string.Welsh), getString(R.string.Irish), getString(R.string.Haitian)));
        SP_DEST_LIST_EN = new ArrayList<>(Arrays.asList(getString(R.string.chinese), getString(R.string.english), getString(R.string.spanish),
                getString(R.string.german), getString(R.string.russian), getString(R.string.french), getString(R.string.italian),
                getString(R.string.portuguese), getString(R.string.thai), getString(R.string.arabic), getString(R.string.turkish), getString(R.string.japanese), getString(R.string.Albanian), getString(R.string.Welsh), getString(R.string.Irish), getString(R.string.Haitian)));


        unbinder = ButterKnife.bind(this);
        setupToolbar();

        mlLocalModelManager = MLLocalModelManager.getInstance();

        Utils.setApiKeyForRemoteMLApplication(this);

        createAndSetSpinner();

        updateLength(tvInputLangOffline, etInputStringOffline.getText().length());

        etInputStringOffline.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                //This method will be triggered before the text
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.i(TAG, "onTextChanged start : " + start + " : before : " + before + " : count : " + count);
                updateLength(tvInputLangOffline, charSequence.toString().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged editable : " + editable.toString() + " : length : " + editable.toString().length());
            }
        });

    }


    @OnClick({R.id.btn_SwitchLangOffline, R.id.btn_downloadSource, R.id.btn_downloadDest,
            R.id.btn_translatorOffline, R.id.btn_identificationOffline})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_SwitchLangOffline:
                hideSoftKeyboard(this, etInputStringOffline);
                doLanguageSwitch();
                break;
            case R.id.btn_downloadSource:
                hideSoftKeyboard(this, etInputStringOffline);
                downloadModelLanguagePackage(getSourceType(), SOURCE_LANG_PACKAGE);
                tvOutputStringOffline.setText("");
                break;
            case R.id.btn_downloadDest:
                hideSoftKeyboard(this, etInputStringOffline);
                downloadModelLanguagePackage(getDestType(), DEST_LANG_PACKAGE);
                tvOutputStringOffline.setText("");
                break;
            case R.id.btn_translatorOffline:
                hideSoftKeyboard(this, etInputStringOffline);
                doTranslate();
                break;
            case R.id.btn_identificationOffline:
                hideSoftKeyboard(this, etInputStringOffline);
                firstLangDetect();
                break;
            default:
                Log.i(TAG, "Default");
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_lrs_od_tt));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void showProgress() {
        progressBarOffline.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBarOffline.setVisibility(View.GONE);
    }


    private void createAndSetSpinner() {
        spSourceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SP_SOURCE_LIST_EN);
        spDestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SP_DEST_LIST_EN);

        spSourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSourceTypeOffline.setAdapter(spSourceAdapter);

        spDestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDestTypeOffline.setAdapter(spDestAdapter);
        // set default destination lang to Turkish
        spDestTypeOffline.setSelection(10);

        spSourceTypeOffline.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        spDestTypeOffline.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        String inputStr = tvOutputStringOffline.getText().toString();
        String outputStr = etInputStringOffline.getText().toString();
        updateInputText(inputStr);
        updateOutputText(outputStr);

    }

    private void updateSourceLanguage(String code) {
        int count = spSourceAdapter.getCount();
        for (int i = 0; i < count; i++) {
            if (getLanguageName(code).equalsIgnoreCase(spSourceAdapter.getItem(i))) {
                spSourceTypeOffline.setSelection(i, true);
                return;
            }
        }
        spSourceTypeOffline.setSelection(0, true);
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
                spDestTypeOffline.setSelection(i, true);
                return;
            }
        }
        spDestTypeOffline.setSelection(0, true);
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
            etInputStringOffline.setText(text);
            updateLength(tvInputLangOffline, text.length());
        });
    }

    private void updateOutputText(final String text) {
        if (text == null || text.isEmpty()) {
            Log.w(TAG, "updateOutputText: text is empty");
            return;
        }

        runOnUiThread(() -> {
            displaySuccessAnalyseResults(text);
            updateLength(tvOutputLenOffline, text.length());
        });

        hideProgress();
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    /* ------------------------------------------------------------------------------------------ */
    //region Language Package Download Operations

    /**
     * @param languageCode
     * @param whichLangDownload : SourceLangPackage = 1, DestLangPackage  = 2;
     */
    private void downloadModelLanguagePackage(final String languageCode, final int whichLangDownload) {

        MLLocalTranslatorModel model = new MLLocalTranslatorModel.Factory(languageCode).create();

        MLModelDownloadListener modelDownloadListener = (alreadyDownLength, totalLength) ->
                showDownloadProcessOnButton(
                        alreadyDownLength,
                        totalLength,
                        whichLangDownload
                );

        // Method 1:
        MLModelDownloadStrategy request = new MLModelDownloadStrategy.Factory()
                .needWifi() // It is recommended that you download the package in a Wi-Fi environment.
                //.needCharging()
                //.needDeviceIdle()
                .create();

        showProgress();
        String whichLang = getEnLanguageName(languageCode);
        Utils.showToastMessage(getApplicationContext(), R.string.language_model + whichLang + " will be download. Please wait a few second.");

        mlLocalModelManager.downloadModel(model, request, modelDownloadListener)
                .addOnSuccessListener(aVoid -> {
                    hideProgress();
                    Utils.showToastMessage(getApplicationContext(), "DownloadModel Success.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure : DownloadModel failed : " + e.getMessage(), e);
                    displayFailureAnalyseResults("DownloadModel onFailure : " + e.getMessage());
                });


        // Method 2:
        /**

         MLModelDownloadStrategy downloadStrategy = new MLModelDownloadStrategy.Factory()
         .needWifi() // It is recommended that you download the package in a Wi-Fi environment.
         .create();

         // Create an offline translator.
         MLLocalTranslateSetting setting = new MLLocalTranslateSetting.Factory()
         // Set the source language code. The ISO 639-1 standard is used. This parameter is mandatory. If this parameter is not set, an error may occur.
         .setSourceLangCode(languageCode)
         // Set the target language code. The ISO 639-1 standard is used. This parameter is mandatory. If this parameter is not set, an error may occur.
         .setTargetLangCode("tr")
         .create();
         final MLLocalTranslator mlLocalTranslator = MLTranslatorFactory.getInstance().getLocalTranslator(setting);

         mlLocalTranslator.preparedModel(downloadStrategy).
         addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        // Called when the model package is successfully downloaded.
        public void onSuccess (Void aVoid){
        Log.i(TAG, "translate download model success");
        }
        }).addOnFailureListener(new OnFailureListener() {
        @Override public void onFailure (Exception e){
        // Called when the model package fails to be downloaded.
        Log.e(TAG, "onFailure : preparedModel failed : " + e.getMessage() , e);
        displayFailureAnalyseResults("preparedModel onFailure : "  + e.getMessage());
        }
        });

         */
    }

    /**
     * @param alreadyDownLength
     * @param totalLength
     * @param whichLangDownload : SourceLangPackage = 1, DestLangPackage  = 2;
     */
    private void showDownloadProcessOnButton(long alreadyDownLength, long totalLength, int whichLangDownload) {
        double downDone = alreadyDownLength * 1.0 / M;
        double downTotal = totalLength * 1.0 / M;
        String downD = String.format("%.2f", downDone);
        String downT = String.format("%.2f", downTotal);

        String text = downD + "M" + "/" + downT + "M";
        Log.e(TAG, "stringformat:" + downD);

        updateButton(text, whichLangDownload);

        if (downD.equals(downT)) {
            updateButton("Download", whichLangDownload);
        }
    }

    /**
     * @param buttonText
     * @param whichLangDownload : SourceLangPackage = 1, DestLangPackage  = 2;
     */
    private void updateButton(final String buttonText, final int whichLangDownload) {
        runOnUiThread(() -> {
            try {
                switch (whichLangDownload) {
                    case SOURCE_LANG_PACKAGE:
                        btnDownloadSourceLang.setText(buttonText);
                        break;
                    case DEST_LANG_PACKAGE:
                        btnDownloadDestLang.setText(buttonText);
                        break;
                    default:
                        Log.i(TAG, "Default");
                }
            } catch (Exception e) {
                Log.e(TAG, "updateButton exc : " + e.getMessage(), e);
            }
        });
    }

    //endregion
    /* ------------------------------------------------------------------------------------------ */

    /* ------------------------------------------------------------------------------------------ */
    //region Translation Operations

    final CountDownLatch latch = new CountDownLatch(1);

    private void doTranslate() {
        showProgress();
        // Translating, get data, and update output boxes.
        String sourceText = getInputText();
        String sourceLang = getSourceType();
        String targetLang = getDestType();

        // Auto detect language
        if (sourceLang.equalsIgnoreCase("AUTO")) {
            detectLanguage(sourceText, latch);
        } else {
            latch.countDown();
        }

        new Thread(() -> {
            try {
                latch.await();
            } catch (Exception e) {
                Log.e(TAG, "doTranslate : latch.await() Exception : " + e.getMessage(), e);
            }
            String sourceLanguage = sourceLang;
            if (firstBestDetectLangResult != null) {
                sourceLanguage = firstBestDetectLangResult.toUpperCase(Locale.ENGLISH);
                firstBestDetectLangResult = null;
            }

            // Create local translator
            MLLocalTranslateSetting setting = new MLLocalTranslateSetting.Factory()
                    .setSourceLangCode(sourceLanguage)
                    .setTargetLangCode(targetLang)
                    .create();
            final MLLocalTranslator translator = MLTranslatorFactory.getInstance().getLocalTranslator(setting);

            translateImpl(translator, sourceText);
        }).start();

    }

    private String firstBestDetectLangResult;

    /**
     * Create local language detector
     *
     * @param input
     * @param latch : CountDownLatch
     */
    private void detectLanguage(final String input, final CountDownLatch latch) {
        MLLangDetectorFactory factory = MLLangDetectorFactory.getInstance();
        MLLocalLangDetectorSetting setting = new MLLocalLangDetectorSetting.Factory().setTrustedThreshold(0.01f).create();
        MLLocalLangDetector localLangDetector = factory.getLocalLangDetector(setting);

        localLangDetector.firstBestDetect(input)
                .addOnSuccessListener(s -> {
                    Log.i(TAG, "firstBestDetect detectLanguage : " + s);
                    latch.countDown();
                    firstBestDetectLangResult = s;
                    updateSourceLanguage(firstBestDetectLangResult);
                    localLangDetector.stop();
                }).addOnFailureListener(e -> {
            Log.e(TAG, "LocalLangDetector firstBestDetect failed : " + e.getMessage(), e);
            displayFailureAnalyseResults("LocalLangDetector firstBestDetect failed : " + e.getMessage());
        });
    }


    /**
     * @param translator
     * @param input
     */
    private void translateImpl(final MLLocalTranslator translator, String input) {
        final long startTime = System.currentTimeMillis();
        translator.asyncTranslate(input)
                .addOnSuccessListener(s -> {
                    Log.d(TAG, "MLLocalTranslator asyncTranslate success : " + s);
                    long endTime = System.currentTimeMillis();
                    updateTime(endTime - startTime);
                    updateOutputText(s);
                    translator.stop();
                }).addOnFailureListener(e -> {
            Log.e(TAG, "MLLocalTranslator asyncTranslate failed : " + e.getMessage(), e);
            displayFailureAnalyseResults("LocalTranslator asyncTranslate failed : " + e.getMessage());
        });
    }


    private String getInputText() {
        return etInputStringOffline.getText().toString();
    }

    private String getSourceType() {
        return srcLanguage;
    }

    private String getDestType() {
        return dstLanguage;
    }

    private void updateTime(long time) {
        tvTimeOffline.setText(getResources().getString(R.string.ttr_elapsed_time) + time + " ms");
    }


    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    /* ------------------------------------------------------------------------------------------ */
    //region Display Result Operations


    /**
     * first best guess of language with MLLocalLangDetector
     */
    private void firstLangDetect() {
        String input = getInputText();
        MLLangDetectorFactory factory = MLLangDetectorFactory.getInstance();
        MLLocalLangDetectorSetting setting = new MLLocalLangDetectorSetting.Factory().setTrustedThreshold(0.01f).create();
        final MLLocalLangDetector localLangDetector = factory.getLocalLangDetector(setting);
        bestLangDetectImpl(localLangDetector, input);
    }

    /**
     * language detector listener of MLLocalLangDetector with firstBestDetect
     *
     * @param detector
     * @param input
     */
    private void bestLangDetectImpl(final MLLocalLangDetector detector, String input) {
        final long startTime = System.currentTimeMillis();
        detector.firstBestDetect(input)
                .addOnSuccessListener(s -> {
                    Log.i(TAG, "BestLangDetect success: " + s);
                    String result = "Language=" + getEnLanguageName(s);
                    long endTime = System.currentTimeMillis();
                    updateTime(endTime - startTime);
                    updateOutputText(result);
                    detector.stop();
                }).addOnFailureListener(e -> {
            Log.e(TAG, "Detector.firstBestDetect onFailure :: " + e.getMessage(), e);
            Utils.showToastMessage(getApplicationContext(), "Detector.firstBestDetect onFailure : " + e.getMessage());
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


    /* ------------------------------------------------------------------------------------------ */
    //region Display Result Operations

    private void displaySuccessAnalyseResults(String text) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        tvOutputStringOffline.setText(text);
        Log.i(TAG, "Success Translation Results : " + text);
        resultLogs.setText("Success Translation Results : with " + text.length() + " characters :\n" + text);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Failure Translation Results : " + msg);
        resultLogs.setText("Failure Translation Results : \n" + msg);
        tvOutputStringOffline.setText("");
        Utils.showToastMessage(getApplicationContext(), "Translation Process was Failed : " + msg);
    }

    private void updateLength(TextView view, int length) {
        view.setText(String.format(Locale.ENGLISH, "%d words", length));
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
    }

}