/**
 * Copyright 2021. Explore in HMS. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.genar.hmssandbox.huawei.modelingkit3d.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.modelingkit3d.model.UserBean;
import com.genar.hmssandbox.huawei.modelingkit3d.ui.fragment.HomeFragment;
import com.genar.hmssandbox.huawei.modelingkit3d.utils.BaseUtils;
import com.genar.hmssandbox.huawei.modelingresource.db.DatabaseAppUtils;
import com.genar.hmssandbox.huawei.modelingresource.materialdb.DatabaseMaterialAppUtils;
import com.huawei.hms.materialgeneratesdk.MaterialGenApplication;
import com.huawei.hms.objreconstructsdk.ReconstructApplication;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @Description: Home page
 * @Since: 2021-04-16
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks , HomeFragment.AskPermissionInterface {

    HomeFragment homeFragment;

    private static final int RC_CAMERA_AND_EXTERNAL_STORAGE = 0x01 << 8;

    UserBean bean ;
    int permissionType ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3d);
        MaterialGenApplication.getInstance().setApiKey("CV8RiFSCwQTFPxl1ET8PWacetyb/E3+HjejRkuQHJ/RSczHVZzPXC7pNRBPPpSoJvuigzxm5tRMzvee57oVD3djKVLNc");
        ReconstructApplication.getInstance().setApiKey("CV8RiFSCwQTFPxl1ET8PWacetyb/E3+HjejRkuQHJ/RSczHVZzPXC7pNRBPPpSoJvuigzxm5tRMzvee57oVD3djKVLNc");
        Toolbar toolBar = findViewById(R.id.tb_main_modeling3d);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        homeFragment = new HomeFragment(this);
        replaceFragment(homeFragment);
        bean = BaseUtils.getUser(MainActivity.this);
        if (bean==null){
            bean = new UserBean();
            try {
                BaseUtils.saveUser(MainActivity.this,bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (fragment!=null) {
            ft.replace(R.id.frame_layout, fragment);
        }
        ft.commit();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (perms.size()>3) {

            if (permissionType==1){
                Intent rgbIntent = new Intent(MainActivity.this, NewScanActivity.class);
                startActivity(rgbIntent);
            }else if (permissionType==2){
                Intent intent = new Intent(MainActivity.this, CaptureMaterialActivity.class);
                startActivity(intent);
            }

        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setRequestCode(RC_CAMERA_AND_EXTERNAL_STORAGE)
                    .setRationale("Prompt description information (specific supplement)")
                    .setTitle("Title (specific supplement)")
                    .build()
                    .show();
        }
    }

    @Override
    public void askFromType(int type) {
        permissionType = type ;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseAppUtils.closeDatabase();
        DatabaseMaterialAppUtils.closeDatabase();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}