package com.example.nfcdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper 类用于管理应用的SQLite数据库。
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // 数据库名称
    private static final String DATABASE_NAME = "NFCApp.db";
    // 数据库版本
    private static final int DATABASE_VERSION = 1;

    /**
     * 构造函数，用于创建DatabaseHelper实例。
     *
     * @param context 应用上下文
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 创建数据库时调用，初始化数据库表结构。
     *
     * @param db SQLiteDatabase实例
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建卡片信息表
        db.execSQL("CREATE TABLE CardInfo (CardID TEXT PRIMARY KEY, Class TEXT, StudentID TEXT, Name TEXT)");
        // 创建打卡记录表
        db.execSQL("CREATE TABLE Attendance (ID INTEGER PRIMARY KEY AUTOINCREMENT, CardID TEXT, Class TEXT, StudentID TEXT, Name TEXT, Timestamp TEXT, Theme TEXT, Count INTEGER)");
    }

    /**
     * 数据库升级时调用，更新数据库表结构。
     *
     * @param db SQLiteDatabase实例
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除旧的卡片信息表
        db.execSQL("DROP TABLE IF EXISTS CardInfo");
        // 删除旧的打卡记录表
        db.execSQL("DROP TABLE IF EXISTS Attendance");
        // 重新创建表
        onCreate(db);
    }
}
