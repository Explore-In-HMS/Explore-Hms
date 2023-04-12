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

package com.hms.explorehms.huawei.feature_modem5g_kit.hms5gkit.activitys.constants;

import com.hms.explorehms.huawei.feature_modem5g_kit.R;
import com.huawei.hms5gkit.agentservice.constants.parameters.FailureEvent;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum EventParamEnum {
    //EVENT_TYPE_FAILUREEVENT(R.id.fe, FailureEvent.FAILUREEVENT),
    EVENT_TYPE_FAILUREEVENT_SCG(R.id.fe_scg, FailureEvent.FAILUREEVENT_SCG),
    EVENT_TYPE_FAILUREEVENT_RACH(R.id.fe_rach, FailureEvent.FAILUREEVENT_RACH),
    EVENT_TYPE_FAILUREEVENT_RADIOLINK(R.id.fe_rl, FailureEvent.FAILUREEVENT_RADIOLINK),
    EVENT_TYPE_FAILUREEVENT_HANDOVER(R.id.fe_ho, FailureEvent.FAILUREEVENT_HANDOVER);

    private int resourceId; // Checkbox resource id
    private String eventName; // Request parameter
    public static int eventNum = 4;

    private static Map<Integer, String> resourceId2EventNameMap
            = Arrays.stream(EventParamEnum.values())
            .collect(Collectors.toMap(EventParamEnum::getResourceId, EventParamEnum::getEventName));

    EventParamEnum(int resourceId, String eventName) {
        this.resourceId = resourceId;
        this.eventName = eventName;
    }

    public static Map<Integer, String> getResourceId2EventNameMap() {
        return resourceId2EventNameMap;
    }

    public static List<String> hasAllEvent(List<String> selected)
    {
        List<String> tmp = new ArrayList<>();
        if (selected.size() == eventNum && selected.contains(FailureEvent.FAILUREEVENT_SCG)
                && selected.contains(FailureEvent.FAILUREEVENT_RACH)
                && selected.contains(FailureEvent.FAILUREEVENT_RADIOLINK)
                && selected.contains(FailureEvent.FAILUREEVENT_HANDOVER)) {
            tmp.add(FailureEvent.FAILUREEVENT);
            return tmp;
        }else {
            return selected;
        }
    }

    private int getResourceId() {
        return resourceId;
    }

    private String getEventName() {
        return eventName;
    }
}
