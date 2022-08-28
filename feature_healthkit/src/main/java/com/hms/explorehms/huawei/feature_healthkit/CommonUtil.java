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

import android.util.Log;
import android.widget.TextView;

import com.huawei.hms.common.ApiException;
import com.huawei.hms.hihealth.HiHealthStatusCodes;
import com.huawei.hms.hihealth.data.Field;
import com.huawei.hms.hihealth.data.SamplePoint;
import com.huawei.hms.hihealth.data.SampleSet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能描述
 *
 * @since 2020-09-19
 */
class CommonUtil {
    // Line separators for the display on the UI
    private static final String SPLIT = "*******************************" + System.lineSeparator();

    private CommonUtil() {

    }

    /**
     * Printout failure exception error code and error message
     *
     * @param tag         activity log tag
     * @param e           Exception object
     * @param api         Interface name
     * @param logInfoView Text View object
     */
    public static void printFailureMessage(String tag, Exception e, String api, TextView logInfoView) {
        String errorCode = e.getMessage();
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(errorCode);
        String failText = "failure";
        if (e instanceof ApiException) {
            int eCode = ((ApiException) e).getStatusCode();
            String errorMsg = HiHealthStatusCodes.getStatusCodeMessage(eCode);
            logger(api + failText + eCode + ":" + errorMsg, tag, logInfoView);
            return;
        } else if (isNum.matches()) {
            String errorMsg = HiHealthStatusCodes.getStatusCodeMessage(Integer.parseInt(errorCode));
            logger(api + failText + errorCode + ":" + errorMsg, tag, logInfoView);
            return;
        } else {
            logger(api + failText + errorCode, tag, logInfoView);
        }
        logger(SPLIT, tag, logInfoView);
    }

    /**
     * Send the operation result logs to the logcat and TextView control on the UI
     *
     * @param string      indicating the log string
     * @param tag         activity log tag
     * @param logInfoView Text View object
     */
    public static void logger(String string, String tag, TextView logInfoView) {
        Log.i(tag, string);
        logInfoView.append(string + System.lineSeparator());
        int offset = logInfoView.getLineCount() * logInfoView.getLineHeight();
        if (offset > logInfoView.getHeight()) {
            logInfoView.scrollTo(0, offset - logInfoView.getHeight());
        }
    }

    public static void showSampleSet(SampleSet sampleSet, String tag, TextView textView) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SamplePoint samplePoint : sampleSet.getSamplePoints()) {
            logger("Sample point type: " + samplePoint.getDataType().getName(), tag, textView);
            Date start = new Date(samplePoint.getStartTime(TimeUnit.MILLISECONDS));
            Date end = new Date(samplePoint.getEndTime(TimeUnit.MILLISECONDS));
            logger("Start: " + dateFormat.format(start), tag, textView);
            logger("End: " + dateFormat.format(end), tag, textView);
            for (Field field : samplePoint.getDataType().getFields()) {
                logger("Field: " + field.getName() + " Value: " + samplePoint.getFieldValue(field), tag, textView);
            }
        }
    }


}
