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

package com.hms.explorehms.huawei.feature_imagekit.ui.interfaces;

import com.hms.explorehms.huawei.feature_imagekit.model.TokenResponseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIServiceImageKit {

    @Headers({
            "Charset: UTF-8"
    })
    @FormUrlEncoded
    @POST("oauth2/v2/token")
    Call<TokenResponseModel> getToken(@Field("grant_type") String grantType,@Field("client_id") String clientId,@Field("client_secret") String clientSecret);
}

