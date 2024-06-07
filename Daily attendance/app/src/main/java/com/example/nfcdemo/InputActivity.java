package com.example.nfcdemo;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;

/**
 * InputActivity 类用于录入学生信息并将其与NFC卡片关联。
 */
public class InputActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private Tag tag;
    private EditText editTextClass, editTextStudentID, editTextName;
    private DatabaseHelper dbHelper;

    /**
     * onCreate 方法在活动创建时调用，初始化界面和组件。
     *
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // 初始化UI组件
        editTextClass = findViewById(R.id.edit_text_class);
        editTextStudentID = findViewById(R.id.edit_text_student_id);
        editTextName = findViewById(R.id.edit_text_name);
        Button inputButton = findViewById(R.id.input_button);
        Button clearCardInfoButton = findViewById(R.id.clear_card_info_button);

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

        // 设置输入按钮的点击事件监听器
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag != null) {
                    String className = editTextClass.getText().toString();
                    String studentID = editTextStudentID.getText().toString();
                    String name = editTextName.getText().toString();
                    addCardInfo(bytesToHex(tag.getId()), className, studentID, name);
                    Toast.makeText(InputActivity.this, "卡片录入成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InputActivity.this, "请将卡片贴近手机", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 设置清除卡片信息按钮的点击事件监听器
        clearCardInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag != null) {
                    clearSpecificCardInfo(bytesToHex(tag.getId()));
                } else {
                    Toast.makeText(InputActivity.this, "请将卡片贴近手机", Toast.LENGTH_SHORT).show();
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
            Log.d("InputActivity", "NFC Tag Detected");
        }
    }

    /**
     * addCardInfo 方法将卡片信息保存到数据库。
     *
     * @param cardID 卡片ID
     * @param className 班级名称
     * @param studentID 学生ID
     * @param name 学生姓名
     */
    private void addCardInfo(String cardID, String className, String studentID, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CardID", cardID);
        values.put("Class", className);
        values.put("StudentID", studentID);
        values.put("Name", name);
        db.insert("CardInfo", null, values);
        db.close();
    }

    /**
     * clearSpecificCardInfo 方法清除特定卡片的信息。
     *
     * @param cardID 卡片ID
     */
    private void clearSpecificCardInfo(String cardID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = db.delete("CardInfo", "CardID = ?", new String[]{cardID});
        db.close();
        if (rowsDeleted > 0) {
            Toast.makeText(this, "卡片信息已清除", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "未找到卡片信息", Toast.LENGTH_SHORT).show();
        }
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
