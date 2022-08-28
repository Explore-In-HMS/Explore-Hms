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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.generalCardRecognition.cardOperations;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.huawei.hms.mlsdk.text.MLText;

import java.util.ArrayList;
import java.util.List;

/**
 * Post processing plug-in of Hong Kong permanent identity card recognition
 */
public class HKIdCardProcessor implements GeneralCardProcessor {

    private static final String TAG = HKIdCardProcessor.class.getSimpleName();

    private final MLText text;

    public HKIdCardProcessor(MLText text) {
        this.text = text;
    }

    @Override
    public GeneralCardResult getResult() {
        List<MLText.Block> blocks = text.getBlocks();
        if (blocks.isEmpty()) {
            Log.i(TAG, "Result blocks is empty");
            return null;
        }

        ArrayList<GeneralCardBlockItem> originItems = hkGetOriginItems(blocks);

        String valid = "";
        String number = "";
        boolean numberFlag = false;
        boolean validFlag = false;

        int location = 1;
        for (GeneralCardBlockItem item : originItems) {
            String tempStr = item.text;

            if (!validFlag && (originItems.size() - location) < 3) {
                String result = tryGetValidDate(tempStr);
                if (!result.isEmpty()) {
                    valid = result;
                    validFlag = true;
                }
            }

            if (!numberFlag) {
                String result = tryGetCardNumber(tempStr);
                if (!result.isEmpty()) {
                    number = result;
                    numberFlag = true;
                }
            }
            location++;
        }

        Log.i(TAG, "valid: " + valid);
        Log.i(TAG, "number: " + number);

        return new GeneralCardResult(valid, number);
    }

    private String tryGetValidDate(String originStr) {
        int[] formatter = {2, 2, 2};
        return CardStringUtils.getCorrectDate(originStr, "\\-", formatter);
    }

    private String tryGetCardNumber(String originStr) {
        return CardStringUtils.getHKIdCardNum(originStr);
    }

    private ArrayList<GeneralCardBlockItem> hkGetOriginItems(List<MLText.Block> blocks) {
        ArrayList<GeneralCardBlockItem> originItems = new ArrayList<>();
        for (MLText.Block block : blocks) {
            // Add in behavior units
            List<MLText.TextLine> lines = block.getContents();
            for (MLText.TextLine line : lines) {
                String hkText = line.getStringValue();
                hkText = CardStringUtils.filterString(hkText, "[^a-zA-Z0-9\\.\\-,<\\(\\)\\s]");
                Log.d(TAG, "text: " + text);
                Point[] points = line.getVertexes();
                Rect rect = new Rect(points[0].x, points[0].y, points[2].x, points[2].y);
                GeneralCardBlockItem item = new GeneralCardBlockItem(hkText, rect);
                originItems.add(item);
            }
        }
        return originItems;
    }
}
