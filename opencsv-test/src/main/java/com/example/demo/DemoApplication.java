package com.example.demo;

import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class DemoApplication {
    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    record CsvUploadResponse(String message) {
    }

    @RestController
    static class HomeController {
        @GetMapping("/")
        ResponseEntity<Map<String, String>> home() {
            return ResponseEntity.ok(Map.of("message", "hello work mint linux rocks"));
        }

        @PostMapping(value = "/dataset", produces = MediaType.TEXT_PLAIN_VALUE)
        ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
            logger.info("received file: {}, size: {}", file.getOriginalFilename(), file.getSize());
            try {

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
                resaleFlatPriceList.forEach(it -> logger.info("item: {}", it));
                stopWatchB.stop();
                long timeToPrint = stopWatchB.getTotalTimeMillis();

                return ResponseEntity.ok(
                        new CsvUploadResponse(
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
                        ).toString()
                );
            } catch (Exception ex) {
                logger.error("Error", ex);
                return ResponseEntity.internalServerError()
                        .body(new CsvUploadResponse(ex.getMessage()).toString());
            }
        }
    }


}
