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

package com.hms.explorehms.huawei.feature_imagekit.utils;

import android.content.Context;

import com.hms.explorehms.huawei.feature_imagekit.R;

public class ImageRenderUtils {

    private ImageRenderUtils() {
        throw new IllegalStateException("Utility class");
    }


    public static String getAnimationInfo(String animation, Context cnx){

        if(animation.equals("PositionAnimation")){
            return cnx.getResources().getString(R.string.msg_position_anim);
        }else if(animation.equals("RotationAnimation")){
            return cnx.getResources().getString(R.string.msg_rotation_anim);
        }else if(animation.equals("SizeAnimation")){
            return cnx.getResources().getString(R.string.msg_size_anim);
        }else if(animation.equals("AlphaAnimation")){
            return cnx.getResources().getString(R.string.msg_alpha_anim);
        }else if(animation.equals("SourcesAnimation")){
            return cnx.getResources().getString(R.string.msg_sources_anim);
        }else if(animation.equals("ParticleView")){
            return cnx.getResources().getString(R.string.msg_particle_view);
        }else if(animation.equals("WaterWallpaper")){
            return cnx.getResources().getString(R.string.msg_waterwall_view);
        }else if(animation.equals("Paint")){
            return cnx.getResources().getString(R.string.msg_paint);
        }else if(animation.equals("Marquee")){
            return cnx.getResources().getString(R.string.msg_marquee);
        }else if(animation.equals("ParticleScatter")){
            return cnx.getResources().getString(R.string.msg_particle_scatter);
        }else if(animation.equals("RotateView")){
            return cnx.getResources().getString(R.string.msg_rotate_view);
        }else if(animation.equals("DropPhysicalView")){
            return cnx.getResources().getString(R.string.msg_drop_physical_view);
        }else if(animation.equals("FlipBook")){
            return cnx.getResources().getString(R.string.msg_flipbook);
        }else if(animation.equals("MeshImage")){
            return cnx.getResources().getString(R.string.msg_mesh_image);
        }
        return "?";
    }
}
