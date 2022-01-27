/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.impl;

import android.widget.CheckBox;

import com.genar.hmssandbox.huawei.feature_modem5g_kit.R;
import com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.base.BaseActivity;

import butterknife.BindView;

public abstract class HmsKitBaseActivity extends BaseActivity {
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
    @BindView(R.id.nr_scinfo)
    CheckBox nrServCellInfoCbox;
    @BindView(R.id.nr_sc_arfcn)
    CheckBox nrServCellInfoSsbArfchCbox;
    @BindView(R.id.nr_sc_pci)
    CheckBox nrServCellInfoPhyCellIdCbox;
    @BindView(R.id.nr_sc_band)
    CheckBox nrServCellInfoBandCbox;
    @BindView(R.id.nr_sc_cgt)
    CheckBox nrServCellInfoCgTypeCbox;
    @BindView(R.id.nr_sc_ct)
    CheckBox nrServCellInfoCellTypeCbox;
    @BindView(R.id.nr_sc_st)
    CheckBox nrServCellInfoScsTypeCbox;
    @BindView(R.id.nr_sc_dml)
    CheckBox nrServCellInfoDlMimoLayersCbox;
    @BindView(R.id.nr_sc_dt)
    CheckBox nrServCellInfoDssTypeCbox;
    @BindView(R.id.nr_sc_srp)
    CheckBox nrServCellInfoSsbRsrpCbox;
    @BindView(R.id.nr_sc_srq)
    CheckBox nrServCellInfoSsbRsrqCbox;
    @BindView(R.id.nr_sc_ssr)
    CheckBox nrServCellInfoSsbSinrCbox;
    @BindView(R.id.nr_sc_crp)
    CheckBox nrServCellInfoCsiRsrpCbox;
    @BindView(R.id.nr_sc_crq)
    CheckBox nrServCellInfoCsiRsrqCbox;
    @BindView(R.id.nr_sc_csr)
    CheckBox nrServCellInfoCsiSinrCbox;
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
}
