package com.api.usercontrol.mappers;

import com.api.usercontrol.dto.UserDto;
import com.api.usercontrol.models.UserModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper mapper;

    @Test
    public void should_update_all_entities_in_UserModel_except_Cpf() throws JsonProcessingException {
        UserDto update = UserDto.builder()
                .cpf("0987654321")
                .firstName("Updated First Name")
                .lastName("Updated Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1999, 12, 31)))
                .email(JsonNullable.of("updated@fake.com"))
                .build();

        UserModel destination = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email("old@fake.com")
                .build();

        UserModel expected = UserModel.builder()
                .cpf("1234567890")
                .firstName("Updated First Name")
                .lastName("Updated Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1999, 12, 31)))
                .email("updated@fake.com")
                .build();

        mapper.update(update, destination);
        assertEquals(expected.getCpf(), destination.getCpf());
        assertEquals(expected.getFirstName(), destination.getFirstName());
        assertEquals(expected.getLastName(), destination.getLastName());
        assertEquals(expected.getDateOfBirth(), destination.getDateOfBirth());
        assertEquals(expected.getEmail(), destination.getEmail());
    }

    @Test
    public void should_update_only_nullable_fields_in_UserModel() {
        UserDto update = UserDto.builder()
                .cpf("0987654321")
                .firstName(null)
                .lastName(null)
                .dateOfBirth(null)
                .email(JsonNullable.of(null))
                .build();

        UserModel destination = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email("old@fake.com")
                .build();

        UserModel expected = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email(null)
                .build();

        mapper.update(update, destination);
        assertEquals(expected.getCpf(), destination.getCpf());
        assertEquals(expected.getFirstName(), destination.getFirstName());
        assertEquals(expected.getLastName(), destination.getLastName());
        assertEquals(expected.getDateOfBirth(), destination.getDateOfBirth());
        assertEquals(expected.getEmail(), destination.getEmail());
    }

    @Test
    public void should_not_update_any_field_in_UserModel_when_field_nullable_is_undefined() {
        UserDto update = UserDto.builder()
                .cpf(null)
                .firstName(null)
                .lastName(null)
                .dateOfBirth(null)
                .email(JsonNullable.undefined())
                .build();

        UserModel destination = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email("old@fake.com")
                .build();

        UserModel expected = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email(null)
                .build();

        mapper.update(update, destination);
        assertEquals(expected.getCpf(), destination.getCpf());
        assertEquals(expected.getFirstName(), destination.getFirstName());
        assertEquals(expected.getLastName(), destination.getLastName());
        assertEquals(expected.getDateOfBirth(), destination.getDateOfBirth());
        assertEquals(expected.getEmail(), destination.getEmail());
    }

    @Test
    public void should_not_update_any_field_in_userModel_when_field_nullable_is_null() {
        UserDto update = UserDto.builder()
                .cpf(null)
                .firstName(null)
                .lastName(null)
                .dateOfBirth(null)
                .email(JsonNullable.of(null))
                .build();

        UserModel destination = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email("old@fake.com")
                .build();

        UserModel expected = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email(null)
                .build();

        mapper.update(update, destination);
        assertEquals(expected.getCpf(), destination.getCpf());
        assertEquals(expected.getFirstName(), destination.getFirstName());
        assertEquals(expected.getLastName(), destination.getLastName());
        assertEquals(expected.getDateOfBirth(), destination.getDateOfBirth());
        assertEquals(expected.getEmail(), destination.getEmail());
    }
}
