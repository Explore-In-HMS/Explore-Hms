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

package com.hms.explorehms.huawei.feature_cloudfunctions.model;

/**
 * It is an enum class that handles method types to using them on functions.
 */
public enum MethodTypes {
    SUM("sum", "+"),
    SUB("sub", "-"),
    MUL("mul", "*"),
    DIV("div", "/");

    private String methodType;
    private String methodValue;

    MethodTypes(String methodType, String methodValue) {
        this.methodType = methodType;
        this.methodValue = methodValue;
    }

    /**
     * It returns method Type.
     */
    public String getMethodType() {
        return methodType;
    }

    /**
     * It returns method value.
     */
    public String getMethodValue() {
        return methodValue;
    }

}