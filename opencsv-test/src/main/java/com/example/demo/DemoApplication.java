package com.example.demo;

import com.opencsv.bean.CsvToBeanBuilder;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyManager;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

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

        public <R> R withConnection(Function<Connection, R> consumer) {
            try (Connection connection = source.getConnection()) {
                return consumer.apply(connection);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    record CsvUploadResponse(String message) {
    }

    @RestController
    static class DummyController {

        @PostMapping(value = "/entity", produces = MediaType.TEXT_PLAIN_VALUE)
        ResponseEntity<String> rootEntity(@RequestBody Root root) {
            if (root instanceof TypeA a) {
                return ResponseEntity.ok(a.getClass().getCanonicalName());
            }
            if (root instanceof TypeB b) {
                return ResponseEntity.ok(b.getClass().getCanonicalName());
            }
            return ResponseEntity.internalServerError().body("unable to resolve");
        }

    }

    @RestController
    static class HomeController {

        private static final Configuration configuration = new Configuration();

        @GetMapping("/")
        ResponseEntity<Map<String, String>> home() {
            return ResponseEntity.ok(Map.of("message", "hello work mint linux rocks"));
        }

        @PostMapping(value = "/dataset", produces = MediaType.TEXT_PLAIN_VALUE)
        ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "useStream", defaultValue = "false", required = false) Boolean useStream) {
            logger.info("received file: {}, size: {}", file.getOriginalFilename(), file.getSize());
            try {
                var response = useStream ? processStream(file) : process(file);
                return ResponseEntity.ok(response.toString());
            } catch (Exception ex) {
                logger.error("Error", ex);
                return ResponseEntity.internalServerError()
                        .body(new CsvUploadResponse(ex.getMessage()).toString());
            }
        }

        @PostMapping(value = "/v2/dataset", produces = MediaType.TEXT_PLAIN_VALUE)
        ResponseEntity<String> uploadCsvV2(@RequestParam("file") MultipartFile file) {
            logger.info("received file: {}, size: {}", file.getOriginalFilename(), file.getSize());
            try {
                StopWatch watch = new StopWatch();
                watch.start();
                validateCsvFile(file);
                CsvUploadResponse insertResponse = insertCsvStream(file);
                watch.stop();
                return ResponseEntity.ok("""
                        insertResponse: [%s],
                        totalTimeTaken: [%dms]
                        """.formatted(insertResponse.message, watch.getTotalTimeMillis()));
            } catch (Exception ex) {
                logger.error("Error", ex);
                return ResponseEntity.internalServerError()
                        .body(new CsvUploadResponse(ex.getMessage()).toString());
            }
        }

        private static CsvUploadResponse process(MultipartFile file) throws Exception {
            StopWatch stopWatchA = new StopWatch(UUID.randomUUID().toString());
            stopWatchA.start();
            var resaleFlatPriceList = new CsvToBeanBuilder<ResaleFlatPrice>(new InputStreamReader(file.getInputStream()))
                    .withType(ResaleFlatPrice.class)
                    .build()
                    .parse();
            stopWatchA.stop();
            long timeTaken = stopWatchA.getTotalTimeMillis();

            StopWatch stopWatchB = new StopWatch(UUID.randomUUID().toString());
            stopWatchB.start();
            resaleFlatPriceList.forEach(it -> logger.debug("item: {}", it));
            stopWatchB.stop();
            long timeToPrint = stopWatchB.getTotalTimeMillis();
            return new CsvUploadResponse(
                    """
                            successfully processed: %d of lines,
                            time taken: %d ms,
                            time taken to print lines: %d ms
                            """.strip()
                            .formatted(
                                    resaleFlatPriceList.size(),
                                    timeTaken,
                                    timeToPrint
                            )
            );
        }

        private static CsvUploadResponse processStream(MultipartFile file) throws Exception {
            StopWatch stopWatchA = new StopWatch(UUID.randomUUID().toString());
            stopWatchA.start();
            var count = new AtomicInteger(0);
            new CsvToBeanBuilder<ResaleFlatPrice>(new InputStreamReader(file.getInputStream()))
                    .withType(ResaleFlatPrice.class)
                    .build()
                    .stream()
                    .forEach(it -> {
                        count.incrementAndGet();
                        logger.debug("item: {}", it);
                    });
            stopWatchA.stop();
            long timeTaken = stopWatchA.getTotalTimeMillis();

//            resaleFlatPriceList.forEach(it -> logger.info("item: {}", it));
            return new CsvUploadResponse(
                    """
                            successfully processed: %d of lines,
                            total time taken: %d ms,
                            """.strip()
                            .formatted(
                                    count.get(),
                                    timeTaken
                            )
            );
        }

        private static CsvUploadResponse processCustomStream(MultipartFile file) throws Exception {
            StopWatch stopWatchA = new StopWatch(UUID.randomUUID().toString());
            stopWatchA.start();
            var numOfLines = new AtomicInteger(0);
            try (
                    InputStream inputStream = file.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
            ) {

                bufferedReader.lines()
                        .map(HomeController::mapCsvStringToBean)
                        .forEach(it -> {
                            numOfLines.incrementAndGet();
                            logger.debug("item: {}", it);
                        });
            }
            stopWatchA.stop();
            long timeTaken = stopWatchA.getTotalTimeMillis();
            return new CsvUploadResponse(
                    """
                            successfully processed: %d of lines,
                            total time taken: %d ms,
                            """.strip()
                            .formatted(
                                    numOfLines.get(),
                                    timeTaken
                            )
            );
        }

        private static ResaleFlatPrice mapCsvStringToBean(String csvString) {
            if (!StringUtils.hasText(csvString)) {
                throw new RuntimeException("Invalid csv string: %s".formatted(csvString));
            }
            String[] tokens = csvString.split(",");
            ResaleFlatPrice resaleFlatPrice = new ResaleFlatPrice();
            resaleFlatPrice.setYearMonth(tokens[0]);
            resaleFlatPrice.setTown(tokens[1]);
            resaleFlatPrice.setFlatType(tokens[2]);
            resaleFlatPrice.setBlock(tokens[3]);
            resaleFlatPrice.setStreetName(tokens[4]);
            resaleFlatPrice.setStoreyRange(tokens[5]);
            resaleFlatPrice.setFloorArea(new BigDecimal(tokens[6]));
            resaleFlatPrice.setFlatMode(tokens[7]);
            resaleFlatPrice.setLeaseCommenceDate(Integer.parseInt(tokens[8]));
            resaleFlatPrice.setRemainingLease(tokens[9]);
            resaleFlatPrice.setResalePrice(new BigDecimal(tokens[10]));
            return resaleFlatPrice;
        }

        private static void validateCsvFile(MultipartFile file) {
            logger.info("validateCsvFile started ");
            StopWatch watch = new StopWatch(UUID.randomUUID().toString());
            watch.start();
            try (
                    InputStream inputStream = file.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
            ) {
                Optional<ResaleFlatPrice> maybeInvalid = bufferedReader.lines()
                        .map(HomeController::mapCsvStringToBean)
                        .filter(it -> !it.isValid())
                        .findFirst();
                if (maybeInvalid.isPresent()) {
                    throw new RuntimeException("Invalid ResaleFlatPrice: {%s}".formatted(maybeInvalid.get()));
                }

            } catch (Exception ex) {
                logger.error("Error", ex);
                throw new RuntimeException(ex);
            }
            watch.stop();
            long timeTaken = watch.getTotalTimeMillis();
            logger.info("validateCsvFile time taken: {}ms", timeTaken);
        }

        private static CsvUploadResponse insertCsvStream(MultipartFile file) {
            String sql = """
                     COPY resale_flat_price(month, town, flat_type, block, street_name, storey_range, floor_area_sqm, flat_model, lease_commence_date, remaining_lease, resale_price)
                     FROM STDIN
                     WITH (FORMAT csv)
                    """;
            StopWatch stopWatchA = new StopWatch(UUID.randomUUID().toString());
            stopWatchA.start();
            return configuration.withConnection(conn -> {
                try {
                    try (
                            InputStream inputStream = file.getInputStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
                    ) {
                        PGConnection pgConnection = conn.unwrap(PGConnection.class);
                        CopyManager copyAPI = pgConnection.getCopyAPI();
                        CopyIn copyIn = copyAPI.copyIn(sql);
                        bufferedReader.lines()
                                .forEach(csv -> {
                                    byte[] buf = (csv + "\n").getBytes(StandardCharsets.UTF_8);
                                    try {
                                        copyIn.writeToCopy(buf, 0, buf.length);
                                    } catch (Exception ex) {
                                        logger.error("Error", ex);
                                        throw new RuntimeException(ex);
                                    }
                                });
                        long updatedRows = copyIn.endCopy();
                        long handledRowCount = copyIn.getHandledRowCount();
                        stopWatchA.stop();
                        logger.info("UpdatedRows: {}, handledRowCount: {}, timeTaken: {}ms",
                                updatedRows, handledRowCount, stopWatchA.getTotalTimeMillis());
                        return new CsvUploadResponse(
                                """
                                        updated rows: %d,
                                        handled rows: %d,
                                        total time taken: %d ms,
                                        """.strip()
                                        .formatted(
                                                updatedRows,
                                                handledRowCount,
                                                stopWatchA.getTotalTimeMillis()
                                        )
                        );
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                } catch (Exception ex) {
                    logger.error("Error:", ex);
                    return new CsvUploadResponse(ex.getMessage());
                }

            });
        }
    }


}
