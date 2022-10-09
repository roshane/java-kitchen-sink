package com.aeon.restrictionpoc;

import com.aeon.restrictionpoc.dto.AddressRestriction;
import com.aeon.restrictionpoc.mappers.AddressRestrictionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootTest(args = {"--spring.profiles.active=test"})
class RestrictionPocApplicationTests {

    @Autowired
    private AddressRestrictionMapper addressRestrictionMapper;

    @Autowired
    private static DataSource dataSource;

    private static JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private JdbcTemplate createTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Test
    void contextLoads() {
//        Assertions.assertNotNull(dataSource);
        Assertions.assertNotNull(addressRestrictionMapper);
        Assertions.assertNotNull(objectMapper);

        Assertions.assertTrue(objectMapper.canDeserialize(SimpleType.constructUnsafe(AddressRestriction.class)));
    }

//    @AfterEach
    void afterEach() {
        createTemplate().execute("delete from address_restriction");
    }

    @Test
    void testFindAll() {
        Assertions.assertTrue(() -> addressRestrictionMapper.findAll().isEmpty());
    }


}
