package com.bairimeng.probe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 供 adb 广播触发探针：
 * adb shell am broadcast -a com.bairimeng.probe.SHOW
 */
public class ProbeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConversationProbe.show(context);
    }
}
