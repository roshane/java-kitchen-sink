package com.aeon.restrictionpoc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DbInitializer implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(DbInitializer.class);
    private final DataSource dataSource;


    public DbInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (
                Connection conn = dataSource.getConnection();
                Statement statement = conn.createStatement()
        ) {
            String sql = readInitScriptAsString();
            logger.info("executing SQL: {}", sql);
            statement.execute(sql);
        }
    }

    String readInitScriptAsString() {
        try {
            var inputStream = DbInitializer.class
                    .getClassLoader()
                    .getResourceAsStream("init.sql");
            return new String(inputStream.readAllBytes());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
