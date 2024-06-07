package com.example.nfcdemo;

/**
 * AttendanceRecord 类表示一个打卡记录。
 */
public class AttendanceRecord {
    // 班级名称
    private String className;
    // 学生ID
    private String studentId;
    // 学生姓名
    private String studentName;
    // 打卡时间戳
    private String timestamp;
    // 打卡主题
    private String theme;
    // 打卡次数
    private int count;

    /**
     * AttendanceRecord 构造函数，用于创建一个新的打卡记录实例。
     *
     * @param className 班级名称
     * @param studentId 学生ID
     * @param studentName 学生姓名
     * @param timestamp 打卡时间戳
     * @param theme 打卡主题
     * @param count 打卡次数
     */
    public AttendanceRecord(String className, String studentId, String studentName, String timestamp, String theme, int count) {
        this.className = className;
        this.studentId = studentId;
        this.studentName = studentName;
        this.timestamp = timestamp;
        this.theme = theme;
        this.count = count;
    }

    /**
     * 获取班级名称。
     *
     * @return 班级名称
     */
    public String getClassName() {
        return className;
    }

    /**
     * 获取学生ID。
     *
     * @return 学生ID
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * 获取学生姓名。
     *
     * @return 学生姓名
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * 获取打卡时间戳。
     *
     * @return 打卡时间戳
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * 获取打卡主题。
     *
     * @return 打卡主题
     */
    public String getTheme() {
        return theme;
    }

    /**
     * 获取打卡次数。
     *
     * @return 打卡次数
     */
    public int getCount() {
        return count;
    }
}
