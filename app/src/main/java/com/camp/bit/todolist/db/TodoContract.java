package com.camp.bit.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static final String SQL_CREATE_ENTRIES = "create table "+TodoEntry.TABLE_NAME+
            " ("+TodoEntry.ID + " integer primary key autoincrement,"+
            TodoEntry.DATE +" long,"+
            TodoEntry.STATE +" integer,"+
            TodoEntry.CONTENT +" text," +
            TodoEntry.PRIORITY + " integer)";

    public static final String SQL_DELETE_ENTRIES = "drop table if exists "+TodoEntry.TABLE_NAME;
    private TodoContract() {

    }

    public  static class TodoEntry implements BaseColumns{
        public static final String TABLE_NAME = "todo";
        public static final String ID = "id";
        public static final String DATE = "date";
        public static final String STATE= "state";
        public static final String CONTENT = "content";
        public static final String PRIORITY = "priority";

    }

}
