package com.camp.bit.todolist.beans;


/**
 * Created by liuYang on 2019/1/23.
 */

/**
 * provide a static method to get color
 */
public class BackgroundColor {

    /**
     * get color by priority
     * @param priority represent importance of content
     */
    public static String getColor(int priority){
        if(priority ==  Priority.LOW.intValue){
            return "#3F51B5";
        }

        if(priority == Priority.HIGH.intValue){
            return "#FF4081";
        }

        return "#ffffff";
    }
}
