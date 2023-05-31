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

package com.hms.explorehms.huawei.feature_searchkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_searchkit.adapter.AutoSuggestionAdapter;
import com.hms.explorehms.huawei.feature_searchkit.adapter.SearchKitGeneralAdapter;
import com.hms.explorehms.huawei.feature_searchkit.bean.ProgressDialogScreen;
import com.hms.explorehms.huawei.feature_searchkit.bean.TokenResponse;
import com.hms.explorehms.huawei.feature_searchkit.databinding.ActivitySearchKitMainBinding;
import com.hms.explorehms.huawei.feature_searchkit.listeners.AutoSuggestClickListenerSearchKit;
import com.hms.explorehms.huawei.feature_searchkit.network.NetworkManager;
import com.hms.explorehms.huawei.feature_searchkit.network.QueryService;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.searchkit.SearchKitInstance;
import com.huawei.hms.searchkit.bean.AutoSuggestResponse;
import com.huawei.hms.searchkit.bean.BaseSearchResponse;
import com.huawei.hms.searchkit.bean.CommonSearchRequest;
import com.huawei.hms.searchkit.bean.ImageItem;
import com.huawei.hms.searchkit.bean.NewsItem;
import com.huawei.hms.searchkit.bean.SpellCheckResponse;
import com.huawei.hms.searchkit.bean.SuggestObject;
import com.huawei.hms.searchkit.bean.VideoItem;
import com.huawei.hms.searchkit.bean.WebItem;
import com.huawei.hms.searchkit.bean.WebSearchRequest;
import com.huawei.hms.searchkit.utils.Language;
import com.huawei.hms.searchkit.utils.Region;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


enum TabState {
    WEB,
    IMAGE,
    VIDEO,
    NEWS
}

public class SearchKitMainActivity extends AppCompatActivity implements AutoSuggestClickListenerSearchKit {

    public static final WebSearchRequest webSearchRequest = new WebSearchRequest();
    public static final CommonSearchRequest commonSearchRequest = new CommonSearchRequest();
    private static final String TAG = "SEARCH KIT";
    private static final String BASE_URL = "https://oauth-login.cloud.huawei.com/";
    private String CLIENT_ID_TOKEN = "";
    private String CLIENT_SECRET_TOKEN = "";
    private ActivitySearchKitMainBinding binding;
    private AutoSuggestionAdapter autoSuggestionAdapter;

    private String query = "";
    private TabState tabState = TabState.WEB;

    private ProgressDialogScreen progressDialogScreen;


    /**
     * ViewBinding process is done here.
     * <p>
     * Also we are initialize Search kit and some listeners.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchKitMainBinding.inflate(getLayoutInflater());
        CLIENT_ID_TOKEN = AGConnectServicesConfig.fromContext(this).getString("client/app_id");
        CLIENT_SECRET_TOKEN = AGConnectServicesConfig.fromContext(this).getString("client/client_secret");
        Log.i("SearchkitID", CLIENT_ID_TOKEN);
        Log.i("SearchkitSecret", CLIENT_SECRET_TOKEN);
        setContentView(binding.getRoot());
        setupToolbar();

        SearchKitInstance.enableLog();
        SearchKitInstance.init(this, CLIENT_ID_TOKEN);

        progressDialogScreen = new ProgressDialogScreen(this);


        initRetrofit();
        initAdapters();
        initListeners();
        initRequest();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_searchkit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initListeners() {
        binding.edtTextInputSearchkit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    binding.recyclerAutoSuggestion.setVisibility(View.VISIBLE);
                    makeSuggestion(charSequence.toString());
                } else {
                    autoSuggestionAdapter.clearData();
                    query = "";
                    binding.recyclerViewSearchkit.setVisibility(View.GONE);
                    binding.tvSpellingCheckResult.setText("");
                    binding.tvSpellingCheck.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged");
            }
        });

        binding.layTabsSearchKit.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        makeSearch();
                        tabState = TabState.WEB;
                        break;
                    case 1:
                        tabState = TabState.IMAGE;
                        makeSearch();
                        break;

                    case 2:
                        tabState = TabState.VIDEO;
                        makeSearch();
                        break;

                    case 3:
                        tabState = TabState.NEWS;
                        makeSearch();
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i(TAG, "onTabUnselected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i(TAG, "onTabReselected");
            }
        });

        binding.edtTextInputSearchkit.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH && !textView.getText().toString().isEmpty()) {
                getSpellCheck(textView.getText().toString());
                binding.recyclerAutoSuggestion.setVisibility(View.GONE);
                query = textView.getText().toString();
                makeSearch();
                hintSoftKeyboard();
                return true;
            }
            return false;
        });

        binding.spinnerWebsite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                webSearchRequest.setWithin(getResources().getStringArray(R.array.website_array)[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG, "onNothingSelectedSpinnerWebsite");
            }
        });

        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                webSearchRequest.setLang(Language.values()[i]);
                commonSearchRequest.setLang(Language.values()[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG, "onNothingSelectedSpinnerLanguage");
            }
        });

        binding.spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                webSearchRequest.setSregion(Region.values()[i]);
                commonSearchRequest.setSregion(Region.values()[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG, "onNothingSelectedSpinnerRegion");
            }
        });

        binding.tvSpellingCheckResult.setOnClickListener(view -> {
            if (!binding.tvSpellingCheckResult.getText().toString().isEmpty()) {
                query = binding.tvSpellingCheckResult.getText().toString();
                makeSearch();
                binding.edtTextInputSearchkit.setText(binding.tvSpellingCheckResult.getText());
                binding.recyclerAutoSuggestion.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initAdapters() {
        autoSuggestionAdapter = new AutoSuggestionAdapter(new ArrayList<>(), this);
        binding.recyclerAutoSuggestion.setAdapter(autoSuggestionAdapter);


        ArrayAdapter<String> websiteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.website_array_texts));
        binding.spinnerWebsite.setAdapter(websiteAdapter);


        ArrayAdapter<Region> regionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                Region.values());
        binding.spinnerRegion.setAdapter(regionAdapter);
        binding.spinnerRegion.setSelection(6);

        ArrayAdapter<Language> languageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                Language.values());
        binding.spinnerLanguage.setAdapter(languageAdapter);
        binding.spinnerLanguage.setSelection(7);


    }

    private void hintSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive() && this.getCurrentFocus() != null && this.getCurrentFocus().getWindowToken() != null) {
            imm.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * We initialize requests for Search kit. You can customize with different values.
     */
    private void initRequest() {
        webSearchRequest.setLang(Language.TURKISH);
        webSearchRequest.setSregion(Region.TURKEY);
        webSearchRequest.setPn(1);
        webSearchRequest.setPs(10);
        webSearchRequest.setWithin("www.hepsiburada.com");

        commonSearchRequest.setLang(Language.TURKISH);
        commonSearchRequest.setSregion(Region.TURKEY);
        commonSearchRequest.setPn(1);
        commonSearchRequest.setPs(10);

    }

    /**
     * We are making suggestion process on different thread to keep save the app performance.
     *
     * @param query
     */
    @SuppressLint("CheckResult")
    private void makeSuggestion(String query) {

        Observable.create((ObservableOnSubscribe<List<SuggestObject>>) emitter -> {
                    AutoSuggestResponse response =
                            SearchKitInstance.getInstance()
                                    .getSearchHelper()
                                    .suggest(query, Language.ENGLISH);
                    if (response != null && response.getSuggestions() != null && !response.getSuggestions().isEmpty()) {
                        emitter.onNext(response.getSuggestions());
                    }

                    emitter.onComplete();
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(suggestObjects -> {
                    autoSuggestionAdapter.clearData();
                    autoSuggestionAdapter.updateData(suggestObjects);
                });
    }

    /**
     * We are making spell check process on different thread to keep save the app performance.
     *
     * @param query
     */
    @SuppressLint("CheckResult")
    private void getSpellCheck(String query) {
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
                    SpellCheckResponse response =
                            SearchKitInstance.getInstance()
                                    .getSearchHelper()
                                    .spellCheck(query, Language.ENGLISH);
                    if (response != null && response.getCorrectedQuery() != null) {
                        emitter.onNext(response.getCorrectedQuery());
                    } else {
                        Log.e(TAG, "spell error");
                        emitter.onNext("");
                    }
                }).subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    binding.tvSpellingCheck.setText("Spelling check result:");
                    SpannableString result = new SpannableString(s);
                    result.setSpan(new UnderlineSpan(), 0, result.length(), 0);
                    binding.tvSpellingCheckResult.setText(result);
                });
    }


    /**
     * We make other service requests through another thread. This method is more proper for app performance.
     * We used tab layout for changing adapter's data for different response. We are keeping screen state for making requests.
     */
    @SuppressLint("CheckResult")
    public void makeSearch() {
        if (!query.isEmpty()) {

            progressDialogScreen.showProgressDialog();
            Observable.create((ObservableOnSubscribe<BaseSearchResponse>) emitter -> {
                        /**
                         * We are making service request in below.
                         *
                         */
                        switch (tabState) {
                            case WEB:
                                webSearchRequest.setQ(query);
                                BaseSearchResponse<List<WebItem>> webResponse = SearchKitInstance.getInstance().getWebSearcher().search(webSearchRequest);
                                if (webResponse != null && !webResponse.getData().isEmpty())
                                    emitter.onNext(webResponse);
                                else {
                                    progressDialogScreen.dismissProgressDialog();
                                    runOnUiThread(() -> binding.recyclerViewSearchkit.setVisibility(View.GONE));
                                }
                                break;

                            case IMAGE:
                                commonSearchRequest.setQ(query);
                                BaseSearchResponse<List<ImageItem>> imageResponse = SearchKitInstance.getInstance().getImageSearcher().search(commonSearchRequest);
                                if (imageResponse != null && !imageResponse.getData().isEmpty())
                                    emitter.onNext(imageResponse);
                                else {
                                    progressDialogScreen.dismissProgressDialog();
                                    runOnUiThread(() -> binding.recyclerViewSearchkit.setVisibility(View.GONE));
                                }


                                break;

                            case VIDEO:
                                commonSearchRequest.setQ(query);
                                BaseSearchResponse<List<VideoItem>> videoResponse = SearchKitInstance.getInstance().getVideoSearcher().search(commonSearchRequest);
                                if (videoResponse != null && !videoResponse.getData().isEmpty())
                                    emitter.onNext(videoResponse);
                                else {
                                    progressDialogScreen.dismissProgressDialog();
                                    runOnUiThread(() -> binding.recyclerViewSearchkit.setVisibility(View.GONE));
                                }
                                break;

                            case NEWS:
                                commonSearchRequest.setQ(query);
                                BaseSearchResponse<List<NewsItem>> newsResponse = SearchKitInstance.getInstance().getNewsSearcher().search(commonSearchRequest);
                                if (newsResponse != null && !newsResponse.getData().isEmpty())
                                    emitter.onNext(newsResponse);
                                else {
                                    progressDialogScreen.dismissProgressDialog();
                                    runOnUiThread(() -> binding.recyclerViewSearchkit.setVisibility(View.GONE));
                                }
                                break;

                            default:
                                break;
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(baseSearchResponse -> {
                        /**
                         *
                         * The response result comes here if it not null and not empty.
                         * After we are casting the result value with screen state we are setting recyclerView for each different response type.
                         *
                         */
                        switch (tabState) {
                            case WEB: {
                                setAdapterForWEB((List<WebItem>) baseSearchResponse.getData());
                                break;
                            }
                            case IMAGE: {
                                setAdapterForImage((List<ImageItem>) baseSearchResponse.getData());
                                break;
                            }

                            case VIDEO: {
                                setAdapterForVideo((List<VideoItem>) baseSearchResponse.getData());
                                break;
                            }
                            case NEWS: {
                                setAdapterForNews((List<NewsItem>) baseSearchResponse.getData());
                                break;
                            }
                            default: // break state
                                break;
                        }
                    });
        }
    }


    /**
     * After suggestion, user selects one item on the recyclerView and this method is triggered.
     * And other research methods are triggered based on user selection and screen state.
     *
     * @param text
     */
    @Override
    public void onItemClick(String text) {
        getSpellCheck(text);
        binding.recyclerAutoSuggestion.setVisibility(View.GONE);
        query = text;
        makeSearch();

    }

    private void setAdapterForWEB(List<WebItem> data) {
        binding.recyclerViewSearchkit.setVisibility(View.VISIBLE);
        progressDialogScreen.dismissProgressDialog();
        binding.recyclerViewSearchkit.setAdapter(new SearchKitGeneralAdapter(data) {
            @Override
            public int getLayoutResId() {
                return R.layout.item_web_searchkit;
            }

            @Override
            public void onBindData(Object model, int position, View itemView) {
                TextView tv = itemView.findViewById(R.id.tvWebText1);
                TextView tv2 = itemView.findViewById(R.id.tvWebText2);
                TextView tv3 = itemView.findViewById(R.id.tvWebText3);
                tv.setText(Html.fromHtml(((WebItem) model).title));
                tv2.setText(Html.fromHtml(((WebItem) model).snippet));
                tv3.setText(Html.fromHtml(((WebItem) model).click_url));
                tv3.setText(Html.fromHtml(getResources().getString(R.string.linked_text, ((WebItem) model).click_url)));

                tv3.setOnClickListener(view -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(((WebItem) model).click_url));
                    startActivity(browserIntent);
                });
            }
        });
    }

    private void setAdapterForImage(List<ImageItem> data) {
        binding.recyclerViewSearchkit.setVisibility(View.VISIBLE);
        progressDialogScreen.dismissProgressDialog();

        binding.recyclerViewSearchkit.setAdapter(new SearchKitGeneralAdapter(data) {
            @Override
            public int getLayoutResId() {
                return R.layout.item_image_searchkit;
            }

            @Override
            public void onBindData(Object model, int position, View itemView) {
                ImageView image = itemView.findViewById(R.id.imgImageItem);
                TextView tv = itemView.findViewById(R.id.tvImageItemText1);
                TextView tv2 = itemView.findViewById(R.id.tvImageItemText2);

                Picasso.get()
                        .load(((ImageItem) model).source_image.image_content_url)
                        .fit()
                        .into(image);
                tv.setText(((ImageItem) model).title);
                tv2.setText(getResources().getString(R.string.linked_text, ((ImageItem) model).click_url));

                tv2.setOnClickListener(view -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(((ImageItem) model).click_url));
                    startActivity(browserIntent);
                });

            }
        });
    }

    private void setAdapterForVideo(List<VideoItem> data) {
        binding.recyclerViewSearchkit.setVisibility(View.VISIBLE);
        progressDialogScreen.dismissProgressDialog();

        binding.recyclerViewSearchkit.setAdapter(new SearchKitGeneralAdapter(data) {
            @Override
            public int getLayoutResId() {
                return R.layout.item_video_searchkit;
            }

            @Override
            public void onBindData(Object model, int position, View itemView) {
                TextView tv = itemView.findViewById(R.id.tvVideoText1);
                TextView tv2 = itemView.findViewById(R.id.tvVideoText2);
                TextView tv3 = itemView.findViewById(R.id.tvVideoText3);

                tv.setText(((VideoItem) model).title);
                tv2.setText(((VideoItem) model).provider.site_name);
                tv3.setText(((VideoItem) model).click_url);
                tv3.setText(getResources().getString(R.string.linked_text, ((VideoItem) model).click_url));

                tv3.setOnClickListener(view -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(((VideoItem) model).click_url));
                    startActivity(browserIntent);
                });
            }
        });
    }

    private void setAdapterForNews(List<NewsItem> data) {
        binding.recyclerViewSearchkit.setVisibility(View.VISIBLE);
        progressDialogScreen.dismissProgressDialog();
        binding.recyclerViewSearchkit.setAdapter(new SearchKitGeneralAdapter(data) {
            @Override
            public int getLayoutResId() {
                return R.layout.item_news_searchkit;
            }

            @Override
            public void onBindData(Object model, int position, View itemView) {
                TextView tv = itemView.findViewById(R.id.tvItemNewsText1);
                TextView tv2 = itemView.findViewById(R.id.tvItemNewsText2);
                TextView tv3 = itemView.findViewById(R.id.tvItemNewsText3);

                tv.setText(((NewsItem) model).title);
                tv2.setText(((NewsItem) model).provider.site_name);
                tv3.setText(((NewsItem) model).click_url);
                tv3.setText(getResources().getString(R.string.linked_text, ((NewsItem) model).click_url));

                tv3.setOnClickListener(view -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(((NewsItem) model).click_url));
                    startActivity(browserIntent);
                });
            }
        });
    }


    /**
     * For initialize Search Kit instance we need to set credential token.
     * We are sending request to the OAuth server to get token.
     */
    public void initRetrofit() {

        Handler mainHandler = new Handler(this.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                QueryService service = NetworkManager.getInstance().createService(SearchKitMainActivity.this, BASE_URL);
                service.getRequestToken(
                                "client_credentials",
                                CLIENT_ID_TOKEN,
                                "8316388b7d593328b3003b44bbcf5c5247266f6e22920eef883c9810586c1e45")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<TokenResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.e(TAG, "onSubscribe");
                            }

                            @Override
                            public void onNext(TokenResponse tokenResponse) {
                                if (tokenResponse != null) {
                                    if (tokenResponse.getAccessToken() != null) {
                                        SearchKitInstance.getInstance().setInstanceCredential(tokenResponse.getAccessToken());
                                        SearchKitInstance.getInstance().getWebSearcher().setCredential(tokenResponse.getAccessToken());
                                    } else {
                                        Log.e(TAG, "get responseBody token is null");
                                    }
                                } else {
                                    Log.e(TAG, "get responseBody is null");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "get token error: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                Log.e(TAG, "onComplete");
                            }
                        });
            }
        };
        mainHandler.post(myRunnable);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}