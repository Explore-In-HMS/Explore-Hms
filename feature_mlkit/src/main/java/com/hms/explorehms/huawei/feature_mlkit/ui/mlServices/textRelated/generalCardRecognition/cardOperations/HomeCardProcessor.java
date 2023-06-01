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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.generalCardRecognition.cardOperations;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.huawei.hms.mlsdk.text.MLText;

import java.util.ArrayList;
import java.util.List;

/**
 * Post processing plug-in of Mainland Travel Permit for Hong Kong、Macao and Taiwan residents
 */
public class HomeCardProcessor implements GeneralCardProcessor {
    private static final String TAG = HomeCardProcessor.class.getSimpleName();

    private final MLText text;

    public HomeCardProcessor(MLText text) {
        this.text = text;
    }

    @Override
    public GeneralCardResult getResult() {
        List<MLText.Block> blocks = text.getBlocks();
        if (blocks.isEmpty()) {
            Log.i(TAG, "Result blocks is empty");
            return null;
        }

        ArrayList<GeneralCardBlockItem> homeOriginItems = homeGetOriginItems(blocks);

        String valid = "";
        String number = "";
        boolean numberFlag = false;
        boolean validFlag = false;

        for (GeneralCardBlockItem item : homeOriginItems) {
            String homeTempStr = item.text;

            if (!validFlag) {
                String homeResult = tryGetValidDate(homeTempStr);
                if (!homeResult.isEmpty()) {
                    valid = homeResult;
                    validFlag = true;
                }
            }

            if (!numberFlag) {
                String homeResult = tryGetCardNumber(homeTempStr);
                if (!homeResult.isEmpty()) {
                    number = homeResult;
                    numberFlag = true;
                }
            }
        }

        Log.i(TAG, "valid: " + valid);
        Log.i(TAG, "number: " + number);

        return new GeneralCardResult(valid, number);
    }

    private ArrayList<GeneralCardBlockItem> homeGetOriginItems(List<MLText.Block> blocks) {
        ArrayList<GeneralCardBlockItem> originItems = new ArrayList<>();

        for (MLText.Block block : blocks) {
            // Add in behavior units
            List<MLText.TextLine> lines = block.getContents();
            for (MLText.TextLine line : lines) {
                String homeText = line.getStringValue();
                homeText = CardStringUtils.filterString(homeText, "[^a-zA-Z0-9\\.\\-,<\\(\\)\\s]");
                Log.d(TAG, "text: " + homeText);
                Point[] points = line.getVertexes();
                Rect rect = new Rect(points[0].x, points[0].y, points[2].x, points[2].y);
                GeneralCardBlockItem item = new GeneralCardBlockItem(homeText, rect);
                originItems.add(item);
            }
        }
        return originItems;
    }

    private String tryGetValidDate(String originStr) {
        return CardStringUtils.getCorrectValidDate(originStr);
    }

    private String tryGetCardNumber(String originStr) {
        return CardStringUtils.getHomeCardNumber(originStr);
    }
}