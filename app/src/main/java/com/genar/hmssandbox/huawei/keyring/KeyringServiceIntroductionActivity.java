package com.genar.hmssandbox.huawei.keyring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.genar.hmssandbox.huawei.R;

public class KeyringServiceIntroductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyring_service_introduction);
        setupToolbar();
    }


    public void runKeyringExample(View view) {
        Intent intent = new Intent(getApplicationContext(), KeyringServiceActivity.class);
        startActivity(intent);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_keyring);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}