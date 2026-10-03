package com.bairimeng.probe;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

/**
 * MessagingStyle 最小探针。
 * 固定测试数据，与白日梦正式通知系统完全隔离。
 */
public class ConversationProbe {

    public static final String CHANNEL_ID = "bm-test-conversation";
    public static final String CONVERSATION_ID = "test_role_001";
    public static final int NOTIFICATION_ID = 999001;

    private static final String SENDER_NAME = "测试角色";

    /**
     * 触发一次探针通知：发两条固定消息到同一会话。
     */
    public static void show(Context context) {
        ensureChannel(context);
        registerShortcut(context);
        postNotification(context);
    }

    private static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    "测试会话通知",
                    NotificationManager.IMPORTANCE_HIGH
            );
            ch.setDescription("MessagingStyle 探针专用测试渠道");
            ch.enableVibration(true);
            nm.createNotificationChannel(ch);
        }
    }

    private static void registerShortcut(Context context) {
        try {
            Person sender = new Person.Builder().setName(SENDER_NAME).build();
            ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(context, CONVERSATION_ID)
                    .setLongLived(true)
                    .setPerson(sender)
                    .setShortLabel(SENDER_NAME)
                    .setIcon(IconCompat.createWithResource(context, android.R.drawable.ic_dialog_info))
                    .build();
            boolean ok = ShortcutManagerCompat.pushDynamicShortcut(context, shortcut);
            android.util.Log.i("ConversationProbe", "pushDynamicShortcut result=" + ok
                    + " maxShortcuts=" + ShortcutManagerCompat.getMaxShortcutCountPerActivity(context));
        } catch (Throwable t) {
            android.util.Log.e("ConversationProbe", "shortcut failed", t);
        }
    }

    private static void postNotification(Context context) {
        NotificationManager nm = context.getSystemService(NotificationManager.class);

        // 会话参与者
        Person sender = new Person.Builder()
                .setName(SENDER_NAME)
                .setIcon(IconCompat.createWithResource(context, android.R.drawable.ic_dialog_info))
                .build();

        // 两条固定测试消息
        NotificationCompat.MessagingStyle.Message msg1 =
                new NotificationCompat.MessagingStyle.Message(
                        "你好，这是第一条测试消息", System.currentTimeMillis() - 60000, sender);
        NotificationCompat.MessagingStyle.Message msg2 =
                new NotificationCompat.MessagingStyle.Message(
                        "这是第二条测试消息", System.currentTimeMillis(), sender);

        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(sender)
                .setConversationTitle(SENDER_NAME)
                .addMessage(msg1)
                .addMessage(msg2);

        // 打开本 App 的 contentIntent
        Intent launch = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0, launch,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notif = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setStyle(style)
                .setContentIntent(contentIntent)
                .setShortcutId(CONVERSATION_ID)
                .setColor(Color.parseColor("#5B8DEF"))
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .build();

        try {
            nm.notify(NOTIFICATION_ID, notif);
            android.util.Log.i("ConversationProbe",
                    "notify ok, conversationId=" + CONVERSATION_ID + " id=" + NOTIFICATION_ID);
        } catch (Throwable t) {
            android.util.Log.e("ConversationProbe", "notify failed", t);
        }
    }
}
