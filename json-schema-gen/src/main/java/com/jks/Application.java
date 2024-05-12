package com.jks;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.*;
import com.jks.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        jsonSchema();
//        gzipImage("/sample02.jpg", "./");
    }

    private static void gzipImage(String path, String directory) {
        final String[] pathTokens = path.split(File.pathSeparator);
        String fileName = pathTokens[pathTokens.length - 1];
        try (
                final InputStream inputStream = Application.class.getResourceAsStream(path);
        ) {
            final byte[] imageBytes = Objects.requireNonNull(inputStream, "File Input Stream is null")
                    .readAllBytes();
            Path outputFile = Files.createFile(Paths.get(directory, fileName + ".gzip"));
            final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(Files.newOutputStream(outputFile));
            gzipOutputStream.write(imageBytes);
            gzipOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void jsonSchema() {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_7, OptionPreset.PLAIN_JSON);
        configBuilder.forFields().withRequiredCheck(field -> field.getAnnotationConsideringFieldAndGetter(Nullable.class) == null);
        configBuilder.forTypesInGeneral().withTitleResolver(TypeScope::getSimpleTypeDescription);
        SchemaGeneratorConfig config = configBuilder
                .with(Option.NULLABLE_FIELDS_BY_DEFAULT, Option.INLINE_ALL_SCHEMAS)
                .build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode jsonSchema = generator.generateSchema(Order.class);

        System.out.println(jsonSchema.toPrettyString());
    }
}
