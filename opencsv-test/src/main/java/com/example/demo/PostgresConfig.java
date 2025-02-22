package com.example.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("postgres")
public class PostgresConfig {
    private String host;
    private String dataSourceName;
    private String dbName;
    private Integer maxConnections;
    private String user;
    private String password;
    private boolean ssl;
    private String applicationName;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String toString() {
        return "PostgresConfig{" +
                "host='" + host + '\'' +
                ", dataSourceName='" + dataSourceName + '\'' +
                ", dbName='" + dbName + '\'' +
                ", maxConnections=" + maxConnections +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", ssl=" + ssl +
                ", applicationName='" + applicationName + '\'' +
                '}';
    }
}
