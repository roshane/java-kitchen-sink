package com.example.demo;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Controller
public class CsvController {
    private static final int csvColumnCount = 11;
    private static final Predicate<String> isValidCsvLine = it -> it.split(",").length == csvColumnCount;
    private static final Logger logger = LoggerFactory.getLogger(CsvController.class);
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_QUERY = """
            insert into resale_flat_price(month,
                                          town,
                                          flat_type,
                                          block,
                                          street_name,
                                          storey_range,
                                          floor_area_sqm,
                                          flat_model,
                                          lease_commence_date,
                                          remaining_lease,
                                          resale_price)
            values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

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
    ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @PostMapping("/create")
    ResponseEntity<Map<String, String>> http201() {
        return ResponseEntity
                .created(URI.create("/create/%s".formatted(UUID.randomUUID().toString())))
                .body(Map.of("message", "created"));
    }

    @Transactional
    @PostMapping(value = "/dataset/resale_flat_price")
    ResponseEntity<Map<String, Integer>> rootEntity(@RequestParam("file") MultipartFile file) {
        logger.info("Processing upload request");
        Stream<String> stream = Util.fromThrowable(() -> {
            InputStream inputStream = file.getInputStream();
            String s = new String(inputStream.readAllBytes());
            inputStream.close();
            String[] lines = s.split(System.lineSeparator());
            return Arrays.stream(lines);
        });
        int count = persistCsvDataV2(stream);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @Transactional
    @PostMapping(value = "/dataset/resale_flat_price/v1")
    ResponseEntity<Map<String, Integer>> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("Processing upload request: {}, size: {}KB", file.getName(), file.getSize() / (1024));
        StopWatch a = new StopWatch("v1");
        int count = insertBatch(file, a);
        throw new RuntimeException("YOLO");
//        return ResponseEntity.ok(Map.of("count", count));
    }

    private int insertBatch(MultipartFile file, StopWatch a) {
        logger.info("Batch insert started");
        return Util.fromThrowable(() -> {
            var count = 0;
            a.start();
            try (
                    var inputStream = file.getInputStream();
                    var inputStreamReader = new InputStreamReader(inputStream);
                    var bufferedReader = new BufferedReader(inputStreamReader)
            ) {
                List<String> lines = bufferedReader.lines().toList();
                List<List<String>> partionList = ListUtils.partition(lines, 5000);
                count = partionList
                        .stream()
                        .map(it -> {
                            return it.stream()
                                    .map(line -> line.split(","))
                                    .map(Object[].class::cast)
                                    .toList();
                        })
                        .map(it -> {
                            return jdbcTemplate.batchUpdate(INSERT_QUERY, it).length;
                        })
                        .reduce(Integer::sum)
                        .orElse(0);

            }
            a.stop();
            logger.info("Completed in {}(s)", a.getTotalTime(TimeUnit.SECONDS));
            return count;
        });
    }

    private int insertSequential(MultipartFile file, StopWatch a) {
        int count = Util.fromThrowable(() -> {
            a.start();
            try (
                    var inputStream = file.getInputStream();
                    var inputStreamReader = new InputStreamReader(inputStream);
                    var bufferedReader = new BufferedReader(inputStreamReader)
            ) {
                int result = bufferedReader
                        .lines()
                        .map(it -> {
                            Object[] args = it.split(",");
                            return jdbcTemplate.update(INSERT_QUERY, args);
                        })
                        .reduce(0, Integer::sum);
                a.stop();
                logger.info("Time taken: {}(s)", a.getTotalTime(TimeUnit.SECONDS));
                return result;
            }
        });
        return count;
    }

    @DeleteMapping("/dataset/resale_flat_price")
    ResponseEntity<Boolean> deleteEntries() {
        logger.info("Processing delete request");
        int rowCount = jdbcTemplate.update("delete from resale_flat_price");
        return ResponseEntity.ok(rowCount > 0);
    }

    @GetMapping("/dataset/resale_flat_price/size")
    ResponseEntity<Integer> countResaleFlatPriceRecords() {
        logger.info("Processing size request");
        Integer count = jdbcTemplate.queryForObject("select count(*) as count from resale_flat_price", Integer.class);
        return ResponseEntity.ok(count);
    }


    Integer persistCsvDataV2(Stream<String> stream) {
        List<Object[]> dataList = stream
//                .filter(isValidCsvLine)
                .map(line -> line.split(","))
//                .peek(line -> logger.info("line: {}", Arrays.toString(line)))
                .map(it -> ((Object[]) it))
                .toList();
        logger.info("Long task started");
        {
            LocalDateTime date = LocalDateTime.now();
            jdbcTemplate.update("""
                            insert into logs (created_at)
                            values (?)
                            """,
                    date
            );
            logger.info("Created transaction log: {}", date);
//            LocalDateTime time = longTask(5000);
//            logger.info("Long task completed: {}", time);

        }
        return Util.fromThrowable(() -> {
            int[] batchUpdate = jdbcTemplate.batchUpdate(INSERT_QUERY, dataList);
            logger.info("Transaction completed");
//            throw new RuntimeException("SOMETHING bad happened");
            return batchUpdate.length;
        });


    }

    private LocalDateTime longTask(int delay) {
        Util.fromThrowable(() -> {
            Thread.sleep(Duration.ofMillis(delay));
            return true;
        });
        return LocalDateTime.now();
    }


//    private String persistCsvData(Flux<String> stream) {
//        String sql = """
//                 COPY resale_flat_price(month, town, flat_type, block, street_name, storey_range, floor_area_sqm, flat_model, lease_commence_date, remaining_lease, resale_price)
//                 FROM STDIN
//                 WITH (FORMAT csv)
//                """;
//        return Mono.fromCallable(() -> Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection())
//                .map(connection -> {
//                    LocalDateTime date = LocalDateTime.now();
//                    jdbcTemplate.update("""
//                                    insert into logs (created_at)
//                                    values (?)
//                                    """,
//                            date
//                    );
//                    logger.info("created transaction log: {}", date);
//                    return connection;
//                })
//                .flatMap(connection -> {
//                    StopWatch stopWatchA = new StopWatch();
//                    stopWatchA.start();
//                    PGConnection pgConnection = Util.fromThrowable(() -> connection.unwrap(PGConnection.class));
//                    CopyManager copyAPI = Util.fromThrowable(pgConnection::getCopyAPI);
//                    CopyIn copyIn = Util.fromThrowable(() -> copyAPI.copyIn(sql));
//                    return stream
//                            .filter(isValidCsvLine)
//                            .map(it ->
//                                    Util.fromThrowable(() -> {
//                                        byte[] buf = (it + "\n").getBytes(StandardCharsets.UTF_8);
//                                        copyIn.writeToCopy(buf, 0, buf.length);
//                                        return it;
//                                    })
//                            )
//                            .last()
//                            .map("lastLine processed: [%s]"::formatted)
//                            .doOnSuccess(ignore -> {
//                                try {
//                                    long linesProcessed = copyIn.endCopy();
//                                    logger.info("linesProcessed: {}", linesProcessed);
//                                    stopWatchA.stop();
//                                    logger.info("timeTaken: {}ms", stopWatchA.getTotalTimeMillis());
//
//                                } catch (SQLException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            });
//                });
//    }
}
