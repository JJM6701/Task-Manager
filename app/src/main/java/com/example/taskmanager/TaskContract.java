package com.example.taskmanager;

import android.provider.BaseColumns;
public final class TaskContract {
    private TaskContract() {}
    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DUE_DATE = "dueDate";
        public static final String COLUMN_NAME_COMPLETED = "completed";
    }
}