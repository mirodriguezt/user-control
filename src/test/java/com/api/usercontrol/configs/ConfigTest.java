package com.api.usercontrol.configs;

import com.api.usercontrol.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class ConfigTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void should_use_json_nullable_module() throws JsonProcessingException {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JsonNullableModule());

        assertEquals(JsonNullable.of("user@fake.com"), mapper.readValue("{\"email\":\"user@fake.com\"}", UserDto.class).getEmail());
        assertEquals(JsonNullable.of(null), mapper.readValue("{\"email\":null}", UserDto.class).getEmail());
        assertNull(mapper.readValue("{}", UserDto.class).getEmail());
    }
}
