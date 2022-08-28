/*
 * Copyright 2020. Explore in HMS. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.hms.explorehms.mapkit.myEnums;


public enum DirectionActionType {
    STRAIGHT("straight"),
    TURN_LEFT("turn-left"),
    TURN_RIGHT("turn-right"),
    TURN_SLIGHT_LEFT("turn-slight-left"),
    TURN_SLIGHT_RIGHT("turn-slight-right"),
    TURN_SHARP_LEFT("turn-sharp-left"),
    TURN_SHARP_RIGHT("turn-sharp-right"),
    UTURN_LEFT("uturn-left"),
    UTURN_RIGHT("uturn-right"),
    RAMP_LEFT("ramp-left"),
    RAMP_RIGHT("ramp-right"),
    MERGE("merge"),
    FORK_LEFT("fork-left"),
    FORK_RIGHT("fork-right"),
    FERRY ("ferry"),
    FERRY_TRAIN ("ferry-train"),
    ROUNDABOUT_LEFT("roundabout-left"),
    ROUNDABOUT_RIGHT("roundabout-right"),
    END("end"),
    UNKOWN("unknown");

    private final String type;

    public final String getType() {
        return this.type;
    }

    DirectionActionType(String type) {
        this.type = type;
    }
}
