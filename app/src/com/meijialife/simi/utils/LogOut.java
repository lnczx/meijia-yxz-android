package com.meijialife.simi.utils;

import android.util.Log;

import com.meijialife.simi.BuildConfig;

public class LogOut {
  private static final boolean isOnlineModel = BuildConfig.DEBUG;
  protected static final String TAG = "Debug";

  public static void i(String tag, String content) {
    if (!isOnlineModel) Log.i(tag, content + "  " + "time: " + System.currentTimeMillis());
  }

  public static void i(String content) {
    if (!isOnlineModel) Log.i("===bolor==", content + "  " + "time: " + System.currentTimeMillis());
  }

  public static void debug(String content) {
    if (!isOnlineModel) Log.d(TAG, content + "  " + "time: " + System.currentTimeMillis());
  }
}
