package com.group05.mylocation.Interface;

import android.net.Uri;

import com.group05.mylocation.User;

import java.util.List;

public interface UserCallback {
    void onCallback(User value);
    void onCallbackImage(Uri uri);
}
