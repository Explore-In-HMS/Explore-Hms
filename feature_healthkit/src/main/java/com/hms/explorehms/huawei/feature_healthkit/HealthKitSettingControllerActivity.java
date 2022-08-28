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

package com.hms.explorehms.huawei.feature_healthkit;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.hihealth.DataController;
import com.huawei.hms.hihealth.HiHealthOptions;
import com.huawei.hms.hihealth.HuaweiHiHealth;
import com.huawei.hms.hihealth.SettingController;
import com.huawei.hms.hihealth.data.DataCollector;
import com.huawei.hms.hihealth.data.DataType;
import com.huawei.hms.hihealth.data.Field;
import com.huawei.hms.hihealth.data.SamplePoint;
import com.huawei.hms.hihealth.data.SampleSet;
import com.huawei.hms.hihealth.options.DataTypeAddOptions;
import com.huawei.hms.hihealth.options.ReadOptions;
import com.huawei.hms.hihealth.result.ReadReply;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Sample code for add new DataType and read it
 *
 * @since 2020-03-17
 */
public class HealthKitSettingControllerActivity extends AppCompatActivity implements OnItemSelectedListener {

    /**
     * Custom data type read permission
     */
    public static final String HEALTHKIT_SELF_DEFINING_DATA_READ = "https://www.huawei.com/healthkit/selfdefining.read";

    /**
     * Custom data type write permission
     */
    public static final String HEALTHKIT_SELF_DEFINING_DATA_WRITE =
            "https://www.huawei.com/healthkit/selfdefining.write";

    /**
     * Custom data type read / write permission
     */
    public static final String HEALTHKIT_SELF_DEFINING_DATA_BOTH = "https://www.huawei.com/healthkit/selfdefining.both";

    private static final String TAG = "SettingController";

    // Line separators for the display on the UI
    private static final String SPLIT = "*******************************" + System.lineSeparator();

    // The container that stores Field
    private static final ArrayList<Field> SPINNERLIST = new ArrayList<>();

    // The container that stores Field name
    private static final ArrayList<String> SPINNERLISTSTR = new ArrayList<>();

    // Object of DataController for fitness and health data, providing APIs for read/write, batch read/write, and listening
    private DataController dataController;

    // Object of SettingController for fitness and health data, providing APIs for read/write, batch read/write, and listening
    private SettingController settingController;

    // Internal context object of the activity
    private Context context;

    // EditText for setting data type name information on the UI
    private EditText dataTypeNameView;

    // TextView for displaying operation information on the UI
    private TextView logInfoView;

    // The field value you choose, default value is Field.FIELD_BPM
    private Field selectedField = Field.FIELD_STEPS;

    // The field name value you choose
    private String selectedFieldStr = "FIELD_STEPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_setting_controller);
        setupToolbar();
        context = this;

        initActivityView();
        initDataController();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_setting_controller);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Implementation of OnItemSelectedListener interface.
     * Assign a value to the variable
     */
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (!SPINNERLIST.isEmpty() && arg2 < SPINNERLIST.size()) {
            selectedField = SPINNERLIST.get(arg2);
            selectedFieldStr = SPINNERLISTSTR.get(arg2);
            CommonUtil.logger("your choice is ：" + selectedFieldStr, TAG, logInfoView);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Log.i(TAG, "onNothingSelected");
    }

    /**
     * Initialize Activity view.
     */
    private void initActivityView() {
        logInfoView = findViewById(R.id.setting_controller_log_info);

        for (java.lang.reflect.Field field : Field.class.getDeclaredFields()) {
            if (field.getType() != Field.class) {
                continue;
            }
            try {
                SPINNERLIST.add((Field) (field.get(Field.class)));
                SPINNERLISTSTR.add(field.getName());
            } catch (IllegalAccessException e) {
                CommonUtil.logger("initActivityView: catch an IllegalAccessException", TAG, logInfoView);
            }
        }

        int size = SPINNERLISTSTR.size();
        String[] array = SPINNERLISTSTR.toArray(new String[size]);

        // drop-down box of Field name
        Spinner spinner = (Spinner) findViewById(R.id.spinner01);
        // drop-down box adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(SPINNERLIST.indexOf(selectedField));

        dataTypeNameView = (EditText) findViewById(R.id.data_type_name_text);
        dataTypeNameView.setText(this.getPackageName() + ".anyExtendName");
        dataTypeNameView.setFocusable(true);
        dataTypeNameView.requestFocus();
        dataTypeNameView.setFocusableInTouchMode(true);
    }

    /**
     * Initialize variable of mSignInHuaweiId.
     */
    private void initDataController() {
        // create HiHealth Options, donnot add any datatype here.
        HiHealthOptions hiHealthOptions = HiHealthOptions.builder().build();
        // get AuthHuaweiId by HiHealth Options.
        AuthHuaweiId signInHuaweiId = HuaweiIdAuthManager.getExtendedAuthResult(hiHealthOptions);
        // get DataController.
        dataController = HuaweiHiHealth.getDataController(context, signInHuaweiId);
        settingController = HuaweiHiHealth.getSettingController(context, signInHuaweiId);
    }

    /**
     * add new DataType.
     * you need two object to add new DataType: DataTypeAddOptions and SettingController.
     * specify the field by drop-down box, You cannot add DataType with duplicate DataType's name.
     * You can add multiple field，For simplicity, only one field is added here.
     *
     * @param view (indicating a UI object)
     */
    public void addNewDataType(View view) {
        // get DataType name from EditText view,
        // The name must start with package name, and End with a custom name.
        String dataTypeName = dataTypeNameView.getText().toString();
        // create DataTypeAddOptions,You must specify the Field that you want to add,
        // You can add multiple Fields here.
        DataTypeAddOptions dataTypeAddOptions =
                new DataTypeAddOptions.Builder().setName(dataTypeName).addField(selectedField).build();

        // create SettingController and add new DataType
        // The added results are displayed in the phone screen
        settingController.addDataType(dataTypeAddOptions)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        CommonUtil.printFailureMessage(TAG, e, "addNewDataType", logInfoView);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataType>() {
                    @Override
                    public void onComplete(Task<DataType> task) {
                        String res = task.isSuccessful() ? getString(R.string.success) : getString(R.string.failed);
                        CommonUtil.logger("add dataType of " + selectedFieldStr + " is " + res, TAG, logInfoView);
                        if (task.getException() != null) {
                            CommonUtil.logger("getException is " + task.getException().toString(), TAG, logInfoView);
                        }
                    }
                });
    }

    /**
     * read DataType.
     * Get DataType with the specified name
     *
     * @param view (indicating a UI object)
     */
    public void readDataType(View view) {
        // data type name
        String dataTypeName = dataTypeNameView.getText().toString();

        // create SettingController and get DataType with the specified name
        // The results are displayed in the phone screen
        settingController.readDataType(dataTypeName)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        CommonUtil.printFailureMessage(TAG, e, "readDataType", logInfoView);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataType>() {
                    @Override
                    public void onComplete(Task<DataType> task) {
                        if (task.isSuccessful()) {
                            CommonUtil.logger("DataType : " + task.getResult(), TAG, logInfoView);
                        }
                    }
                });
    }

    /**
     * disable HiHealth.
     * After calling this function, HiHealth will cancel All your Records.
     *
     * @param view (indicating a UI object)
     */
    public void disableHiHealth(View view) {
        // create SettingController and disable HiHealth (cancel All your Records).
        // The results are displayed in the phone screen.
        settingController.disableHiHealth()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        CommonUtil.printFailureMessage(TAG, e, "disableHiHealth", logInfoView);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        String res = task.isSuccessful() ? getString(R.string.success) : getString(R.string.failed);
                        CommonUtil.logger("disableHiHealth is " + res, TAG, logInfoView);
                    }
                });
    }

    /**
     * check whether the Huawei Health app authorise access to HealthKit.
     * After calling this function, if you do not authorise, we will pop the windows to Health app authorization windows.
     *
     * @param view (indicating a UI object)
     */
    public void checkAuthorization(View view) {
        // check whether the Huawei Health app authorise access to HealthKit.
        // if you do not authorise, we will pop the windows to Health app authorization windows.
        settingController.checkHealthAppAuthorisation()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        CommonUtil.printFailureMessage(TAG, e, "checkHealthAppAuthorisation", logInfoView);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        String res = task.isSuccessful() ? getString(R.string.success) : getString(R.string.failed);
                        CommonUtil.logger("checkHealthAppAuthorisation is " + res, TAG, logInfoView);
                    }
                });
    }

    /**
     * get whether the Huawei Health app authorise access to HealthKit.
     * After calling this function, return true means authorised, false means not authorised.
     *
     * @param view (indicating a UI object)
     */
    public void getAuthorization(View view) {
        // get whether the Huawei Health app authorise access to HealthKit.
        // After calling this function, return true means authorised, false means not authorised.
        settingController.getHealthAppAuthorisation()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        CommonUtil.printFailureMessage(TAG, e, "getHealthAppAuthorisation", logInfoView);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(Task<Boolean> task) {
                        String res = task.isSuccessful() ? getString(R.string.success) : getString(R.string.failed);
                        CommonUtil.logger("getHealthAppAuthorisation is " + res, TAG, logInfoView);

                    }
                });
    }

    /**
     * Use the data controller to add a sampling dataset.
     *
     * @param view (indicating a UI object)
     * @throws ParseException (indicating a failure to parse the time string)
     */
    public void insertSelfData(View view) throws ParseException {
        // 0. create new DataType.
        List<Field> fieldsList = new ArrayList<>();
        fieldsList.add(selectedField);
        DataType selfDataType = new DataType(dataTypeNameView.getText().toString(), HEALTHKIT_SELF_DEFINING_DATA_READ,
                HEALTHKIT_SELF_DEFINING_DATA_WRITE, HEALTHKIT_SELF_DEFINING_DATA_BOTH, fieldsList);

        // 1. Build a DataCollector object.
        DataCollector dataCollector = new DataCollector.Builder().setPackageName(context)
                .setDataType(selfDataType)
                .setDataStreamName(selectedFieldStr)
                .setDataGenerateType(DataCollector.DATA_TYPE_RAW)
                .build();

        // 2. Create a sampling dataset set based on the data collector.
        final SampleSet sampleSet = SampleSet.create(dataCollector);

        // 3. Build the start time, end time, and incremental step count for a DT_CONTINUOUS_STEPS_DELTA sampling point.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date startDate = dateFormat.parse("2020-03-17 09:00:00");
        Date endDate = dateFormat.parse("2020-03-17 09:05:00");
        int intValue = 1000;
        float floatValue = 10.0f;
        String strValue = "hello";

        // 4. Build a DT_CONTINUOUS_STEPS_DELTA sampling point.
        SamplePoint samplePoint = sampleSet.createSamplePoint()
                .setTimeInterval(startDate.getTime(), endDate.getTime(), TimeUnit.MILLISECONDS);
        selectedField.getFormat();

        switch (selectedField.getFormat()) {
            case Field.FORMAT_INT32:
                samplePoint.getFieldValue(selectedField).setIntValue(intValue);
                break;
            case Field.FORMAT_FLOAT:
                samplePoint.getFieldValue(selectedField).setFloatValue(floatValue);
                break;
            case Field.FORMAT_STRING:
                samplePoint.getFieldValue(selectedField).setStringValue(strValue);
                break;
            case Field.FORMAT_MAP:
                samplePoint.getFieldValue(selectedField).setKeyValue("hello", 100.0f);
                break;
            default:
                CommonUtil.logger("ERROR : Field format does not match any of the specified Format", TAG, logInfoView);
        }

        // 5. Save a DT_CONTINUOUS_STEPS_DELTA sampling point to the sampling dataset.
        // You can repeat steps 3 through 5 to add more sampling points to the sampling dataset.
        sampleSet.addSample(samplePoint);

        // 6. Call the data controller to insert the sampling dataset into the Health platform.
        // it is an asynchronous operation. Therefore, a listener needs to be registered to monitor whether the data insertion is successful or not.
        dataController.insert(sampleSet).addOnSuccessListener(result -> {
            CommonUtil.logger("Success insert an SampleSet into HMS core / SettingController", TAG, logInfoView);
            CommonUtil.showSampleSet(sampleSet,TAG,logInfoView);
            CommonUtil.logger(SPLIT, TAG, logInfoView);
        }).addOnFailureListener(e -> {
            CommonUtil.printFailureMessage(TAG, e, "insert", logInfoView);
        });
    }

    /**
     * Use the data controller to query the sampling dataset by specific criteria.
     *
     * @param view (indicating a UI object)
     * @throws ParseException (indicating a failure to parse the time string)
     */
    public void readSelfData(View view) throws ParseException {
        // 0. create new DataType.
        List<Field> fieldsList = new ArrayList<>();
        fieldsList.add(selectedField);
        DataType selfDataType = new DataType(dataTypeNameView.getText().toString(), HEALTHKIT_SELF_DEFINING_DATA_READ,
                HEALTHKIT_SELF_DEFINING_DATA_WRITE, HEALTHKIT_SELF_DEFINING_DATA_BOTH, fieldsList);

        // 1. Build the condition for data query: a DataCollector object.
        DataCollector dataCollector = new DataCollector.Builder().setPackageName(context)
                .setDataType(selfDataType)
                .setDataStreamName(selectedFieldStr)
                .setDataGenerateType(DataCollector.DATA_TYPE_RAW)
                .build();

        // 2. Build the time range for the query: start time and end time.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date startDate = dateFormat.parse("2020-03-17 09:00:00");
        Date endDate = dateFormat.parse("2020-03-17 09:05:00");

        // 3. Build the condition-based query objec
        ReadOptions readOptions = new ReadOptions.Builder().read(dataCollector)
                .setTimeRange(startDate.getTime(), endDate.getTime(), TimeUnit.MILLISECONDS)
                .build();

        // 4. Use the specified condition query object to call the data controller to query the sampling dataset.
        Task<ReadReply> readReplyTask = dataController.read(readOptions);

        // 5. Calling the data controller to query the sampling dataset is an asynchronous operation.
        // Therefore, a listener needs to be registered to monitor whether the data query is successful or not.
        readReplyTask.addOnSuccessListener(new OnSuccessListener<ReadReply>() {
            @Override
            public void onSuccess(ReadReply readReply) {
                CommonUtil.logger("Success read an SampleSets from HMS core", TAG, logInfoView);
                for (SampleSet sampleSet : readReply.getSampleSets()) {
                    CommonUtil.showSampleSet(sampleSet,TAG,logInfoView);
                }
                CommonUtil.logger(SPLIT, TAG, logInfoView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                CommonUtil.printFailureMessage(TAG, e, "read", logInfoView);
            }
        });
    }
}
