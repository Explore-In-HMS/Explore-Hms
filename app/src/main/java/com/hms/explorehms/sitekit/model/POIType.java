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

import com.hms.explorehms.R;
import com.huawei.hms.site.api.model.LocationType;

public enum  POIType {
    ALL(null, R.string.nearby_search_poi_all),
    CAFE(LocationType.CAFE, R.string.nearby_search_poi_cafe),
    RESTAURANT(LocationType.RESTAURANT, R.string.nearby_search_poi_restaurant),
    SHOPPING_MALL(LocationType.SHOPPING_MALL, R.string.nearby_search_poi_shopping_mall),
    ADDRESS(LocationType.ADDRESS, R.string.nearby_search_poi_address),
    DRUG_STORE(LocationType.DRUGSTORE, R.string.nearby_search_poi_drug_store),
    HOSPITAL(LocationType.HOSPITAL, R.string.nearby_search_poi_type_hospital),
    BANK(LocationType.BANK, R.string.nearby_search_poi_type_bank);

    private LocationType locationType;
    private int stringVal;
    POIType(LocationType locationType, int stringVal) {
        this.locationType = locationType;
        this.stringVal = stringVal;
    }

    public int getStringVal(){
        return stringVal;
    }

    public LocationType getLocationType(){
        return locationType;
    }

}
