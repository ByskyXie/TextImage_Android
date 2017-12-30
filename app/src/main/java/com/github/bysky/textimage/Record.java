package com.github.bysky.textimage;

/**
 * Created by asus on 2017/12/30.
 */

public class Record {
    private String filePath;
    private String fileName;

    public Record(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }
}
