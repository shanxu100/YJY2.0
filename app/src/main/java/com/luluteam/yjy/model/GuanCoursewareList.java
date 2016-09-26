package com.luluteam.yjy.model;

import java.util.ArrayList;

/**
 * Created by Guan on 2016/8/5.
 */
public class GuanCoursewareList {
    boolean result;
    int size;
    ArrayList<GuanCourseware> files;

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<GuanCourseware> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<GuanCourseware> files) {
        this.files = files;
    }
}
