package com.camp.bit.todolist.beans;

/**
 * Created by liuYang on 2019/1/23.
 */

/**
 * enum of note priority
 */
public enum Priority {
    HIGH(2), COMMON(1),LOW(0);

    public final int intValue;

    Priority(int intValue) {
        this.intValue = intValue;
    }

    public static Priority from(int intValue) {
        for (Priority priority : Priority.values()) {
            if (priority.intValue == intValue) {
                return priority;
            }
        }
        // default priority
        return COMMON;
    }
}
