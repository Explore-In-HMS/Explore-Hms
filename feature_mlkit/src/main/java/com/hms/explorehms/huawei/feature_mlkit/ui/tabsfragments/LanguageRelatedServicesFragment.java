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
package com.hms.explorehms.huawei.feature_mlkit.ui.tabsfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.audioTranscription.AudioFileTranscriptionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.realTimeTranscription.RealTimeTranscriptionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.soundDetection.SoundDetectionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.speechRecognition.AutomaticSpeechRecognitionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.textToSpeech.TextToSpeechActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.textTranslation.TextTranslationOfflineActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.textTranslation.TextTranslationOnlineActivity;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LanguageRelatedServicesFragment extends Fragment {


    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.imageView_vcc)
    ImageView ivVcc;

    //endregion

    public LanguageRelatedServicesFragment() {
        // Required empty public constructor
    }

    public static LanguageRelatedServicesFragment newInstance() {
        return new LanguageRelatedServicesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_related_services, container, false);

        unbinder = ButterKnife.bind(this, view);

        Utils.setItemColorUnavailable(ivVcc);

        return view;
    }


    @OnClick({R.id.cv_lang_related_trn, R.id.cv_lang_related_asr,
            R.id.cv_lang_related_tts, R.id.cv_lang_related_aft,
            R.id.cv_lang_related_rtt, R.id.cv_lang_related_sdtc, R.id.cv_lang_related_vcc})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_lang_related_trn:
                DialogUtils.showTranslateTypeDialog(
                        requireActivity(),
                        "Select Translate Type",
                        "Select Your Want Translate Type by Offline or Online",
                        R.drawable.icon_select_type,
                        "Online",
                        "Offline",
                        TextTranslationOnlineActivity.class,
                        TextTranslationOfflineActivity.class);
                break;
            case R.id.cv_lang_related_asr:
                Utils.startActivity(requireActivity(), AutomaticSpeechRecognitionActivity.class);
                break;
            case R.id.cv_lang_related_tts:
                Utils.startActivity(requireActivity(), TextToSpeechActivity.class);
                break;
            case R.id.cv_lang_related_aft:
                Utils.startActivity(requireActivity(), AudioFileTranscriptionActivity.class);
                break;
            case R.id.cv_lang_related_rtt:
                Utils.startActivity(requireActivity(), RealTimeTranscriptionActivity.class);
                break;
            case R.id.cv_lang_related_sdtc:
                Utils.startActivity(requireActivity(), SoundDetectionActivity.class);
                break;
            case R.id.cv_lang_related_vcc:
                Utils.showToastMessage(v.getContext(), "Video Course Creator is Not Available to Use For Now");
                Utils.openWebPage( requireActivity(), getResources().getString(R.string.link_lrs_vcc));
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}