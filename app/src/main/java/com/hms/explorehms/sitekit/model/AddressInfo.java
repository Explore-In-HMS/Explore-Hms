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

package com.hms.explorehms.sitekit.model;


import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.Coordinate;

import java.io.Serializable;

public class AddressInfo implements Serializable {

    
    private String name;
    private String siteId;
    private Coordinate latLong;
    private AddressDetail addressDetail;

    public AddressInfo(String name, String siteId, Coordinate latLong, AddressDetail addressDetail){
        this.name= name;
        this.siteId= siteId;
        this.addressDetail = addressDetail;
        this.latLong= latLong;
    }

    public String getName() {
        return name;
    }

    public String getSiteId() {
        return siteId;
    }

    public Coordinate getLatLong() {
        return latLong;
    }

    public AddressDetail getAddressDetail() {
        return addressDetail;
    }


}
