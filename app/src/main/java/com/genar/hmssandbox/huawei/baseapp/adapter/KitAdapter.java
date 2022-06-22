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

package com.genar.hmssandbox.huawei.baseapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.baseapp.listeners.IDownloadListener;
import com.genar.hmssandbox.huawei.baseapp.model.KitModel;
import com.huawei.hms.common.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KitAdapter extends RecyclerView.Adapter<KitAdapter.KitViewHolder> implements IDownloadListener {

    private static final String TAG = "KitRecyclerViewAdapter";

    Activity activity;
    List<KitModel> kitList;

    public KitAdapter(Activity activity, List<KitModel> kitList){
        this.activity = activity;
        this.kitList = SortKitList(kitList);
    }

    @NonNull
    @Override
    public KitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.rv_kit_item,parent, false);

        return new KitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KitViewHolder holder, int position) {

        KitModel kit = kitList.get(position);

        holder.bind(kit, this::onSuccessfullyDownloaded);
    }

    @Override
    public int getItemCount() {
        return kitList.size();
    }

    public void updateList(List<KitModel> list){
        kitList = list;
        notifyDataSetChanged();
    }

    public static void setItemColorUnavailable(ImageView v) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
        v.setImageAlpha(128);   // 128 = 0.5
    }

    public static void setItemColorAvailable(ImageView v) {
        v.setColorFilter(null);
        v.setImageAlpha(255);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccessfullyDownloaded(int adapterPosition) {
        notifyItemChanged(adapterPosition);
        notifyDataSetChanged();
    }

    public class KitViewHolder extends RecyclerView.ViewHolder{
        KitModel kit;
        @BindView(R.id.cv_kit) CardView cv_container;
        @BindView(R.id.kitItem_image) ImageView iv_kitIcon;
        @BindView(R.id.tv_name) TextView tv_kitName;
        @BindView(R.id.iv_mode) ImageView ivMode;

        KitViewHolder(View itemView){
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(KitModel kit, IDownloadListener listener){
            this.kit = kit;

            if(kit.getMode() != null){
                if(kit.getMode() == KitModel.KitMode.REFERENCE){
                    ivMode.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_reference));
                }
            }else{
                ivMode.setImageDrawable(null);
            }

            iv_kitIcon.setImageDrawable(ContextCompat.getDrawable(activity, kit.getKitIconResource()));
            tv_kitName.setText(kit.getKitName());

            if (kit.isDynamicFeature()) {
                if (!Util.isFeatureInstalled(activity, kit.getFeatureName())) {
                    setItemColorUnavailable(iv_kitIcon);
                    cv_container.setOnClickListener(v -> Util.showFeatureInstallDialog(activity, kit, KitViewHolder.this, listener));
                } else {
                    setItemColorAvailable(iv_kitIcon);
                    cv_container.setOnClickListener(v -> {
                        try {
                            activity.startActivity(new Intent(activity, Class.forName(kit.getKitPackageName())));
                        } catch (ClassNotFoundException e) {
                            Logger.e(TAG, "Error:", e.toString());
                        }
                    });
                }
            }else{
                setItemColorAvailable(iv_kitIcon);
                cv_container.setOnClickListener(v -> {
                    try {
                        activity.startActivity(new Intent(activity, Class.forName(kit.getKitPackageName())));
                    } catch (ClassNotFoundException e) {
                        Logger.e(TAG, "Error:", e.toString());
                    }
                });
            }

            cv_container.setOnLongClickListener(v -> {
                if (kit.isDynamicFeature()) {
                    if(Util.isFeatureInstalled(activity, kit.getFeatureName())){
                        Util.showFeatureUninstallDialog(activity,kit,KitViewHolder.this);
                    }
                }else{
                    Util.showInfoDialog(activity,kit.getKitName() + " is a base module and can not be uninstalled!", true);
                }
                return false;
            });
        }

        public void refreshUI(){
            SortKitList(kitList);
            bind(kit,KitAdapter.this);
        }
    }

    private List<KitModel> SortKitList(List<KitModel> kitList){
        List<KitModel> availableKits = new ArrayList<>();
        List<KitModel> unavailableKits = new ArrayList<>();

        for (KitModel kit: kitList) {
            if(!kit.isDynamicFeature()){
                availableKits.add(kit);
            }else{
                if(Util.isFeatureInstalled(activity, kit.getFeatureName())){
                    availableKits.add(kit);
                }else{
                    unavailableKits.add(kit);
                }
            }
        }

        Collections.sort(availableKits, KitModel.KITNAME);
        Collections.sort(unavailableKits, KitModel.KITNAME);

        availableKits.addAll(unavailableKits);

        return availableKits;
    }
}
