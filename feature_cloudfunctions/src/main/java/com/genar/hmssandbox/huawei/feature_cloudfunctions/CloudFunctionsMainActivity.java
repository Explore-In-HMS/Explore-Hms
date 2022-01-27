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

package com.genar.hmssandbox.huawei.feature_cloudfunctions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_cloudfunctions.databinding.ActivityCloudFunctionsMainBinding;
import com.genar.hmssandbox.huawei.feature_cloudfunctions.model.MethodTypes;
import com.google.android.material.textfield.TextInputEditText;
import com.huawei.agconnect.function.AGCFunctionException;
import com.huawei.agconnect.function.AGConnectFunction;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CloudFunctionsMainActivity extends AppCompatActivity {


    private static final String TAG = "CloudFunctionsError";
    private static final String RESULT_OF_JSON = "result";
    private ActivityCloudFunctionsMainBinding binding;
    private AGConnectFunction agConnectFunction;

    private ProgressDialogScreen progressDialogScreen;

    /**
     * ViewBinding process is done here.
     * And we get Cloud Functions instance in here.
     * We need this instance for communicate with our functions.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCloudFunctionsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar();
        agConnectFunction = AGConnectFunction.getInstance();

        initUI();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarCloudFunctions);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.cloudfunctions_more_information_link));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * We initialized our UI components and set listeners to the buttons and the textView.
     * In listener we checked user input for requests.
     */
    private void initUI() {

        progressDialogScreen = new ProgressDialogScreen(this);
        String[] methodTypeArr = {
                MethodTypes.SUM.getMethodValue(),
                MethodTypes.SUB.getMethodValue(),
                MethodTypes.MUL.getMethodValue(),
                MethodTypes.DIV.getMethodValue()
        };

        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, methodTypeArr);
        binding.spinnerMethodCloudfunctions.setAdapter(methodAdapter);

        binding.btnCalculateCloudfunctions.setOnClickListener(view -> {
            if (validator(binding.edtValue1Cloudfunctions, binding.edtValue2Cloudfunctions,
                    MethodTypes.values()[binding.spinnerMethodCloudfunctions.getSelectedItemPosition()].getMethodType())) {

                progressDialogScreen.showProgressDialog();
                calculate(
                        binding.spinnerMethodCloudfunctions.getSelectedItemPosition(),
                        Integer.parseInt(binding.edtValue1Cloudfunctions.getEditableText().toString()),
                        Integer.parseInt(binding.edtValue2Cloudfunctions.getEditableText().toString())
                );
            }
        });

        binding.btnFindZodiacCloudfunctions.setOnClickListener(view -> {
            if (binding.edtValue3Cloudfunctions.getEditableText().toString().isEmpty() ||
                    Integer.parseInt(binding.edtValue3Cloudfunctions.getEditableText().toString()) == 0) {
                binding.edtValue3Cloudfunctions.setError("Please enter a valid value.");
            } else {
                progressDialogScreen.showProgressDialog();
                findZodiac(Integer.parseInt(binding.edtValue3Cloudfunctions.getEditableText().toString()));
            }
        });
    }


    /**
     * For calculation function we need three value.
     * We created a hashmap to make requests and set values for the function request.
     * While creating Cloud Function, we set the request parameters.
     * After, we are using Cloud Function instance for request response process.
     *
     * @param method
     * @param value1
     * @param value2
     */
    private void calculate(int method, int value1, int value2) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("method", method);
        map.put("value1", value1);
        map.put("value2", value2);

        agConnectFunction.wrap("function-calculator-$latest").call(map)
                .addOnCompleteListener(task -> {
                    progressDialogScreen.dismissProgressDialog();
                    if (task.isSuccessful()) {
                        String value = task.getResult().getValue();

                        setTvResultCloudFunction(method, value);

                        Log.i(TAG, value);
                    } else {
                        Toast.makeText(this,"Please try again",Toast.LENGTH_SHORT).show();
                        Exception e = task.getException();
                        if (e instanceof AGCFunctionException) {
                            int errCode = ((AGCFunctionException) e).getCode();
                            String message = e.getMessage();
                            Log.e(TAG, "errorCode: " + errCode + ", message: " + message);
                        }
                    }
                });
    }

    private void setTvResultCloudFunction(int method, String value) {
        try {
            JSONObject object = new JSONObject(value);
            if (method == 3) {
                Double result = (Double) object.get(RESULT_OF_JSON);
                binding.tvResultCloudfunctions.setText(getResources().getString(R.string.result_text_cloudstorage_f, result));
            } else {
                int result = (int) object.get(RESULT_OF_JSON);
                binding.tvResultCloudfunctions.setText(getResources().getString(R.string.result_text_cloudstorage, result));
            }
        } catch (JSONException e) {
            Log.i(TAG, e.getLocalizedMessage());
        }
    }

    /**
     * For finding zodiac function we need one value.
     * We created a hashmap to make requests and set values for the function request.
     * While creating Cloud Function, we set the request parameters.
     * After, we are using Cloud Function instance for request response process.
     *
     * @param year
     */
    private void findZodiac(int year) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("year", year);

        agConnectFunction.wrap("function-year-animal-$latest").call(map)
                .addOnCompleteListener(task -> {
                    progressDialogScreen.dismissProgressDialog();
                    if (task.isSuccessful()) {
                        String value = task.getResult().getValue();
                        try {
                            JSONObject object = new JSONObject(value);
                            String result = object.get(RESULT_OF_JSON).toString();
                            binding.tvZodiacResultCloudfunctions.setText(getResources().getString(R.string.resultZodiac_text_cloudstorage, result));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i(TAG, value);
                    } else {
                        Exception e = task.getException();
                        Log.e(TAG, e.toString());
                        Toast.makeText(this,"Please try again",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * we are controlling user input in here.
     *
     * @param edt1
     * @param edt2
     * @param divText
     * @return
     */
    private boolean validator(TextInputEditText edt1, TextInputEditText edt2, String divText) {
        if (edt1.getEditableText().toString().trim().isEmpty()) {
            edt1.setError("Please fill in the empty area.");
            return false;
        }
        if (edt2.getEditableText().toString().trim().isEmpty()) {
            edt2.setError("Please fill in the empty area.");
            return false;
        }
        if (divText.equals("div") && Integer.parseInt(edt2.getEditableText().toString().trim()) == 0) {
            edt2.setError("There is no division to 0.");
            return false;
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

}