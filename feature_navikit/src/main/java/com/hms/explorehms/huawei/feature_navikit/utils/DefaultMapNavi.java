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

package com.hms.explorehms.huawei.feature_navikit.utils;


import com.huawei.hms.navi.navibase.MapNaviListener;
import com.huawei.hms.navi.navibase.enums.MapNaviRoutingTip;
import com.huawei.hms.navi.navibase.enums.MapNaviSettingEnums;
import com.huawei.hms.navi.navibase.model.FurnitureInfo;
import com.huawei.hms.navi.navibase.model.FutureEta;
import com.huawei.hms.navi.navibase.model.HighwayExitInfo;
import com.huawei.hms.navi.navibase.model.HistoryTrafficInfo;
import com.huawei.hms.navi.navibase.model.Incident;
import com.huawei.hms.navi.navibase.model.IntersectionNotice;
import com.huawei.hms.navi.navibase.model.JamBubble;
import com.huawei.hms.navi.navibase.model.LaneInfo;
import com.huawei.hms.navi.navibase.model.MapModelCross;
import com.huawei.hms.navi.navibase.model.MapNaviStaticInfo;
import com.huawei.hms.navi.navibase.model.MapNaviTurnPoint;
import com.huawei.hms.navi.navibase.model.MapServiceAreaInfo;
import com.huawei.hms.navi.navibase.model.NaviBroadInfo;
import com.huawei.hms.navi.navibase.model.NaviInfo;
import com.huawei.hms.navi.navibase.model.RouteChangeInfo;
import com.huawei.hms.navi.navibase.model.RouteRecommendInfo;
import com.huawei.hms.navi.navibase.model.SpeedInfo;
import com.huawei.hms.navi.navibase.model.TriggerNotice;
import com.huawei.hms.navi.navibase.model.TurnPointInfo;
import com.huawei.hms.navi.navibase.model.TypeOfTTSInfo;
import com.huawei.hms.navi.navibase.model.VoiceFailedResult;
import com.huawei.hms.navi.navibase.model.VoiceResult;
import com.huawei.hms.navi.navibase.model.ZoomPoint;
import com.huawei.hms.navi.navibase.model.bus.BusNaviPathBean;
import com.huawei.hms.navi.navibase.model.currenttimebusinfo.CurrentBusInfo;
import com.huawei.hms.navi.navibase.model.locationstruct.NaviLocation;

import java.util.List;
import java.util.Map;

/**
 * Default MapNaviListener inplements.
 */
public class DefaultMapNavi implements MapNaviListener {

    @Override
    public void onArriveDestination(MapNaviStaticInfo mapNaviStaticInfo) {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints, MapNaviRoutingTip mapNaviRoutingTip) {

    }

    @Override
    public void onCalBackupGuideSuccess(RouteChangeInfo routeChangeInfo) {

    }

    @Override
    public void onCalBackupGuideFail() {

    }

    @Override
    public void onParallelSwitchFail() {

    }

    @Override
    public void onParallelSwitchSuccess(RouteChangeInfo routeChangeInfo) {

    }

    @Override
    public void onCalculateWalkRouteFailure(int i) {

    }

    @Override
    public void onCalculateWalkRouteSuccess(int[] ints, MapNaviRoutingTip mapNaviRoutingTip) {

    }

    @Override
    public void onCalculateCycleRouteFailure(int i) {

    }

    @Override
    public void onCalculateCycleRouteSuccess(int[] ints, MapNaviRoutingTip mapNaviRoutingTip) {

    }

    @Override
    public void onGetNavigationText(NaviBroadInfo naviBroadInfo) {

    }

    @Override
    public void onLocationChange(NaviLocation naviLocation) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onLineLimitSpeedUpdate(SpeedInfo speedInfo) {

    }

    @Override
    public void onServiceAreaUpdate(MapServiceAreaInfo[] mapServiceAreaInfos) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onDriveRoutesChanged() {

    }

    @Override
    public void onExpiredBackupRoute(List<Integer> list) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onStartNaviSuccess() {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLaneInfoHide() {

    }

    @Override
    public void onLaneInfoShow(LaneInfo laneInfo) {

    }

    @Override
    public void onCrossHide() {

    }

    @Override
    public void onCrossShow(IntersectionNotice intersectionNotice) {

    }

    @Override
    public void onFullScreenGuideHide() {

    }

    @Override
    public void onFullScreenGuideShow(TriggerNotice triggerNotice) {

    }

    @Override
    public void onModeCrossShow(MapModelCross mapModelCross) {

    }

    @Override
    public void onModeCrossHide() {

    }

    @Override
    public void onTurnInfoUpdated(MapNaviTurnPoint mapNaviTurnPoint) {

    }

    @Override
    public void onAutoZoomUpdate(ZoomPoint zoomPoint) {

    }

    @Override
    public void onFurnitureInfoUpdate(FurnitureInfo[] furnitureInfos) {

    }

    @Override
    public void onCalcuBusDriveRouteSuccess(BusNaviPathBean busNaviPathBean) {

    }

    @Override
    public void onCalcuBusDriveRouteFailed(int i) {

    }

    @Override
    public void onGetRealTimeBusInfoSuccess(CurrentBusInfo currentBusInfo) {

    }

    @Override
    public void onGetRealTimeBusInfoFailed(int i) {

    }

    @Override
    public void onJamBubbleInfo(JamBubble jamBubble) {

    }

    @Override
    public void onGetMilestoneDisappear(int i) {

    }

    @Override
    public void onSendLocationSuccess() {

    }

    @Override
    public void onSendLocationFailed() {

    }

    @Override
    public void getVoiceByteSuccess(VoiceResult voiceResult) {

    }

    @Override
    public void getVoiceByteFailed(VoiceFailedResult voiceFailedResult) {

    }

    @Override
    public void getAccessTypeOfTTSSuccess(TypeOfTTSInfo typeOfTTSInfo) {

    }

    @Override
    public void getAccessTypeOfTTSFailed(int i) {

    }

    @Override
    public void onGetFutureEtaSuccess(List<FutureEta> list) {

    }

    @Override
    public void onGetFutureEtaFailed(int i) {

    }

    @Override
    public void onGetHistoryTrafficSuccess(List<HistoryTrafficInfo> list) {

    }

    @Override
    public void onGetHistoryTrafficFailed(int i) {

    }

    @Override
    public void onIncidentUpdate(Incident incident) {

    }

    @Override
    public void onEnterTunnel() {

    }

    @Override
    public void onLeaveTunnel() {

    }

    @Override
    public void onSpecialTurnPointHide(TurnPointInfo turnPointInfo) {

    }

    @Override
    public void onBusTakeoff(int i) {

    }

    @Override
    public void onBroadcastModeChangeSuccess(int i) {

    }

    @Override
    public void onBroadcastModeChangeFail(int i) {

    }

    @Override
    public void onHighwayExitPointUpdate(HighwayExitInfo highwayExitInfo) {

    }

    @Override
    public void onSetNaviSettingSuccess(Map<MapNaviSettingEnums, Object> map) {

    }

    @Override
    public void onSetNaviSettingFail(Map<MapNaviSettingEnums, Object> map) {

    }

    @Override
    public void onUpdateWaypointsSuccess(RouteChangeInfo routeChangeInfo) {

    }

    @Override
    public void onUpdateWaypointsFail() {

    }

    @Override
    public void onUpdateRouteSuccess(RouteChangeInfo routeChangeInfo) {

    }

    @Override
    public void onUpdateRouteFail() {

    }

    @Override
    public void onAuthenticationFail() {

    }

    @Override
    public void onRecommendBetterRoute(RouteRecommendInfo routeRecommendInfo) {

    }
}
