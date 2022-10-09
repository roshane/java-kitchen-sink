package com.aeon.commandline.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class CsvMapper<T> {
    private final CSVFormat csvFormat;
    private final Charset charset = StandardCharsets.UTF_8;
    private final Function<CSVRecord, T> rowMapper;
    private final boolean skipHeader;

    public CsvMapper(CSVFormat csvFormat,
                     Function<CSVRecord, T> rowMapper,
                     boolean skipHeader) {
        this.csvFormat = csvFormat;
        this.rowMapper = rowMapper;
        this.skipHeader = skipHeader;
    }

    public List<T> read(final InputStreamReader inputStream) throws IOException {
        return CSVParser.parse(inputStream, csvFormat)
                .getRecords()
                .stream()
                .skip(skipHeader ? 1 : 0)
                .map(rowMapper)
                .collect(Collectors.toList());
    }
}
