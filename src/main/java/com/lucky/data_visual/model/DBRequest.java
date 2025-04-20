package com.lucky.data_visual.model;

public class DBRequest {
    private String dbType;
    private String dbName;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    @Override
    public String toString() {
        return "DBRequest{" +
                "dbType='" + dbType + '\'' +
                ", dbName='" + dbName + '\'' +
                ", dbUrl='" + dbUrl + '\'' +
                ", dbUser='" + dbUser + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                '}';
    }

    // getters and setters
    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}