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
package com.hms.explorehms.huawei.feature_hiai.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplicationUtils {

    private ApplicationUtils(){

    }

    public static Boolean checkDeviceProcessor(){

        boolean processorOK = false;
        String hardware = "Hardware";
        try(BufferedReader br = new BufferedReader (new FileReader("/proc/cpuinfo"))) {

            String str;

            Map<String, String> output = new HashMap<>();

            while ((str = br.readLine ()) != null) {

                String[] data = str.split (":");

                if (data.length > 1) {

                    String key = data[0].trim ().replace (" ", "_");
                    if (key.equals(hardware)){
                        output.put(key, data[1].trim ());
                        break;
                    }
                }
            }

            /**
             * Farklı şekillerde kullanılabilir
             */

            if(output.get(hardware) != null && !Objects.equals(output.get(hardware), "")){
                String processorHardware = output.get(hardware);

                processorOK = processorHardware != null && (
                        processorHardware.contains("Kirin970") ||
                        processorHardware.contains("Kirin990") );

            }
            br.close();

        }catch (IOException e) {
            Log.e("ProcessorInfo",e.toString());
            processorOK = false;
        }

        return processorOK;
    }

    public static void openWebPage(Activity activity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

}
