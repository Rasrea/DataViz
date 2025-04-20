package com.lucky.data_visual.enums;

public enum FileType {
    CSV("CSV"),
    EXCEL("EXCEL"),
    DB("DB");

    private final String fileType;

    FileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }
}
