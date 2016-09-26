package com.luluteam.yjy.model;

import java.io.Serializable;

/**
 * Created by Guan on 2016/7/28.
 */
public class Courseware implements Serializable {

    private String docId="";
    private String originalName;
    private String downPath="";
    private String docSize="";
    private String type="";
    //private String originalName="";

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getDownPath() {
        return downPath;
    }

    public void setDownPath(String downPath) {
        this.downPath = downPath;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocSize() {
        return docSize;
    }

    public void setDocSize(String docSize) {
        this.docSize = docSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
