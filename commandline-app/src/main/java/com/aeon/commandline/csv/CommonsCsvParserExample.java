package com.aeon.commandline.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommonsCsvParserExample {
    private static final String STREET_FILED = "street";
    private static final String CITY_FIELD = "city";
    private static final String ZIP_FIELD = "zip";
    private static final String STATE_FIELD = "state";
    private static final String BEDS_FIELD = "beds";

    private static String csvFileName = "Sacramentorealestatetransactions.csv";

    //TODO remaining fields baths,sq__ft,type,sale_date,price,latitude,longitude"

    private static final Function<CSVRecord, Map<String, String>> rowMapper = record -> {
        Map<String, String> dto = new HashMap<>();
        dto.put(ZIP_FIELD, record.get(ZIP_FIELD));
        dto.put(CITY_FIELD, record.get(CITY_FIELD));
        dto.put(BEDS_FIELD, record.get(BEDS_FIELD));
        dto.put(STATE_FIELD, record.get(STATE_FIELD));
        dto.put(STREET_FILED, record.get(STREET_FILED));
        return dto;
    };

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        final CSVFormat csvFormat = CSVFormat.newFormat(',')
                .withHeader(STREET_FILED, CITY_FIELD, ZIP_FIELD, STATE_FIELD, BEDS_FIELD);
        final CsvMapper<Map<String, String>> mapper = new CsvMapper<>(csvFormat, rowMapper, true);

        try (
                InputStream inputStream = CommonsCsvParserExample.class
                        .getClassLoader()
                        .getResourceAsStream(csvFileName);
                final InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream))
        ) {
            final List<Map<String, String>> result = mapper.read(inputStreamReader);
            result.forEach(System.out::println);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            System.out.println("Runtime: " + (System.currentTimeMillis() - start) + " ms");
        }
    }
}
