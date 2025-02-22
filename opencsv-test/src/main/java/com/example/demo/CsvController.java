package com.example.demo;

import org.postgresql.PGConnection;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

@Controller
public class CsvController {
    private static final int csvColumnCount = 11;
    private static final Predicate<String> isValidCsvLine = it -> it.split(",").length == csvColumnCount;
    private static final Logger logger = LoggerFactory.getLogger(CsvController.class);
    private final JdbcTemplate jdbcTemplate;

    public CsvController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (InputStream stream = Objects.requireNonNull(CsvController.class.getClassLoader()
                .getResource("postgres.sql")).openStream()) {
            logger.info("Initializing the database");
            String initSql = new String(stream.readAllBytes());
            jdbcTemplate.execute(initSql);
            logger.info("executed initialization script");
        } catch (Exception ex) {
            logger.error("Error initializing DB", ex);
            throw new RuntimeException(ex);
        }
    }

    @GetMapping("/ping")
    Mono<ResponseEntity<String>> ping() {
        return Mono.just(ResponseEntity.ok("pong"));
    }

    @PostMapping("/create")
    Mono<ResponseEntity<Map<String, String>>> http201() {
        return Mono.just(
                ResponseEntity
                        .created(URI.create("/create/%s".formatted(UUID.randomUUID().toString())))
                        .body(Map.of("message", "created"))

        );
    }

    @PostMapping(value = "/dataset/resale_flat_price")
    Mono<ResponseEntity<String>> rootEntity(@RequestBody Flux<String> stream) {
        logger.info("Processing upload request");
        return persistCsvData(stream)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/dataset/resale_flat_price")
    Mono<ResponseEntity<Boolean>> deleteEntries() {
        logger.info("Processing delete request");
        return Mono.fromCallable(() -> {
            int rowCount = jdbcTemplate.update("delete from resale_flat_price");
            return rowCount > 0;
        }).map(ResponseEntity::ok);
    }

    @GetMapping("/dataset/resale_flat_price/size")
    Mono<ResponseEntity<Integer>> countResaleFlatPriceRecords() {
        logger.info("Processing size request");
        return Mono.fromCallable(() ->
                jdbcTemplate.queryForObject("select count(*) as count from resale_flat_price", Integer.class)
        ).map(ResponseEntity::ok);
    }
    

    //    @Transactional
    private Mono<String> persistCsvData(Flux<String> stream) {
        String sql = """
                 COPY resale_flat_price(month, town, flat_type, block, street_name, storey_range, floor_area_sqm, flat_model, lease_commence_date, remaining_lease, resale_price)
                 FROM STDIN
                 WITH (FORMAT csv)
                """;
        return Mono.fromCallable(() -> Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection())
                .map(connection -> {
                    LocalDateTime date = LocalDateTime.now();
                    jdbcTemplate.update("""
                                    insert into logs (created_at)
                                    values (?)
                                    """,
                            date
                    );
                    logger.info("created transaction log: {}", date);
                    return connection;
                })
                .flatMap(connection -> {
                    StopWatch stopWatchA = new StopWatch();
                    stopWatchA.start();
                    PGConnection pgConnection = Util.fromThrowable(() -> connection.unwrap(PGConnection.class));
                    CopyManager copyAPI = Util.fromThrowable(pgConnection::getCopyAPI);
                    CopyIn copyIn = Util.fromThrowable(() -> copyAPI.copyIn(sql));
                    return stream
                            .filter(isValidCsvLine)
                            .map(it ->
                                    Util.fromThrowable(() -> {
                                        byte[] buf = (it + "\n").getBytes(StandardCharsets.UTF_8);
                                        copyIn.writeToCopy(buf, 0, buf.length);
                                        return it;
                                    })
                            )
                            .last()
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
                });
    }
}
