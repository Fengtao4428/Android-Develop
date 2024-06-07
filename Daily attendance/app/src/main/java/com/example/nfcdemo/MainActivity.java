package com.example.nfcdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity 类是应用的主界面，提供导航到其他功能模块的入口。
 */
public class MainActivity extends AppCompatActivity {

    /**
     * onCreate 方法在活动创建时调用，初始化界面和组件。
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化UI组件
        Button inputButton = findViewById(R.id.input_button);
        Button recordButton = findViewById(R.id.record_button);
        Button historyButton = findViewById(R.id.history_button);

        // 设置输入按钮的点击事件监听器，跳转到 InputActivity
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
            }
        });

        // 设置记录按钮的点击事件监听器，跳转到 RecordActivity
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        });

        // 设置历史按钮的点击事件监听器，跳转到 HistoryActivity
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}
