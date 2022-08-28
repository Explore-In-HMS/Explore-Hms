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
package com.hms.explorehms.huawei.feature_arengine.common;

public class TextDisplay {
    private OnTextInfoChangeListener mTextInfoListener;

    /**
     * Display the string information. This method is called in each frame
     * when {@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}.
     *
     * @param sb String builder.
     */
    public void onDrawFrame(StringBuilder sb) {
        if (sb == null) {
            showTextInfo();
            return;
        }
        showTextInfo(sb.toString());
    }

    /**
     * Set the listener to display information in the UI thread. This method is called
     * when {@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}.
     *
     * @param listener OnTextInfoChangeListener.
     */
    public void setListener(OnTextInfoChangeListener listener) {
        mTextInfoListener = listener;
    }

    /**
     * Listen to the text change and execute corresponding methods.
     *
     * @author HW
     * @since 2020-03-16
     */
    public interface OnTextInfoChangeListener {
        /**
         * Display the given text.
         *
         * @param text Text to be displayed.
         * @param positionX X-coordinates of points
         * @param positionY Y-coordinates of points
         */
        void textInfoChanged(String text, float positionX, float positionY);
    }

    private void showTextInfo(String text) {
        if (mTextInfoListener != null) {
            mTextInfoListener.textInfoChanged(text, 0, 0);
        }
    }

    private void showTextInfo() {
        if (mTextInfoListener != null) {
            mTextInfoListener.textInfoChanged(null, 0, 0);
        }
    }
}
