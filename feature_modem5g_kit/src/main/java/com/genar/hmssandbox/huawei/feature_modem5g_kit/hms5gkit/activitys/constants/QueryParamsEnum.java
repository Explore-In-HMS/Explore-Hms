/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.constants;


import com.genar.hmssandbox.huawei.feature_modem5g_kit.R;
import com.huawei.hms5gkit.agentservice.constants.parameters.Bearer;
import com.huawei.hms5gkit.agentservice.constants.parameters.Lte;
import com.huawei.hms5gkit.agentservice.constants.parameters.ModemSlice;
import com.huawei.hms5gkit.agentservice.constants.parameters.Nr;
import com.huawei.hms5gkit.agentservice.constants.parameters.NetDiagnosis;
import com.huawei.hms5gkit.agentservice.constants.parameters.FailureEvent;


import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum QueryParamsEnum {
    LTE_TYPE(R.id.lte_cb, Lte.LTE),
    LTE_TYPE_ARFCN(R.id.lte_arfcn_cb, Lte.LTE_ARFCN),
    LTE_TYPE_PHYCELLID(R.id.lte_phyCellId_cb, Lte.LTE_PHYCELLID),
    LTE_TYPE_DLFREQ(R.id.lte_dlFreq_cb, Lte.LTE_DLFREQ),
    LTE_TYPE_BAND(R.id.lte_band, Lte.LTE_BAND),
    LTE_TYPE_MIMO(R.id.lte_mimo, Lte.LTE_MIMO),
    LTE_TYPE_DLBANDWIDTH(R.id.lte_dbw, Lte.LTE_DL_BANDWIDTH),
    LTE_TYPE_LTEMODETYPE(R.id.lte_lmt, Lte.LTE_LTE_MODE_TYPE),
    LTE_TYPE_TRACKAREACODE(R.id.lte_tac, Lte.LTE_TRACK_AREA_CODE),
    LTE_TYPE_CELLIDENTITY(R.id.lte_cid, Lte.LTE_CELL_IDENTITY),
    LTE_TYPE_MCC(R.id.lte_mcc, Lte.LTE_MCC),
    LTE_TYPE_MNC(R.id.lte_mnc, Lte.LTE_MNC),
    LTE_TYPE_MEASCELL(R.id.lte_mcell, Lte.LTE_INTRA_EUTRA_CELL_MEAS_INFO),
    LTE_TYPE_MEASCELL_CELLID(R.id.lte_m_cid, Lte.LTE_INTRA_EUTRA_CELL_MEAS_INFO_CELLID),
    LTE_TYPE_MEASCELL_RSRP(R.id.lte_m_rsrp, Lte.LTE_INTRA_EUTRA_CELL_MEAS_INFO_RSRP),
    LTE_TYPE_MEASCELL_RSRQ(R.id.lte_m_rsrq, Lte.LTE_INTRA_EUTRA_CELL_MEAS_INFO_RSRQ),
    LTE_TYPE_MEASCELL_SINR(R.id.lte_m_sinr, Lte.LTE_INTRA_EUTRA_CELL_MEAS_INFO_SINR),
    LTE_TYPE_SCELL(R.id.lte_scell, Lte.LTE_SCELL),
    LTE_TYPE_SCELL_ARFCN(R.id.lte_s_arfcn, Lte.LTE_SCELL_arfcn),
    LTE_TYPE_SCELL_PHYCELLID(R.id.lte_s_pid, Lte.LTE_SCELL_phyCellId),
    LTE_TYPE_SCELL_DLFREQ(R.id.lte_s_df, Lte.LTE_SCELL_dlFreq),
    LTE_TYPE_SCELL_BAND(R.id.lte_s_band, Lte.LTE_SCELL_band),
    LTE_TYPE_SCELL_MIMO(R.id.lte_s_mimo, Lte.LTE_SCELL_mimo),
    LTE_TYPE_SCELL_DLBANDWIDTH(R.id.lte_s_dbw, Lte.LTE_SCELL_dlBandWidth),
    LTE_TYPE_SCELL_RSRP(R.id.lte_s_rsrp, Lte.LTE_SCELL_rsrp),
    LTE_TYPE_SCELL_RSRQ(R.id.lte_s_rsrq, Lte.LTE_SCELL_rsrq),
    LTE_TYPE_SCELL_SINR(R.id.lte_s_sinr, Lte.LTE_SCELL_sinr),

    //Old way
    /*NR_TYPE(R.id.nr_cb, Nr.NR),
    NR_TYPE_SERVCELLINFO(R.id.nr_scinfo, Nr.NR_SERV_CELL_INFO),
    NR_TYPE_SERVCELLINFO_SSBARFCN(R.id.nr_sc_arfcn, Nr.NR_SSB_ARFCN),
    NR_TYPE_SERVCELLINFO_PHYCELLID(R.id.nr_sc_pci, Nr.NR_PHY_CELL_ID),
    NR_TYPE_SERVCELLINFO_BAND(R.id.nr_sc_band, Nr.NR_BAND),
    NR_TYPE_SERVCELLINFO_CGTYPE(R.id.nr_sc_cgt, Nr.NR_CGTYPE),
    NR_TYPE_SERVCELLINFO_CELLTYPE(R.id.nr_sc_ct, Nr.NR_CELL_TYPE),
    NR_TYPE_SERVCELLINFO_SCSTYPE(R.id.nr_sc_st, Nr.NR_SCS_TYPE),
    NR_TYPE_SERVCELLINFO_DLMIMOLAYERS(R.id.nr_sc_dml, Nr.NR_DLMIMO_LAYERS),
    NR_TYPE_SERVCELLINFO_DSSTYPE(R.id.nr_sc_dt, Nr.NR_DSS_TYPE),
    NR_TYPE_SERVCELLINFO_SSBRSRP(R.id.nr_sc_srp, Nr.NR_SSB_RSRP),
    NR_TYPE_SERVCELLINFO_SSBRSRQ(R.id.nr_sc_srq, Nr.NR_SSB_RSRQ),
    NR_TYPE_SERVCELLINFO_SSBSINR(R.id.nr_sc_ssr, Nr.NR_SSB_SINR),
    NR_TYPE_SERVCELLINFO_CSIRSRP(R.id.nr_sc_crp, Nr.NR_CSI_RSRP),
    NR_TYPE_SERVCELLINFO_CSIRSRQ(R.id.nr_sc_crq, Nr.NR_CSI_RSRQ),
    NR_TYPE_SERVCELLINFO_CSISINR(R.id.nr_sc_csr, Nr.NR_CSI_SINR),*/

    NR_TYPE(R.id.nr_cb, Nr.NR),
    NR_TYPE_SPCELL_INFO(R.id.nr_spcell_info, Nr.NR_SPCELL_INFO),
    NR_TYPE_SCELL_INFO(R.id.nr_scell_info, Nr.NR_SCELL_INFO),
    NR_TYPE_SPCELL_BASIC(R.id.nr_spcell_basic, Nr.NR_SPCELL_BASIC),
    NR_TYPE_SPCELL_CFG(R.id.nr_spcell_cfg, Nr.NR_SPCELL_CFG),
    NR_TYPE_SPCELL_MEAS(R.id.nr_spcell_meas, Nr.NR_SPCELL_MEAS),
    NR_TYPE_SCELL_BASIC(R.id.nr_scell_basic, Nr.NR_SCELL_BASIC),
    NR_TYPE_SCELL_CFG(R.id.nr_scell_cfg, Nr.NR_SCELL_CFG),
    NR_TYPE_SCELL_SSB_MEAS(R.id.nr_scell_ssb_meas, Nr.NR_SCELL_SSB_MEAS),

    NET_TYPE(R.id.net, NetDiagnosis.NET),
    NET_TYPE_LTE_INFO(R.id.net_lte_info, NetDiagnosis.NET_LTE_INFO),
    NET_TYPE_NR_INFO(R.id.net_nr_info, NetDiagnosis.NET_NR_INFO),
    NET_TYPE_LTE_REJ_CNT(R.id.net_lte_rej_cnt, NetDiagnosis.NET_LTE_REJ_CNT),
    NET_TYPE_LTE_REJ_INFOS(R.id.net_lte_rej_infos, NetDiagnosis.NET_LTE_REJ_INFOS),
    NET_TYPE_LTE_PDN_REJ_CNT(R.id.net_lte_pdn_rej_cnt, NetDiagnosis.NET_LTE_PDN_REJ_CNT),
    NET_TYPE_LTE_PDN_REJ_INFOS(R.id.net_lte_pdn_rej_infos, NetDiagnosis.NET_LTE_PDN_REJ_INFOS),
    NET_TYPE_LTE_AMBR_CNT(R.id.net_lte_ambr_cnt, NetDiagnosis.NET_LTE_AMBR_CNT),
    NET_TYPE_NR_REJ_INFO(R.id.net_nr_rej_info, NetDiagnosis.NET_NR_REJ_INFO),
    NET_TYPE_NR_PDU_REJ_CNT(R.id.net_nr_pdu_rej_cnt, NetDiagnosis.NET_NR_PDU_REJ_CNT),
    NET_TYPE_NR_PDU_REJ_INFO(R.id.net_nr_pdu_rej_info, NetDiagnosis.NET_NR_PDU_REJ_INFO),
    NET_TYPE_NR_AMBR_CNT(R.id.net_nr_ambr_cnt, NetDiagnosis.NET_NR_AMBR_CNT),
    NET_TYPE_NR_AMBR(R.id.net_nr_ambr, NetDiagnosis.NET_NR_AMBR),



    BEARER_TYPE(R.id.bearer_cb, Bearer.BEARER),
    BEARER_TYPE_DRBINFO(R.id.bearer_dinfo, Bearer.BEARER_DRB_INFO),
    BEARER_TYPE_DRBINFO_RBID(R.id.bearer_d_rbid, Bearer.BEARER_RBID),
    BEARER_TYPE_DRBINFO_PDCPVERSION(R.id.bearer_d_pver, Bearer.BEARER_PDCP_VERSION),
    BEARER_TYPE_DRBINFO_BEARERTYPE(R.id.bearer_d_btype, Bearer.BEARER_BEARER_TYPE),
    BEARER_TYPE_DRBINFO_DATASPLITTHRESHOLD(R.id.bearer_d_dst, Bearer.BEARER_DATA_SPLIT_THRESHOLD),



    MODEM_SLICE(R.id.modem_slice, ModemSlice.MODEM_SLICE);

    private int resourceId; // Checkbox resource id
    private String queryName; // Request parameter

    private static Map<Integer, String> resourceId2QueryNameMap
            = Arrays.stream(QueryParamsEnum.values())
            .collect(Collectors.toMap(QueryParamsEnum::getResourceId, QueryParamsEnum::getQueryName));

    QueryParamsEnum(int resourceId, String queryName) {
        this.resourceId = resourceId;
        this.queryName = queryName;
    }

    public static Map<Integer, String> getResourceId2QueryNameMap() {
        return resourceId2QueryNameMap;
    }

    private int getResourceId() {
        return resourceId;
    }

    private String getQueryName() {
        return queryName;
    }
}
