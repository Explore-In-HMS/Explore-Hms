/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.impl;

import android.widget.CheckBox;

import com.genar.hmssandbox.huawei.feature_modem5g_kit.R;
import com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.base.BaseActivity;

import butterknife.BindView;

public abstract class HmsKitBaseActivity extends BaseActivity {
    @BindView(R.id.fe_scg)
    CheckBox feScgCbox;
    @BindView(R.id.fe_rach)
    CheckBox feRachCbox;
    @BindView(R.id.fe_rl)
    CheckBox feRadioLinkCbox;
    @BindView(R.id.fe_ho)
    CheckBox feHandOverCbox;

    @BindView(R.id.lte_cb)
    CheckBox lteCbox;
    @BindView(R.id.lte_arfcn_cb)
    CheckBox lteArfcnCbox;
    @BindView(R.id.lte_phyCellId_cb)
    CheckBox ltePhyCellIdCbox;
    @BindView(R.id.lte_dlFreq_cb)
    CheckBox lteDlFreqCbox;
    @BindView(R.id.lte_band)
    CheckBox lteBandCbox;
    @BindView(R.id.lte_mimo)
    CheckBox lteMimoCbox;
    @BindView(R.id.lte_dbw)
    CheckBox lteDlBandWidthCbox;
    @BindView(R.id.lte_lmt)
    CheckBox lteModeTypeCbox;
    @BindView(R.id.lte_tac)
    CheckBox lteTrackAreaCodeCbox;
    @BindView(R.id.lte_cid)
    CheckBox lteCellIdentityCbox;
    @BindView(R.id.lte_mcc)
    CheckBox lteMccCbox;
    @BindView(R.id.lte_mnc)
    CheckBox lteMncCbox;
    @BindView(R.id.lte_mcell)
    CheckBox lteMeasCellCbox;
    @BindView(R.id.lte_m_cid)
    CheckBox lteMeasCellCellIdCbox;
    @BindView(R.id.lte_m_rsrp)
    CheckBox lteMeasCellRsrpCbox;
    @BindView(R.id.lte_m_rsrq)
    CheckBox lteMeasCellRsrqCbox;
    @BindView(R.id.lte_m_sinr)
    CheckBox lteMeasCellSinrCbox;
    @BindView(R.id.lte_scell)
    CheckBox lteScellCbox;
    @BindView(R.id.lte_s_arfcn)
    CheckBox lteScellArfcnCbox;
    @BindView(R.id.lte_s_pid)
    CheckBox lteScellPhyCellIdCbox;
    @BindView(R.id.lte_s_df)
    CheckBox lteScellDlFreqCbox;
    @BindView(R.id.lte_s_band)
    CheckBox lteScellBandCbox;
    @BindView(R.id.lte_s_mimo)
    CheckBox lteScellMimoCbox;
    @BindView(R.id.lte_s_dbw)
    CheckBox lteScellDlBandWidthCbox;
    @BindView(R.id.lte_s_rsrp)
    CheckBox lteScellRsrpCbox;
    @BindView(R.id.lte_s_rsrq)
    CheckBox lteScellRsrqCbox;
    @BindView(R.id.lte_s_sinr)
    CheckBox lteScellSinrCbox;
    @BindView(R.id.nr_cb)
    CheckBox nrCbox;
    @BindView(R.id.nr_spcell_info)
    CheckBox nrSpcellInfoCbox;
    @BindView(R.id.nr_scell_info)
    CheckBox nrScellInfoCbox;
    @BindView(R.id.nr_spcell_basic)
    CheckBox nrSpcellBasicCbox;
    @BindView(R.id.nr_spcell_cfg)
    CheckBox nrSpcellCfgCbox;
    @BindView(R.id.nr_spcell_meas)
    CheckBox nrSpcellMeasCbox;
    @BindView(R.id.nr_scell_basic)
    CheckBox nrScellBasicCbox;
    @BindView(R.id.nr_scell_cfg)
    CheckBox nrScellCfgCbox;
    @BindView(R.id.nr_scell_ssb_meas)
    CheckBox nrScellSsbMeasCbox;
    @BindView(R.id.net)
    CheckBox netCbox;
    @BindView(R.id.net_lte_info)
    CheckBox netLteInfoCbox;
    @BindView(R.id.net_nr_info)
    CheckBox netNrInfoCbox;
    @BindView(R.id.net_lte_rej_cnt)
    CheckBox netLteRejCntCbox;
    @BindView(R.id.net_lte_rej_infos)
    CheckBox netLteRejInfosCbox;
    @BindView(R.id.net_lte_pdn_rej_cnt)
    CheckBox netLtePdnRejCntCbox;
    @BindView(R.id.net_lte_pdn_rej_infos)
    CheckBox netLtePdnRejInfosCbox;
    @BindView(R.id.net_lte_ambr_cnt)
    CheckBox netLteAmbrCntCbox;
    @BindView(R.id.net_lte_ambrs)
    CheckBox netLteAmbrsCbox;
    @BindView(R.id.net_nr_rej_cnt)
    CheckBox netNrRejCntCbox;
    @BindView(R.id.net_nr_rej_info)
    CheckBox netNrRejInfoCbox;
    @BindView(R.id.net_nr_pdu_rej_cnt)
    CheckBox netNrPduRejCntCbox;
    @BindView(R.id.net_nr_pdu_rej_info)
    CheckBox netNrPduRejInfoCbox;
    @BindView(R.id.net_nr_ambr_cnt)
    CheckBox netNrAmbrCntCbox;
    @BindView(R.id.net_nr_ambr)
    CheckBox netNrAmbrCbox;


    @BindView(R.id.bearer_cb)
    CheckBox bearerCbox;
    @BindView(R.id.bearer_dinfo)
    CheckBox bearerDrbInfoCbox;
    @BindView(R.id.bearer_d_rbid)
    CheckBox bearerDrbInfoRbIdCbox;
    @BindView(R.id.bearer_d_pver)
    CheckBox bearerDrbInfoPdcpVersionCbox;
    @BindView(R.id.bearer_d_btype)
    CheckBox bearerDrbInfoBearerTypeCbox;
    @BindView(R.id.bearer_d_dst)
    CheckBox bearerDrbInfoDataSplitThresholdCbox;

    @BindView(R.id.modem_slice)
    CheckBox modemsliceCbox;
}
