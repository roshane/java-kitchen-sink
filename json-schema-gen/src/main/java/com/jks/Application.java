package com.jks;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.*;
import com.jks.model.Order;

public class Application {
    public static void main(String[] args) {
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
