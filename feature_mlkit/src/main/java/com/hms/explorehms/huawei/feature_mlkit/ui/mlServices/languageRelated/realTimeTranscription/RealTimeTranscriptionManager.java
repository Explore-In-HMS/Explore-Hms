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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.realTimeTranscription;

import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscription;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscriptionConfig;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscriptionConstants;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscriptionListener;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscriptionResult;

import java.util.ArrayList;

public class RealTimeTranscriptionManager {

    private static final String TAG = RealTimeTranscriptionManager.class.getSimpleName();

    public static final int RESULT_FINAL = 2;
    public static final int RESULT_RECEVING = 3;

    private boolean mlAsrLongRecognizerIsListening;
    private onResultsReady mListener;
    private final ArrayList<String> mResultsList = new ArrayList<>();

    private MLSpeechRealTimeTranscription mlAsrLongRecognizer;

    private final String language;

    /**
     * Real-time transcription enables your app to convert long speech (no longer than 5 hours) into text in real time.
     * The generated text contains punctuation and timestamps.
     * Currently, Mandarin Chinese, English, and French can be recognized, including Chinese-English bilingual speech.
     * <p>
     * Real-time transcription depends on the on-cloud API for speech recognition.
     * During commissioning and usage, ensure that the device can access the Internet.
     */
    public RealTimeTranscriptionManager(String mLanguage, onResultsReady listener) {
        language = mLanguage;
        try {
            mListener = listener;
        } catch (ClassCastException e) {
            Log.e(TAG, e.toString());
        }
        new Thread(this::startRealTimeTranscriptionRecognizer).start();
    }

    private void startRealTimeTranscriptionRecognizer() {
        MLSpeechRealTimeTranscriptionConfig config = new MLSpeechRealTimeTranscriptionConfig.Factory()
                .setLanguage(language)
                .enablePunctuation(true)
                .enableSentenceTimeOffset(true)
                .enableWordTimeOffset(true)
                .create();
        MLSpeechRealTimeTranscription.getInstance().setRealTimeTranscriptionListener(new SpeechRecognitionListener());
        MLSpeechRealTimeTranscription.getInstance().startRecognizing(config);
    }

    public void destroy() {
        if (mlAsrLongRecognizer != null) {
            mlAsrLongRecognizer.destroy();
            mlAsrLongRecognizer = null;
        }
    }

    protected class SpeechRecognitionListener implements MLSpeechRealTimeTranscriptionListener {
        @Override
        public void onStartListening() {
            Log.d(TAG, "SpeechRecognitionListener onStartListening");
        }

        @Override
        public void onStartingOfSpeech() {
            Log.d(TAG, "SpeechRecognitionListener onStartingOfSpeech");
        }

        @Override
        public void onVoiceDataReceived(byte[] data, float energy, Bundle bundle) {
            int length = data == null ? 0 : data.length;
            Log.d(TAG, "SpeechRecognitionListener onVoiceDataReceived data.length=" + length);
        }

        @Override
        public void onRecognizingResults(Bundle partialResults) {
            if (partialResults != null && mListener != null) {
                mResultsList.clear();
                mResultsList.add(partialResults.getString(MLSpeechRealTimeTranscriptionConstants.RESULTS_RECOGNIZING));
                boolean isFinal = partialResults.getBoolean(MLSpeechRealTimeTranscriptionConstants.RESULTS_PARTIALFINAL);
                if (isFinal) {
                    String result = partialResults.getString(MLSpeechRealTimeTranscriptionConstants.RESULTS_RECOGNIZING);
                    Log.d(TAG, "SpeechRecognitionListener onRecognizingResults is " + result);
                    mListener.onRecognizingResults(mResultsList, RESULT_FINAL);

                    ArrayList<MLSpeechRealTimeTranscriptionResult> wordOffset = partialResults.getParcelableArrayList(MLSpeechRealTimeTranscriptionConstants.RESULTS_WORD_OFFSET);
                    ArrayList<MLSpeechRealTimeTranscriptionResult> sentenceOffset = partialResults.getParcelableArrayList(MLSpeechRealTimeTranscriptionConstants.RESULTS_SENTENCE_OFFSET);

                    if (wordOffset != null) {
                        for (int i = 0; i < wordOffset.size(); i++) {
                            MLSpeechRealTimeTranscriptionResult remoteResult = wordOffset.get(i);
                            Log.d(TAG, "SpeechRecognitionListener onRecognizingResults word offset is " + i + " ---> " + remoteResult.toString());
                        }
                    }

                    if (sentenceOffset != null) {
                        for (int i = 0; i < sentenceOffset.size(); i++) {
                            MLSpeechRealTimeTranscriptionResult remoteResult = sentenceOffset.get(i);
                            Log.d(TAG, "SpeechRecognitionListener onRecognizingResults sentence offset is " + i + " ---> " + remoteResult.toString());
                        }
                    }
                } else {
                    if (!mResultsList.isEmpty()) {
                        mListener.onRecognizingResults(mResultsList, RESULT_RECEVING);
                    } else {
                        Log.e(TAG, "SpeechRecognitionListener onRecognizingResults mResultsList is Empty!");
                    }
                }
            }
        }

        @Override
        public void onError(int error, String errorMessage) {
            Log.e(TAG, "SpeechRecognitionListener onError errorCode : " + error + " errorMessage : " + errorMessage);
            // If this parameter is not added,
            // the system does not respond after the network is disconnected and the recording is performed again.
            mlAsrLongRecognizerIsListening = false;
            if (mListener != null) {
                mListener.onError(error);
            }
        }

        @Override
        public void onState(int state, Bundle params) {
            Log.e(TAG, "SpeechRecognitionListener onState is " + state);
            if (state == MLSpeechRealTimeTranscriptionConstants.STATE_SERVICE_RECONNECTING) { // webSocket Reconnecting
                Log.e(TAG, "SpeechRecognitionListener onState webSocket reconnect ");
            } else if (state == MLSpeechRealTimeTranscriptionConstants.STATE_SERVICE_RECONNECTED) { // webSocket Reconnection succeeded.
                Log.e(TAG, "SpeechRecognitionListener onState webSocket reconnect success ");
            }
        }
    }

    public boolean isMlAsrLongRecognizerIsListening() {
        return mlAsrLongRecognizerIsListening;
    }

    public interface onResultsReady {
        void onRecognizingResults(ArrayList<String> results, int status);

        void onError(int error);
    }
}
