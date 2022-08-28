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

package com.hms.explorehms.huawei.feature_dynamicability;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.feature.install.FeatureInstallManager;
import com.huawei.hms.feature.install.FeatureInstallManagerFactory;
import com.huawei.hms.feature.listener.InstallStateListener;
import com.huawei.hms.feature.model.FeatureInstallException;
import com.huawei.hms.feature.model.FeatureInstallRequest;
import com.huawei.hms.feature.model.FeatureInstallSessionStatus;
import com.huawei.hms.feature.model.InstallState;
import com.huawei.hms.feature.tasks.FeatureTask;
import com.huawei.hms.feature.tasks.listener.OnFeatureCompleteListener;
import com.huawei.hms.feature.tasks.listener.OnFeatureFailureListener;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class DynamicAbilityTestActivity extends AppCompatActivity {

    private static final String STATE ="STATE";
    private static final String MODULE_NAME_FOR_INSTALL ="dynamicfeaturetest";
    private static final String MODULE_ACTIVITY_NAME ="com.hms.explorehms.huawei.feature_dynamicability.dynamicfeaturetest.DynamicFeatureTestActivity";

    private FeatureInstallManager mFeatureInstMan;
    private Button btnInstallNewModule;
    private Button btnStartMod;
    private Button btnDownloadLan;

    private int sessionID = -1;

    private final InstallStateListener stateListener = new InstallStateListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            Log.d(STATE,"Install state : " + installState.toString());

            if(installState.status() == FeatureInstallSessionStatus.REQUIRES_USER_CONFIRMATION){
                try{
                    Log.d(STATE, "user confirmation ");

                    Toast.makeText(getApplicationContext(), "User confirmation", Toast.LENGTH_SHORT).show();

                    mFeatureInstMan.triggerUserConfirm(installState,DynamicAbilityTestActivity.this,1);
                }catch (Exception e){
                    Log.e(STATE,e.toString());
                }
                return;
            }

            if(installState.status() == FeatureInstallSessionStatus.REQUIRES_PERSON_AGREEMENT){
                try{
                    Log.d(STATE, "user agreement ");

                    Toast.makeText(getApplicationContext(), "Person agreement", Toast.LENGTH_SHORT).show();

                    mFeatureInstMan.triggerUserConfirm(installState,DynamicAbilityTestActivity.this,1);
                }catch (Exception e){
                    Log.e(STATE,e.toString());
                }
                return;
            }

            if(installState.status() == FeatureInstallSessionStatus.INSTALLING){
                Log.d(STATE,"installed success,can use new feature");

                Toast.makeText(getApplicationContext(), "Feature installing", Toast.LENGTH_SHORT).show();
                return;
            }

            if(installState.status() == FeatureInstallSessionStatus.FAILED){
                Log.e(STATE, "fail ");

                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                Log.e(STATE, String.valueOf(installState.errorCode()));
                return;
            }

            if (installState.status() == FeatureInstallSessionStatus.INSTALLED) {
                Toast.makeText(getApplicationContext(), "Module installed", Toast.LENGTH_SHORT).show();
                btnStartMod.setEnabled(true);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_ability_test);
        setupToolbar();

        mFeatureInstMan = FeatureInstallManagerFactory.create(this);

        setButtonEvent();

        checkModuleState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFeatureInstallListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setUnRegisterListener();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        try {
            FeatureCompat.install(newBase);
        }catch (Exception e){
            Log.e(STATE,e.toString());
        }
    }

    private void checkModuleState() {
        try {
            Set<String> installedModules = mFeatureInstMan.getAllInstalledModules();

            Log.d(STATE,"All installed modules"+installedModules);

            if(installedModules.contains(MODULE_NAME_FOR_INSTALL)){
                btnInstallNewModule.setEnabled(false);
                btnStartMod.setEnabled(true);
            }else{
                btnInstallNewModule.setEnabled(true);
                btnStartMod.setEnabled(false);
            }
        }catch (Exception e){
            Log.e(STATE,e.toString());
        }
    }

    private void setButtonEvent() {
        btnInstallNewModule = findViewById(R.id.btnDownloadModule);
        btnInstallNewModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                installFeature();
                checkModuleState();
            }
        });

        btnStartMod = findViewById(R.id.btnStartFeature);
        btnStartMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFeature();
            }
        });

        btnDownloadLan = findViewById(R.id.btnDownloadLanguage);
        btnDownloadLan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLanguage();
            }
        });

    }

    private void loadLanguage() {
        try {
            if (mFeatureInstMan == null) {
                return;
            }

            Log.d(STATE,"load language");
            final Set<String>language = new HashSet<>();
            language.add("tr-TR");
            language.add("en-EN");

            FeatureInstallRequest.Builder newBuilder = FeatureInstallRequest.newBuilder();

            for (String lang : language) {
                newBuilder.addLanguage(Locale.forLanguageTag(lang));
            }

            final FeatureInstallRequest req = newBuilder.build();
            FeatureTask<Integer> newTask = mFeatureInstMan.installFeature(req);

            newTask.addOnListener(new OnFeatureCompleteListener<Integer>() {
                @Override
                public void onComplete(FeatureTask<Integer> featureTask) {
                    if(featureTask.isComplete()){
                        Log.d(STATE,"Load Language completed");
                        if(featureTask.isSuccessful()){
                            Log.d(STATE,"Load language successful");
                            //check current page language
                            if (req.getLanguages().size() > 0) {
                                if (btnDownloadLan.getText().equals("Load Turkish language package")) {
                                    setLanguageOfApp(req.getLanguages().get(0));
                                } else {
                                    setLanguageOfApp(req.getLanguages().get(1));
                                }
                                recreate();
                            }
                        } else {
                            Exception exception = featureTask.getException();
                            Log.e(STATE,"Load language : "+exception.toString());
                        }
                    }
                }
            });

            newTask.addOnListener(new OnFeatureFailureListener<Integer>() {
                @Override
                public void onFailure(Exception e) {
                    if (e instanceof FeatureInstallException) {
                        Log.d(STATE, "onFailure callback "
                                + ((FeatureInstallException) e).getErrorCode());
                    } else {
                        Log.e(STATE, "onFailure callback ", e);
                    }
                }
            });

        }catch (Exception e){
            Log.e(STATE, e.toString());
        }
    }

    private void setLanguageOfApp(Locale lng) {

        Locale.setDefault(lng);
        Resources resources = getBaseContext().getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());
        configuration.setLocale(lng);
        resources.updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }

    private void installFeature() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("New Module Installation")
                    .setMessage("Are you sure to install new module ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (mFeatureInstMan == null) {
                                return;
                            }

                            if(mFeatureInstMan.getAllInstalledModules().contains(MODULE_NAME_FOR_INSTALL)){
                                Log.d(STATE,"Same module cannot be installed");
                                return;
                            }

                            //Set dynamic feature module name
                            FeatureInstallRequest request = FeatureInstallRequest.newBuilder()
                                    .addModule(MODULE_NAME_FOR_INSTALL)
                                    .build();

                            final FeatureTask<Integer> task = mFeatureInstMan.installFeature(request);

                            //call Feature success listener
                            task.addOnListener(new OnFeatureCompleteListener<Integer>() {
                                @Override
                                public void onComplete(FeatureTask<Integer> featureTask) {
                                    if (featureTask.isSuccessful()) {
                                        Log.d(STATE, "Feature installation successfully");
                                        sessionID = featureTask.getResult();
                                        Toast.makeText(getApplicationContext(), "Sessions ID : " + sessionID, Toast.LENGTH_SHORT).show();

                                        Log.d(STATE, "succeed to start install. session id :" + sessionID);
                                        btnStartMod.setEnabled(true);
                                        btnInstallNewModule.setEnabled(false);
                                    }
                                }
                            });

                            task.addOnListener(new OnFeatureFailureListener<Integer>() {
                                @Override
                                public void onFailure(Exception e) {
                                    if (e instanceof FeatureInstallException) {
                                        int errorCode = ((FeatureInstallException) e).getErrorCode();
                                        Log.d(STATE, "load feature onFailure.errorCode:" + errorCode);
                                    }
                                }
                            });

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(STATE,"install operation cancelled");
                }
            }).show();

        }catch (Exception e){
            Log.e(STATE,e.toString());
        }
    }

    private void installModule(){
        //Set dynamic feature module name
        final FeatureInstallRequest request = FeatureInstallRequest.newBuilder()
                .addModule(MODULE_NAME_FOR_INSTALL)
                .build();

        final FeatureTask<Integer> task = mFeatureInstMan.installFeature(request);

        //call Feature success listener
        task.addOnListener(new OnFeatureCompleteListener<Integer>() {
            @Override
            public void onComplete(FeatureTask<Integer> featureTask) {
                if(featureTask.isComplete()){
                    Log.d(STATE,"Feature installing");
                    if(featureTask.isSuccessful()){
                        Log.d(STATE,"Feature installation successfully");
                        sessionID = featureTask.getResult();
                        Toast.makeText(getApplicationContext(),"Sessions ID : "+sessionID,Toast.LENGTH_SHORT).show();

                        Log.d(STATE, "succeed to start install. session id :" + sessionID);

                        btnStartMod.setEnabled(true);
                        btnInstallNewModule.setEnabled(false);
                    }else{
                        Exception exception = featureTask.getException();
                        Log.d(STATE, "onComplete: "+exception);
                    }
                }else{
                    Log.d(STATE, "fail to start install.");
                    Exception exception = featureTask.getException();
                    Log.d(STATE, "onComplete: "+exception);
                }
            }
        });

        task.addOnListener(new OnFeatureFailureListener<Integer>() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof FeatureInstallException) {
                    int errorCode = ((FeatureInstallException) e).getErrorCode();
                    Log.d(STATE, "load feature onFailure.errorCode:" + errorCode);
                }
            }
        });
    }

    private void startFeature(){
        try {

            Set<String> moduleName = mFeatureInstMan.getAllInstalledModules();

            Log.d(STATE,"All Modules : "+moduleName);

            if(moduleName != null && moduleName.contains(MODULE_NAME_FOR_INSTALL)){
                startActivity(new Intent(getApplicationContext(),Class.forName(MODULE_ACTIVITY_NAME)));
            }else{
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.text_for_start_feature_toast),Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.e(STATE,e.toString());
        }
    }

    private void setFeatureInstallListener() {
        mFeatureInstMan.registerInstallListener(stateListener);
    }

    private void setUnRegisterListener() {
        mFeatureInstMan.unregisterInstallListener(stateListener);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbDynamicAbilityTest);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
