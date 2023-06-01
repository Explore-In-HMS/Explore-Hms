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

package com.hms.explorehms.huawei.ui.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件分发（支持黏性事件）
 * 
 * @author xwx882936
 * @since 2021/10/9
 */
public class HiDataBusUtils {

    private static final ConcurrentHashMap<String, StickyLiveData<Object>> EVENT_MAP;

    public static final HiDataBusUtils INSTANCE;

    private HiDataBusUtils() {
    }

    static {
        INSTANCE = new HiDataBusUtils();
        EVENT_MAP = new ConcurrentHashMap<>();
    }

    public synchronized final <T> StickyLiveData<T> with(@NonNull String eventName) {
        StickyLiveData<Object> liveData = EVENT_MAP.get(eventName);
        if (liveData == null) {
            EVENT_MAP.putIfAbsent(eventName, new StickyLiveData<>(eventName));
            liveData = EVENT_MAP.get(eventName);
        }
        return (StickyLiveData<T>) liveData;
    }

    public static ConcurrentHashMap<String, StickyLiveData<Object>> getEventMap() {
        return EVENT_MAP;
    }

    public static final class StickyLiveData<T> extends LiveData<T> {
        private T mStickyData;

        private int mVersion;

        private final String eventName;

        public StickyLiveData(String eventName) {
            super();
            this.eventName = eventName;
        }

        public final void setStickyData(T stickyData) {
            this.mStickyData = stickyData;
            this.setValue(stickyData);
        }

        public final void postStickyData(T stickyData) {
            this.mStickyData = stickyData;
            this.postValue(stickyData);
        }

        protected void setValue(T value) {
            this.mVersion++;
            super.setValue(value);
        }

        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            this.observerSticky(owner, false, observer);
        }

        public final void observerSticky(@NonNull LifecycleOwner owner, boolean sticky,
            @NonNull Observer<? super T> observer) {
            owner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {

            });
            super.observe(owner, new StickyObserver<>(this, sticky, observer));
        }

        public int getVersion() {
            return mVersion;
        }

        public T getStickyData() {
            return mStickyData;
        }

        public String getEventName() {
            return eventName;
        }

        public void remove(){
            HiDataBusUtils.getEventMap().remove(StickyLiveData.this.eventName);
        }
    }

    public static final class StickyObserver<T> implements Observer<T> {
        private int lastVersion;

        private final StickyLiveData<T> stickyLiveData;

        private final boolean sticky;

        private final Observer<? super T> observer;

        public StickyObserver(StickyLiveData<T> stickyLiveData, boolean sticky, Observer<? super T> observer) {
            super();
            this.stickyLiveData = stickyLiveData;
            this.sticky = sticky;
            this.observer = observer;
            this.lastVersion = this.stickyLiveData.getVersion();
        }

        @Override
        public void onChanged(T o) {
            if (this.lastVersion >= this.stickyLiveData.getVersion()) {
                if (this.sticky && this.stickyLiveData.getStickyData() != null) {
                    this.observer.onChanged(this.stickyLiveData.getStickyData());
                }
            } else {
                this.lastVersion = this.stickyLiveData.getVersion();
                this.observer.onChanged(o);
            }
        }

        public final StickyLiveData<T> getStickyLiveData() {
            return this.stickyLiveData;
        }

        public final boolean getSticky() {
            return this.sticky;
        }

        public final Observer<? super T> getObserver() {
            return this.observer;
        }
    }
}
