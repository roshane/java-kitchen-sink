package com.example.demo;

import org.postgresql.PGConnection;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.function.Predicate;

@SpringBootApplication
public class DemoApplication {
    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    static class Configuration {
        private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
        private final PGPoolingDataSource source;

        {
            source = new PGPoolingDataSource();
            source.setDataSourceName("primary-pool-01");
            source.setDatabaseName("test-db");
            source.setMaxConnections(5);
            source.setUser("admin");
            source.setPassword("password");
            source.setSsl(false);
            source.setServerName("postgres");
//            source.setServerName("localhost");
        }

        public <R> R withConnection(ThrowableFunction<Connection, R> consumer) {
            try (Connection connection = source.getConnection()) {
                return consumer.apply(connection);
            } catch (Exception ex) {
                logger.error("Error", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    @Controller
    public static class CsvController {

        private static final int csvColumnCount = 11;
        private static final Predicate<String> isValidCsvLine =
                it -> it.split(",").length == csvColumnCount;
        private static final Configuration configuration = new Configuration();

        static {
            try (InputStream stream = Objects.requireNonNull(CsvController.class.getClassLoader()
                    .getResource("postgres.sql")).openStream()) {
                logger.info("Initializing the database");
                String initSql = new String(stream.readAllBytes());
                configuration.withConnection(connection -> {
                    Statement statement = connection.createStatement();
                    boolean execute = statement.execute(initSql);
                    logger.info("executed initialization script: {}", execute);
                    statement.close();
                    return execute;
                });
            } catch (Exception ex) {
                logger.error("Error initializing DB", ex);
                throw new RuntimeException(ex);
            }
        }

        @GetMapping("/ping")
        Mono<ResponseEntity<String>> ping() {
            return Mono.just(ResponseEntity.ok("pong"));
        }

        @PostMapping(value = "/dataset/resale_flat_price")
        Mono<ResponseEntity<String>> rootEntity(@RequestBody Flux<String> stream) {
            return persistCsvData(stream)
                    .map(ResponseEntity::ok);
        }

        @DeleteMapping("/dataset/resale_flat_price")
        Mono<ResponseEntity<Boolean>> deleteEntries() {
            return Mono.fromCallable(() -> configuration.withConnection(connection -> {
                Statement statement = connection.createStatement();
                boolean execute = statement.execute("delete from resale_flat_price");
                statement.close();
                return execute;
            })).map(ResponseEntity::ok);
        }

        @GetMapping("/dataset/resale_flat_price/size")
        Mono<ResponseEntity<Integer>> countResaleFlatPriceRecords() {
            return configuration.withConnection(conn -> {
                return Mono.fromCallable(() -> {
                    Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery("select count(*) as count from resale_flat_price");
                    int result = 0;
                    while (resultSet.next()) {
                        result = resultSet.getInt("cou");
                    }
                    resultSet.close();
                    statement.close();
                    return ResponseEntity.ok(result);
                });
            });
        }

        private Mono<String> persistCsvData(Flux<String> stream) {
            String sql = """
                     COPY resale_flat_price(month, town, flat_type, block, street_name, storey_range, floor_area_sqm, flat_model, lease_commence_date, remaining_lease, resale_price)
                     FROM STDIN
                     WITH (FORMAT csv)
                    """;
            return configuration.withConnection(connection -> {
                StopWatch stopWatchA = new StopWatch();
                stopWatchA.start();
                PGConnection pgConnection = connection.unwrap(PGConnection.class);
                CopyManager copyAPI = pgConnection.getCopyAPI();
                CopyIn copyIn = copyAPI.copyIn(sql);
                Mono<String> result = stream
                        .filter(isValidCsvLine)
                        .map(it -> {
                            byte[] buf = (it + "\n").getBytes(StandardCharsets.UTF_8);
                            try {
                                copyIn.writeToCopy(buf, 0, buf.length);
                                return it;
                            } catch (SQLException ex) {
                                logger.error("SQLError", ex);
                                throw new RuntimeException(ex);
                            }
                        }).last()
                        .map("lastLine processed: [%s]"::formatted)
                        .doOnSuccess(ignore -> {
                            try {
                                long linesProcessed = copyIn.endCopy();
                                logger.info("linesProcessed: {}", linesProcessed);
                                stopWatchA.stop();
                                logger.info("timeTaken: {}ms", stopWatchA.getTotalTimeMillis());
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                return result;
            });
        }
    }

}
