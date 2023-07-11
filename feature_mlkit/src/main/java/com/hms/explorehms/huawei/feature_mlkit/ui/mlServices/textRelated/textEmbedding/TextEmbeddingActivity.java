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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.textEmbedding;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.audioTranscription.AudioFileTranscriptionActivity;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.textembedding.MLTextEmbeddingAnalyzer;
import com.huawei.hms.mlsdk.textembedding.MLTextEmbeddingAnalyzerFactory;
import com.huawei.hms.mlsdk.textembedding.MLTextEmbeddingException;
import com.huawei.hms.mlsdk.textembedding.MLTextEmbeddingSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TextEmbeddingActivity extends AppCompatActivity {


    //region variablesAndObjects
    private static final String TAG = AudioFileTranscriptionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private MLTextEmbeddingAnalyzer analyzer;


    @Nullable
    @BindView(R.id.spinnerEmbeddingApiType)
    Spinner spinnerEmbeddingApiType;

    @Nullable
    @BindView(R.id.spinnerEmbeddingLanguage)
    Spinner spinnerEmbeddingLanguage;


    @Nullable
    @BindView(R.id.clDicVerInfo)
    ConstraintLayout clDicVerInfo;

    @Nullable
    @BindView(R.id.clWordVector)
    ConstraintLayout clWordVector;
    @Nullable
    @BindView(R.id.etWordVector)
    EditText etWordVector;


    @Nullable
    @BindView(R.id.clBatchWordsVector)
    ConstraintLayout clBatchWordsVector;
    @Nullable
    @BindView(R.id.etBatchWordVector)
    EditText etBatchWordVector;

    @Nullable
    @BindView(R.id.clSentenceVector)
    ConstraintLayout clSentenceVector;
    @Nullable
    @BindView(R.id.etSentenceVector)
    EditText etSentenceVector;


    @Nullable
    @BindView(R.id.clWordSimilarity)
    ConstraintLayout clWordSimilarity;
    @Nullable
    @BindView(R.id.etWordSimilarity1)
    EditText etWordSimilarity1;
    @Nullable
    @BindView(R.id.etWordSimilarity2)
    EditText etWordSimilarity2;

    @Nullable
    @BindView(R.id.clSentenceSimilarity)
    ConstraintLayout clSentenceSimilarity;
    @Nullable
    @BindView(R.id.etSentenceSimilarity1)
    EditText etSentenceSimilarity1;
    @Nullable
    @BindView(R.id.etSentenceSimilarity2)
    EditText etSentenceSimilarity2;

    @Nullable
    @BindView(R.id.clSimilarWords)
    ConstraintLayout clSimilarWords;
    @Nullable
    @BindView(R.id.etSimilarWords)
    EditText etSimilarWords;
    @Nullable
    @BindView(R.id.etSimilarWordQuantity)
    EditText etSimilarWordQuantity;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    //endregion views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_text_embedding);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        Utils.setApiKeyForRemoteMLApplication(this);

        createTextEmbeddingAnalyzer(MLTextEmbeddingSetting.LANGUAGE_EN);

        createAndSetSpinnerListeners();

    }

    @OnClick({R.id.searchDictionaryVer, R.id.searchWordVector, R.id.deleteWordVector,
            R.id.searchBatchWordVector, R.id.deleteBatchWordVector,
            R.id.searchSentenceVector, R.id.deleteSentenceVector,
            R.id.searchWordSimilarity, R.id.deleteWordSimilarity,
            R.id.searchSentenceSimilarity, R.id.deleteSentenceSimilarity,
            R.id.searchSimilarWords, R.id.deleteSimilarWords})
    public void onItemClick(View v) {
        resultLogs.setText(getResources().getString(R.string.embedding_result_descriptions_will_be_here));
        switch (v.getId()) {
            case R.id.searchDictionaryVer:
                getDictionaryVersionInformation();
                break;
            case R.id.searchWordVector:
                doSearchWordVector();
                break;
            case R.id.deleteWordVector:
                clearView(etWordVector);
                break;
            case R.id.searchBatchWordVector:
                doSearchBatchWordsVector();
                break;
            case R.id.deleteBatchWordVector:
                clearView(etBatchWordVector);
                break;
            case R.id.searchSentenceVector:
                doSearchSentenceVector();
                break;
            case R.id.deleteSentenceVector:
                clearView(etSentenceVector);
                break;
            case R.id.searchWordSimilarity:
                doSearchWordSimilarity();
                break;
            case R.id.deleteWordSimilarity:
                clearView(etWordSimilarity1);
                clearView(etWordSimilarity2);
                break;
            case R.id.searchSentenceSimilarity:
                doSearchSentenceSimilarity();
                break;
            case R.id.deleteSentenceSimilarity:
                clearView(etSentenceSimilarity1);
                clearView(etSentenceSimilarity2);
                break;
            case R.id.searchSimilarWords:
                doSearchSimilarWords();
                break;
            case R.id.deleteSimilarWords:
                clearView(etSimilarWords);
                clearView(etSimilarWordQuantity);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_trs_temb));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Nothing is necessary to implement.
    }


    /**
     * @param language : such as MLTextEmbeddingSetting.LANGUAGE_EN = 'en'
     */
    private void createTextEmbeddingAnalyzer(String language) {
        MLTextEmbeddingSetting setting = new MLTextEmbeddingSetting.Factory()
                .setLanguage(language)
                .create();
        analyzer = MLTextEmbeddingAnalyzerFactory.getInstance().getMLTextEmbeddingAnalyzer(setting);
    }

    private void createAndSetSpinnerListeners() {
        spinnerEmbeddingApiType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "spinnerEmbeddingApiType Selected : position " + position + " item " + spinnerEmbeddingApiType.getSelectedItem().toString());
                editLayoutVisibilities(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This method will be trigger when nothing is selected.
            }
        });

        spinnerEmbeddingLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) spinnerEmbeddingLanguage.getSelectedItem();
                String mLanguage = MLTextEmbeddingSetting.LANGUAGE_EN;
                if (str.equals("English")) {
                    mLanguage = MLTextEmbeddingSetting.LANGUAGE_EN;
                } else if (str.equals("Chinese")) {
                    mLanguage = MLTextEmbeddingSetting.LANGUAGE_ZH;
                }
                createTextEmbeddingAnalyzer(mLanguage);

                Log.i(TAG, "spinnerEmbeddingLanguage Selected : " + mLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This method will be trigger when nothing is selected.
            }
        });
    }


    private void getDictionaryVersionInformation() {
        if (Utils.haveNetworkConnection(this)) {
            analyzer.getVocabularyVersion()
                    .addOnSuccessListener(dictionaryVersionVo -> {
                        String result = getString(R.string.dic_versions) + dictionaryVersionVo.getVersionNo() + "\n"
                                + getString(R.string.dic_dimension) + dictionaryVersionVo.getDictionaryDimension() + "\n"
                                + getString(R.string.dic_versions) + dictionaryVersionVo.getDictionarySize();
                        displaySuccessAnalyseResults(getString(R.string.t_embed_ver), result);
                    })
                    .addOnFailureListener(e -> {
                        String errorMessage = e.getMessage();
                        try {
                            int errorCode = ((MLTextEmbeddingException) e).getErrCode();
                            String errorMsg = e.getMessage();
                            errorMessage = getString(R.string.error) + errorCode + " : " + errorMsg;
                        } catch (Exception ex) {
                            Log.e(TAG, "getDictionaryVersionInformation.onFailure (MLTextEmbeddingException) exception : " + ex.getMessage(), ex);
                        }
                        Log.e(TAG, "getDictionaryVersionInformation.onFailure errorMessage : " + errorMessage, e);
                        displayFailureAnalyseResults(getString(R.string.t_embed_ver), errorMessage);
                    });
        } else {
            callDialog("You can not get Dictionary Version Information without Network!");
        }
    }

    private void doSearchWordVector() {
        String input = etWordVector.getText().toString();
        if (Utils.haveNetworkConnection(this)) {
            if (!input.isEmpty()) {
                analyzer.analyseWordVector(input)
                        .addOnSuccessListener(wordVector -> {
                            try {
                                JSONArray jsonObject = new JSONArray(wordVector);
                                String result = getString(R.string.te_word_vector) + "\n" + jsonObject.toString();

                                displaySuccessAnalyseResults(getString(R.string.t_embed_w_v), result);

                            } catch (JSONException e) {
                                Log.e(TAG, "doSearchWordVector JSONException : " + e.getMessage(), e);
                                displayFailureAnalyseResults(getString(R.string.t_embed_w_v), "JSONException : " + e.getMessage());
                            }
                        })
                        .addOnFailureListener(e -> {
                            String errorMessage = e.getMessage();
                            try {
                                int errorCode = ((MLTextEmbeddingException) e).getErrCode();
                                String errorMsg = e.getMessage();
                                errorMessage = getString(R.string.error) + errorCode + " : " + errorMsg;
                            } catch (Exception ex) {
                                Log.e(TAG, "doSearchWordVector.onFailure (MLTextEmbeddingException) exception : " + ex.getMessage(), ex);
                            }
                            Log.e(TAG, "doSearchWordVector.onFailure errorMessage : " + errorMessage, e);
                            displayFailureAnalyseResults(getString(R.string.t_embed_w_v), errorMessage);
                        });
            } else {
                Utils.showToastMessage(getApplicationContext(), getString(R.string.hint_word_vector));
            }
        } else {
            callDialog("You can not Search Word Vector without Network!");
        }
    }

    private void doSearchBatchWordsVector() {
        String input = etBatchWordVector.getText().toString();
        if (Utils.haveNetworkConnection(this)) {
            if (!input.isEmpty()) {

                Set<String> inputSet = new HashSet<>();
                List<String> stringList = Arrays.asList(input.split(","));
                inputSet.addAll(stringList);

                analyzer.analyseWordVectorBatch(inputSet)
                        .addOnSuccessListener(wordsVector -> {

                            JSONObject jsonObject = new JSONObject(wordsVector);
                            String result = getString(R.string.te_words_vector) + "\n" + jsonObject.toString();

                            displaySuccessAnalyseResults(getString(R.string.t_embed_b_w_v), result);
                        })
                        .addOnFailureListener(e -> {
                            String errorMessage = e.getMessage();
                            try {
                                int errorCode = ((MLTextEmbeddingException) e).getErrCode();
                                String errorMsg = e.getMessage();
                                errorMessage = getString(R.string.error) + errorCode + " : " + errorMsg;
                            } catch (Exception ex) {
                                Log.e(TAG, "doSearchBatchWordsVector.onFailure (MLTextEmbeddingException) exception : " + ex.getMessage(), ex);
                            }
                            Log.e(TAG, "doSearchBatchWordsVector.onFailure errorMessage : " + errorMessage, e);
                            displayFailureAnalyseResults(getString(R.string.t_embed_b_w_v), errorMessage);
                        });

            } else {
                Utils.showToastMessage(getApplicationContext(), getString(R.string.hint_batch_word_vector));
            }
        } else {
            callDialog("You can not Search Batch Words Vector without Network!");
        }
    }

    private void doSearchSentenceVector() {
        String input = etSentenceVector.getText().toString();
        if (Utils.haveNetworkConnection(this)) {
            if (!input.isEmpty()) {
                analyzer.analyseSentenceVector(input)
                        .addOnSuccessListener(sentenceVector -> {
                            try {
                                JSONArray jsonObject = new JSONArray(sentenceVector);
                                String result = getString(R.string.te_sentence_vector) + "\n" + jsonObject.toString();

                                displaySuccessAnalyseResults(getString(R.string.t_embed_s_v), result);

                            } catch (JSONException e) {
                                Log.e(TAG, "doSearchSentenceVector JSONException : " + e.getMessage(), e);
                                displayFailureAnalyseResults(getString(R.string.t_embed_s_v), "JSONException : " + e.getMessage());
                            }
                        })
                        .addOnFailureListener(e -> {
                            String errorMessage = e.getMessage();
                            try {
                                int errorCode = ((MLTextEmbeddingException) e).getErrCode();
                                String errorMsg = e.getMessage();
                                errorMessage = getString(R.string.error) + errorCode + " : " + errorMsg;
                            } catch (Exception ex) {
                                Log.e(TAG, "doSearchSentenceVector.onFailure (MLTextEmbeddingException) exception : " + ex.getMessage(), ex);
                            }
                            Log.e(TAG, "doSearchSentenceVector.onFailure errorMessage : " + errorMessage, e);
                            displayFailureAnalyseResults(getString(R.string.t_embed_s_v), errorMessage);
                        });

            } else {
                Utils.showToastMessage(getApplicationContext(), getString(R.string.hint_sentence_vector));
            }
        } else {
            callDialog("You can not Search Sentence Vector without Network!");
        }
    }

    private void doSearchWordSimilarity() {
        String input1 = etWordSimilarity1.getText().toString();
        String input2 = etWordSimilarity2.getText().toString();
        if (Utils.haveNetworkConnection(this)) {
            if (!input1.isEmpty() || !input2.isEmpty()) {
                analyzer.analyseWordsSimilarity(input1, input2)
                        .addOnSuccessListener(wordsSimilarity -> {
                            String result = getString(R.string.te_word_similarity) + "\n" + wordsSimilarity.toString();
                            displaySuccessAnalyseResults(getString(R.string.t_embed_w_sim), result);
                        })
                        .addOnFailureListener(e -> {
                            String errorMessage = e.getMessage();
                            try {
                                int errorCode = ((MLTextEmbeddingException) e).getErrCode();
                                String errorMsg = e.getMessage();
                                errorMessage = getString(R.string.error) + errorCode + " : " + errorMsg;
                            } catch (Exception ex) {
                                Log.e(TAG, "doSearchWordSimilarity.onFailure (MLTextEmbeddingException) exception : " + ex.getMessage(), ex);
                            }
                            Log.e(TAG, "doSearchWordSimilarity.onFailure errorMessage : " + errorMessage, e);
                            displayFailureAnalyseResults(getString(R.string.t_embed_w_sim), errorMessage);
                        });

            } else {
                Utils.showToastMessage(getApplicationContext(), getString(R.string.hint_word_similarity));
            }
        } else {
            callDialog("You can not Search Word Similarity without Network!");
        }
    }

    private void doSearchSentenceSimilarity() {
        String input1 = etSentenceSimilarity1.getText().toString();
        String input2 = etSentenceSimilarity2.getText().toString();
        if (Utils.haveNetworkConnection(this)) {
            if (!input1.isEmpty() || !input2.isEmpty()) {
                analyzer.analyseSentencesSimilarity(input1, input2)
                        .addOnSuccessListener(sentencesSimilarity -> {
                            String result = getString(R.string.te_sentence_similarity) + "\n" + sentencesSimilarity.toString();
                            displaySuccessAnalyseResults(getString(R.string.t_embed_s_sim), result);
                        })
                        .addOnFailureListener(e -> {
                            String errorMessage = e.getMessage();
                            try {
                                int errorCode = ((MLTextEmbeddingException) e).getErrCode();
                                String errorMsg = e.getMessage();
                                errorMessage = getString(R.string.error) + errorCode + " : " + errorMsg;
                            } catch (Exception ex) {
                                Log.e(TAG, "doSearchSentenceSimilarity.onFailure (MLTextEmbeddingException) exception : " + ex.getMessage(), ex);
                            }
                            Log.e(TAG, "doSearchSentenceSimilarity.onFailure errorMessage : " + errorMessage, e);
                            displayFailureAnalyseResults(getString(R.string.t_embed_s_sim), errorMessage);
                        });

            } else {
                Utils.showToastMessage(getApplicationContext(), getString(R.string.hint_sentence_similarity));
            }
        } else {
            callDialog("You can not Search Sentence Similarity without Network!");
        }
    }

    private void doSearchSimilarWords() {
        String input = etSimilarWords.getText().toString();
        String count = etSimilarWordQuantity.getText().toString();
        if (Utils.haveNetworkConnection(this)) {
            if (!input.isEmpty() || !count.isEmpty()) {
                analyzer.analyseSimilarWords(input, Integer.parseInt(count))
                        .addOnSuccessListener(words -> {
                            JSONArray jsonObject = new JSONArray(words);
                            String result = getString(R.string.te_words_vector) + "\n" + jsonObject.toString();
                            displaySuccessAnalyseResults(getString(R.string.t_embed_sim_w), result);
                        })
                        .addOnFailureListener(e -> {
                            String errorMessage = e.getMessage();
                            try {
                                int errorCode = ((MLTextEmbeddingException) e).getErrCode();
                                String errorMsg = e.getMessage();
                                errorMessage = getString(R.string.error) + errorCode + " : " + errorMsg;
                            } catch (Exception ex) {
                                Log.e(TAG, "doSearchSimilarWords.onFailure (MLTextEmbeddingException) exception : " + ex.getMessage(), ex);
                            }
                            Log.e(TAG, "doSearchSimilarWords.onFailure errorMessage : " + errorMessage, e);
                            displayFailureAnalyseResults(getString(R.string.t_embed_sim_w), errorMessage);
                        });

            } else {
                Utils.showToastMessage(getApplicationContext(), getString(R.string.hint_similar_words));
            }
        } else {
            callDialog("You can not Search Similar Words without Network!");
        }
    }


    private void displaySuccessAnalyseResults(String title, String msg) {
        Utils.createVibration(getApplicationContext(), 200);
        resultLogs.setText(title + " Success Results : \n" + msg);
        Log.i(TAG, "displaySuccessAnalyseResults : " + title + " Success Results : \n" + msg);
    }

    private void displayFailureAnalyseResults(String title, String msg) {
        Utils.createVibration(getApplicationContext(), 400);
        resultLogs.setText(title + getString(R.string.failure_results) + msg);
        Log.e(TAG, "displayFailureAnalyseResults : " + title + getString(R.string.failure_results) + msg);
        Utils.showToastMessage(getApplicationContext(), title + getString(R.string.failure_results) + msg);
    }


    public void callDialog(String msg) {
        DialogUtils.showDialogNetworkWarning(this,
                "NEED NETWORK!",
                "Would You Like To Go To Settings To Open Network?",
                R.drawable.icon_settings,
                msg,
                "YES GO", "CANCEL");
    }

    private void clearView(EditText etView) {
        etView.setText(null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void editLayoutVisibilities(int order) {
        Log.i(TAG, "editLayoutVisibilities order  " + order);
        switch (order) {
            case 0:
                clDicVerInfo.setVisibility(View.VISIBLE);
                clWordVector.setVisibility(View.GONE);
                clBatchWordsVector.setVisibility(View.GONE);
                clSentenceVector.setVisibility(View.GONE);
                clWordSimilarity.setVisibility(View.GONE);
                clSentenceSimilarity.setVisibility(View.GONE);
                clSimilarWords.setVisibility(View.GONE);
                break;
            case 1:
                clDicVerInfo.setVisibility(View.GONE);
                clWordVector.setVisibility(View.VISIBLE);
                clBatchWordsVector.setVisibility(View.GONE);
                clSentenceVector.setVisibility(View.GONE);
                clWordSimilarity.setVisibility(View.GONE);
                clSentenceSimilarity.setVisibility(View.GONE);
                clSimilarWords.setVisibility(View.GONE);
                break;
            case 2:
                clDicVerInfo.setVisibility(View.GONE);
                clWordVector.setVisibility(View.GONE);
                clBatchWordsVector.setVisibility(View.VISIBLE);
                clSentenceVector.setVisibility(View.GONE);
                clWordSimilarity.setVisibility(View.GONE);
                clSentenceSimilarity.setVisibility(View.GONE);
                clSimilarWords.setVisibility(View.GONE);
                break;
            case 3:
                clDicVerInfo.setVisibility(View.GONE);
                clWordVector.setVisibility(View.GONE);
                clBatchWordsVector.setVisibility(View.GONE);
                clSentenceVector.setVisibility(View.VISIBLE);
                clWordSimilarity.setVisibility(View.GONE);
                clSentenceSimilarity.setVisibility(View.GONE);
                clSimilarWords.setVisibility(View.GONE);
                break;
            case 4:
                clDicVerInfo.setVisibility(View.GONE);
                clWordVector.setVisibility(View.GONE);
                clBatchWordsVector.setVisibility(View.GONE);
                clSentenceVector.setVisibility(View.GONE);
                clWordSimilarity.setVisibility(View.VISIBLE);
                clSentenceSimilarity.setVisibility(View.GONE);
                clSimilarWords.setVisibility(View.GONE);
                break;
            case 5:
                clDicVerInfo.setVisibility(View.GONE);
                clWordVector.setVisibility(View.GONE);
                clBatchWordsVector.setVisibility(View.GONE);
                clSentenceVector.setVisibility(View.GONE);
                clWordSimilarity.setVisibility(View.GONE);
                clSentenceSimilarity.setVisibility(View.VISIBLE);
                clSimilarWords.setVisibility(View.GONE);
                break;
            case 6:
                clDicVerInfo.setVisibility(View.GONE);
                clWordVector.setVisibility(View.GONE);
                clBatchWordsVector.setVisibility(View.GONE);
                clSentenceVector.setVisibility(View.GONE);
                clWordSimilarity.setVisibility(View.GONE);
                clSentenceSimilarity.setVisibility(View.GONE);
                clSimilarWords.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }


}