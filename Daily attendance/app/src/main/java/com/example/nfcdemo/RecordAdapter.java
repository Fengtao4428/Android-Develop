package com.example.nfcdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * RecordAdapter 类用于将打卡记录数据绑定到 RecyclerView 上。
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<AttendanceRecord> records;

    /**
     * 构造函数，用于创建 RecordAdapter 实例。
     *
     * @param records 打卡记录列表
     */
    public RecordAdapter(List<AttendanceRecord> records) {
        this.records = records;
    }

    /**
     * 创建新的 ViewHolder 实例，并初始化一些必要的元素。
     *
     * @param parent   父视图组
     * @param viewType 视图类型
     * @return 新的 ViewHolder 实例
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_record, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 将数据绑定到 ViewHolder 上。
     *
     * @param holder   ViewHolder 实例
     * @param position 位置索引
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceRecord record = records.get(position);
        holder.className.setText("班级：" + record.getClassName());
        holder.studentId.setText("学号：" + record.getStudentId());
        holder.studentName.setText("姓名：" + record.getStudentName());
        holder.timestamp.setText("时间：" + record.getTimestamp());
        holder.theme.setText("主题：" + record.getTheme());
        holder.count.setText("次数：" + String.valueOf(record.getCount()));
    }

    /**
     * 获取项目总数。
     *
     * @return 打卡记录列表的大小
     */
    @Override
    public int getItemCount() {
        return records.size();
    }

    /**
     * ViewHolder 类用于缓存视图组件，提高性能。
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView className, studentId, studentName, timestamp, theme, count;

        /**
         * ViewHolder 构造函数，初始化视图组件。
         *
         * @param itemView 项目视图
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.class_name);
            studentId = itemView.findViewById(R.id.student_id);
            studentName = itemView.findViewById(R.id.student_name);
            timestamp = itemView.findViewById(R.id.timestamp);
            theme = itemView.findViewById(R.id.theme);
            count = itemView.findViewById(R.id.count);
        }
    }
}
