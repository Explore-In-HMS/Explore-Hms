package com.hms.explorehms.huawei.feature_navikit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.Util;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mBtnApiTest;
    private TextView tvServiceIntroductionBody, tvServiceScenariosBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarNaviKitInfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, (getResources().getString(R.string.navi_more_information_link_documentation_link)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initView() {
        mBtnApiTest = findViewById(R.id.btnLetsStart_navikit);
        tvServiceIntroductionBody = findViewById(R.id.tvServiceIntroductionBody_navikit);
        tvServiceScenariosBody = findViewById(R.id.tvServiceScenariosBody_navikit);

        tvServiceIntroductionBody.setText(getResources().getString(R.string.service_introduction_body_navikit));
        tvServiceScenariosBody.setText(getResources().getString(R.string.service_scenarios_body_navikit));

        mBtnApiTest.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, ApiTestActivity.class)));
    }
}