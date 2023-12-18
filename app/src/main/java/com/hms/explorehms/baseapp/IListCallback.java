package com.hms.explorehms.baseapp;

import java.util.List;

public interface IListCallback<UserInfo> {
    void onResultCallBack(List<UserInfo> list);
}
