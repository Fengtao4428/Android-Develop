package com.example.nfcdemo;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecordActivity 类用于记录和显示打卡信息。
 */
public class RecordActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private Tag tag;
    private EditText editTextTheme;
    private DatabaseHelper dbHelper;
    private RecyclerView recordRecyclerView;
    private RecordAdapter recordAdapter;
    private List<AttendanceRecord> recordList;

    /**
     * onCreate 方法在活动创建时调用，初始化界面和组件。
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // 初始化UI组件
        editTextTheme = findViewById(R.id.edit_text_theme);
        Button startRecordButton = findViewById(R.id.start_record_button);
        recordRecyclerView = findViewById(R.id.record_recycler_view);

        // 初始化打卡记录列表和适配器
        recordList = new ArrayList<>();
        recordAdapter = new RecordAdapter(recordList);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordRecyclerView.setAdapter(recordAdapter);

        // 初始化NFC适配器和PendingIntent
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(this);

        // 设置IntentFilter和技术列表，用于过滤和处理特定类型的NFC标签
        intentFiltersArray = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        };
        techListsArray = new String[][] {
                new String[] { NfcA.class.getName() },
                new String[] { NfcB.class.getName() },
                new String[] { IsoDep.class.getName() },
                new String[] { NfcF.class.getName() },
                new String[] { NfcV.class.getName() },
                new String[] { Ndef.class.getName() },
                new String[] { NdefFormatable.class.getName() }
        };

        // 设置开始打卡按钮的点击事件监听器
        startRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordList.clear();
                recordAdapter.notifyDataSetChanged();
                if (tag != null) {
                    String theme = editTextTheme.getText().toString();
                    if (!theme.isEmpty()) {
                        startRecording(theme);
                    } else {
                        Toast.makeText(RecordActivity.this, "请输入打卡主题", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RecordActivity.this, "请将卡片贴近手机", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * onResume 方法在活动恢复时调用，启用前台调度系统。
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }
    }

    /**
     * onPause 方法在活动暂停时调用，禁用前台调度系统。
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    /**
     * onNewIntent 方法在检测到新的NFC标签时调用，处理标签数据。
     *
     * @param intent 新的Intent，包含NFC标签数据
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Log.d("RecordActivity", "NFC Tag Detected");
            if (editTextTheme.getText().toString().isEmpty()) {
                Toast.makeText(this, "请输入打卡主题", Toast.LENGTH_SHORT).show();
            } else {
                recordAttendance(bytesToHex(tag.getId()), editTextTheme.getText().toString());
            }
        }
    }

    /**
     * startRecording 方法初始化新的打卡记录会话。
     *
     * @param theme 打卡主题
     */
    private void startRecording(String theme) {
        Toast.makeText(this, "开始打卡: " + theme, Toast.LENGTH_SHORT).show();
    }

    /**
     * recordAttendance 方法将打卡信息保存到数据库。
     *
     * @param cardID 卡片ID
     * @param theme 打卡主题
     */
    private void recordAttendance(String cardID, String theme) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CardInfo WHERE CardID = ?", new String[]{cardID});
        if (cursor.moveToFirst()) {
            String className = cursor.getString(cursor.getColumnIndex("Class"));
            String studentID = cursor.getString(cursor.getColumnIndex("StudentID"));
            String name = cursor.getString(cursor.getColumnIndex("Name"));
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // 获取该学生在当前主题下的打卡次数
            int count = getStudentCountForTheme(db, studentID, theme) + 1;

            ContentValues values = new ContentValues();
            values.put("CardID", cardID);
            values.put("Class", className);
            values.put("StudentID", studentID);
            values.put("Name", name);
            values.put("Timestamp", timestamp);
            values.put("Theme", theme);
            values.put("Count", count);
            db.insert("Attendance", null, values);

            recordList.add(new AttendanceRecord(className, studentID, name, timestamp, theme, count));
            recordAdapter.notifyDataSetChanged();

            Toast.makeText(this, "打卡成功：" + name, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "未知卡片", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
    }

    private int getStudentCountForTheme(SQLiteDatabase db, String studentID, String theme) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Attendance WHERE StudentID = ? AND Theme = ?", new String[]{studentID, theme});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }


    /**
     * getCountForTheme 方法获取当前主题的打卡次数。
     *
     * @param db SQLiteDatabase实例
     * @param theme 打卡主题
     * @return 当前主题的打卡次数
     */
    private int getCountForTheme(SQLiteDatabase db, String theme) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Attendance WHERE Theme = ?", new String[]{theme});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * bytesToHex 方法将字节数组转换为十六进制字符串。
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
