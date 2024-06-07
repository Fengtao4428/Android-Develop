package com.example.nfcdemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * HistoryActivity 类用于显示和管理历史打卡记录。
 */
public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText searchStudentId, searchTheme, searchDate;
    private Button searchButton, clearHistoryButton;
    private TextView totalAttendanceCount;
    private RecyclerView historyRecyclerView;
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
        setContentView(R.layout.activity_history);

        // 初始化UI组件
        searchStudentId = findViewById(R.id.search_student_id);
        searchTheme = findViewById(R.id.search_theme);
        searchDate = findViewById(R.id.search_date);
        searchButton = findViewById(R.id.search_button);
        clearHistoryButton = findViewById(R.id.clear_history_button);
        totalAttendanceCount = findViewById(R.id.total_attendance_count);
        historyRecyclerView = findViewById(R.id.history_recycler_view);

        // 初始化打卡记录列表和适配器
        recordList = new ArrayList<>();
        recordAdapter = new RecordAdapter(recordList);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(recordAdapter);

        // 初始化数据库帮助类
        dbHelper = new DatabaseHelper(this);

        // 设置搜索按钮的点击事件监听器
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAttendanceRecords();
            }
        });

        // 设置清除历史记录按钮的点击事件监听器
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistoryRecords();
            }
        });
    }

    /**
     * 搜索打卡记录，根据用户输入的条件查询数据库。
     */
    private void searchAttendanceRecords() {
        String studentId = searchStudentId.getText().toString();
        String theme = searchTheme.getText().toString();
        String date = searchDate.getText().toString();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Attendance WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (!studentId.isEmpty()) {
            queryBuilder.append(" AND StudentID = ?");
            args.add(studentId);
        }
        if (!theme.isEmpty()) {
            queryBuilder.append(" AND Theme = ?");
            args.add(theme);
        }
        if (!date.isEmpty()) {
            queryBuilder.append(" AND DATE(Timestamp) = DATE(?)");
            args.add(date);
        }

        Cursor cursor = db.rawQuery(queryBuilder.toString(), args.toArray(new String[0]));
        recordList.clear();
        int totalCount = 0;

        while (cursor.moveToNext()) {
            String className = cursor.getString(cursor.getColumnIndex("Class"));
            String studentID = cursor.getString(cursor.getColumnIndex("StudentID"));
            String studentName = cursor.getString(cursor.getColumnIndex("Name"));
            String timestamp = cursor.getString(cursor.getColumnIndex("Timestamp"));
            String themeResult = cursor.getString(cursor.getColumnIndex("Theme"));
            int count = cursor.getInt(cursor.getColumnIndex("Count"));

            AttendanceRecord record = new AttendanceRecord(className, studentID, studentName, timestamp, themeResult, count);
            recordList.add(record);
            totalCount++;
        }
        cursor.close();
        recordAdapter.notifyDataSetChanged();

        if (!studentId.isEmpty()) {
            totalAttendanceCount.setVisibility(View.VISIBLE);
            totalAttendanceCount.setText("总打卡次数: " + totalCount);
        } else {
            totalAttendanceCount.setVisibility(View.GONE);
        }
    }


    /**
     * 清除所有历史打卡记录。
     */
    private void clearHistoryRecords() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Attendance", null, null);
        recordList.clear();
        recordAdapter.notifyDataSetChanged();
        totalAttendanceCount.setVisibility(View.GONE);
        Toast.makeText(this, "历史记录已清除", Toast.LENGTH_SHORT).show();
    }
}
