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
package com.hms.explorehms.huawei.feature_remoteconfig.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_remoteconfig.R;
import com.hms.explorehms.huawei.feature_remoteconfig.model.RemoteResult;

import java.util.List;

public class RemoteConfigAdaptor extends RecyclerView.Adapter<RemoteConfigAdaptor.ViewHolder> {

     List<RemoteResult>remoteConfigResult;
     TextView txtVwRemoteK;
     TextView txtVwRemoteVal;

    public RemoteConfigAdaptor(List<RemoteResult> allValues){
        remoteConfigResult=allValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtVwRemoteK = itemView.findViewById(R.id.txtVwRemoteKey);
            txtVwRemoteVal= itemView.findViewById(R.id.txtVwRemoteValue);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.remote_result_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        txtVwRemoteK.setText(remoteConfigResult.get(position).getObjectKey());
        txtVwRemoteVal.setText(remoteConfigResult.get(position).getObjectValue());
    }

    @Override
    public int getItemCount() {
        return remoteConfigResult.size();
    }


}
