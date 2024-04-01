package com.reireilla.database;

import org.apache.ibatis.jdbc.ScriptRunner;
import java.io.*;
import java.sql.Connection;

public class ScriptExecutor {
    public static void executeFile(String scriptPath, Connection connection) {
        try {
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.setSendFullScript(true);
            scriptRunner.setStopOnError(true);
            scriptRunner.runScript(
                    new InputStreamReader(ScriptExecutor.class.getClassLoader().getResourceAsStream(scriptPath)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
