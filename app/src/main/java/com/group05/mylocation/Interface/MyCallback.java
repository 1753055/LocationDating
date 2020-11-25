package com.group05.mylocation.Interface;

import com.group05.mylocation.User;

import java.util.List;

public interface MyCallback {
    void onCallback(List<User> value);
    void onCallback2(List<String> value);

}
