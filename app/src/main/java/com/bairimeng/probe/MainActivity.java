package com.bairimeng.probe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 探针主界面：启动即触发一次，并提供一个按钮可重复触发。
 * 也可用 adb 广播触发：adb shell am broadcast -a com.bairimeng.probe.SHOW
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 80, 40, 40);

        TextView title = new TextView(this);
        title.setText("MessagingStyle 探针");
        title.setTextSize(20);
        root.addView(title);

        TextView hint = new TextView(this);
        hint.setText("点击下方按钮，发送一条会话式通知（2 条固定消息）\n\n"
                + "也可用 adb 触发：\n"
                + "adb shell am start -n com.bairimeng.probe/.MainActivity\n"
                + "adb shell am broadcast -a com.bairimeng.probe.SHOW");
        hint.setTextSize(14);
        hint.setPadding(0, 30, 0, 30);
        root.addView(hint);

        Button btn = new Button(this);
        btn.setText("发送会话通知");
        btn.setOnClickListener(v -> ConversationProbe.show(this));
        root.addView(btn);

        setContentView(root);
    }
}
