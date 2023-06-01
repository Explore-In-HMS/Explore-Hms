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

package com.hms.explorehms.huawei.feature_networkkit;

import com.huawei.hms.network.httpclient.Submit;
import com.huawei.hms.network.restclient.anno.Body;
import com.huawei.hms.network.restclient.anno.DELETE;
import com.huawei.hms.network.restclient.anno.Field;
import com.huawei.hms.network.restclient.anno.FieldMap;
import com.huawei.hms.network.restclient.anno.FormUrlEncoded;
import com.huawei.hms.network.restclient.anno.GET;
import com.huawei.hms.network.restclient.anno.HEAD;
import com.huawei.hms.network.restclient.anno.Header;
import com.huawei.hms.network.restclient.anno.HeaderMap;
import com.huawei.hms.network.restclient.anno.Headers;
import com.huawei.hms.network.restclient.anno.POST;
import com.huawei.hms.network.restclient.anno.PUT;
import com.huawei.hms.network.restclient.anno.Query;
import com.huawei.hms.network.restclient.anno.QueryMap;
import com.huawei.hms.network.restclient.anno.Url;

import java.util.HashMap;
import java.util.Map;


public interface SampleService {

    @GET
    Submit<String> getTest(@Url String url);

    /**
     * POST test API
     *
     * @param url     Request URL.
     * @param headers Request header information.
     * @param body    Request body.
     * @return Submit<T> object.
     */
    @POST
    Submit<String> post(@Url String url, @HeaderMap HashMap<String, String> headers, @Body String body);

    /**
     * PUT test API
     *
     * @param url Request URL.
     * @return Submit<T> object.
     */
    @PUT
    Submit<String> put(@Url String url);

    /**
     * DELETE test API
     *
     * @param url Request URL.
     * @return Submit<T> object.
     */
    @DELETE
    Submit<String> delete(@Url String url);

    /**
     * HEAD test API
     *
     * @param url Request URL.
     * @return Submit<T> object.
     */
    @HEAD
    Submit<Void> head(@Url String url);

    /**
     * POST test API
     *
     * @param url   Request URL.
     * @param name  Table name.
     * @param value Table value.
     * @return Submit<T> object.
     */
    @POST
    @FormUrlEncoded
    Submit<String> postField(@Url String url, @Field("name") String name, @Field("value") String value);

    /**
     * POST test API
     *
     * @param url   Request URL.
     * @param forms Table map.
     * @return Submit<T> object.
     */
    @POST
    @FormUrlEncoded
    Submit<String> postFieldMap(@Url String url, @FieldMap Map<String, Object> forms);

    /**
     * POST test API
     *
     * @param url  Request URL.
     * @param num  Request header information.
     * @param data Request body.
     * @return Submit<T> object.
     */
    @POST
    Submit<String> postStringWithHead(@Url String url, @Header("copynum") int num, @Body String data);

    /**
     * POST test API
     *
     * @param url  Request URL.
     * @param data Request body.
     * @return Submit<T> object.
     */
    @POST
    @Headers({"header1:3", "header2:3"})
    Submit<String> postStringWithHeaders(@Url String url, @Body String data);

    /**
     * POST test API
     *
     * @param url     Request URL.
     * @param headers Request header information.
     * @param data    Request body.
     * @return Submit<T> object.
     */
    @POST
    Submit<String> postStringWithHeaderMap(
            @Url String url, @HeaderMap Map<String, String> headers, @Body String data);

    /**
     * GET test API
     *
     * @param url    Request URL.
     * @param idname The value of the request parameter id is idname.
     * @return Submit<T> object.
     */
    @GET
    Submit<String> getQuery(@Url String url, @Query("id") String idname);

    /**
     * GET test API
     *
     * @param url    Request URL.
     * @param querys Request parameters encapsulated using Map.
     * @return Submit<T> object.
     */
    @GET
    Submit<String> getQueryMap(@Url String url, @QueryMap Map<String, String> querys);
}
