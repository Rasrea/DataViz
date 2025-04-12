package com.lucky.data_visual.server.databaseServer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucky.data_visual.model.DatabaseConfPath;
import jakarta.annotation.Resource;

import java.io.File;
import java.io.IOException;

public class MySQLServer {

    @Resource
    private DatabaseConfPath databaseConfPath;
    private String mysqlConfPath = databaseConfPath.getMysqlConfPath();

}
