package com.api.usercontrol.controlllers;

import com.api.usercontrol.dto.UserDto;
import com.api.usercontrol.mappers.UserMapper;
import com.api.usercontrol.models.UserModel;
import com.api.usercontrol.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserControllerTest {
    private UserService userServiceMock;
    @Autowired
    private UserMapper mapper;
    private ValidatorFactoryImpl validatorFactoryImpMock;
    private ValidatorImpl validatorImpMock;
    private static MockedStatic<Validation> validationMock;
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userServiceMock = mock(UserService.class);
        validatorFactoryImpMock = mock(ValidatorFactoryImpl.class);
        validatorImpMock = mock(ValidatorImpl.class);
        validationMock = Mockito.mockStatic(Validation.class);

        userController = new UserController(userServiceMock, mapper);
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(userServiceMock);
        verifyNoMoreInteractions(validatorFactoryImpMock);
        verifyNoMoreInteractions(validatorImpMock);
        validationMock.close();
    }

    @Test
    public void should_save_an_user_if_there_are_no_conflicts() {
        UserDto userDto = UserDto.builder()
                .cpf("1234567890")
                .firstName("First Name")
                .lastName("Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1999, 12, 31)))
                .email(JsonNullable.of("updated@fake.com"))
                .build();

        UserModel userModel = UserModel.builder()
                .cpf("1234567890")
                .firstName("First Name")
                .lastName("Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1999, 12, 31)))
                .email("updated@fake.com")
                .registrationDate(LocalDateTime.of(2022, 9, 15, 1, 1, 1))
                .build();

        ArgumentCaptor<UserModel> userModelArgumentCaptor = ArgumentCaptor.forClass(UserModel.class);
        when(userServiceMock.validateUser(any(UserModel.class), eq(true))).thenReturn(StringUtils.EMPTY);
        when(userServiceMock.save(any(UserModel.class))).thenReturn(userModel);

        ResponseEntity<Object> responseEntity = userController.saveUser(userDto);

        verify(userServiceMock).validateUser(userModelArgumentCaptor.capture(), eq(true));
        verify(userServiceMock).save((any(UserModel.class)));

        UserModel userModelSaved = userModelArgumentCaptor.getValue();

        assertThat(userModelSaved.getCpf(), is("1234567890"));
        assertThat(userModelSaved.getFirstName(), is("First Name"));
        assertThat(userModelSaved.getLastName(), is("Last Name"));
        assertThat(userModelSaved.getDateOfBirth(), is(Date.valueOf(LocalDate.of(1999, 12, 31))));
        assertThat(userModelSaved.getEmail(), is("updated@fake.com"));
        assertThat(userModelSaved.getRegistrationDate(), notNullValue());
        assertThat(responseEntity.getStatusCodeValue(), is(201));
    }

    @Test
    public void should_not_save_an_user_when_exists_conflicts() {
        UserDto userDto = UserDto.builder()
                .cpf("1234567890")
                .firstName("First Name")
                .lastName("Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1999, 12, 31)))
                .email(JsonNullable.of("updated@fake.com"))
                .build();

        ArgumentCaptor<UserModel> userModelArgumentCaptor = ArgumentCaptor.forClass(UserModel.class);

        when(userServiceMock.validateUser(any(UserModel.class), eq(true))).thenReturn("There are conflicts");

        ResponseEntity<Object> responseEntity = userController.saveUser(userDto);

        verify(userServiceMock).validateUser(userModelArgumentCaptor.capture(), eq(true));

        UserModel userModel = userModelArgumentCaptor.getValue();

        assertThat(userModel.getCpf(), is("1234567890"));
        assertThat(userModel.getFirstName(), is("First Name"));
        assertThat(userModel.getLastName(), is("Last Name"));
        assertThat(userModel.getDateOfBirth(), is(Date.valueOf(LocalDate.of(1999, 12, 31))));
        assertThat(userModel.getEmail(), is("updated@fake.com"));
        assertThat(responseEntity.getStatusCodeValue(), is(409));
    }

    @Test
    public void should_return_an_page_of_userModel_records() {
        UserModel userModel = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1998, 12, 31)))
                .email("old_email@fake.com")
                .registrationDate(LocalDateTime.of(2022, 9, 15, 1, 1, 1))
                .build();

        PageRequest pageRequest = PageRequest.of(1, 10);
        List<UserModel> userModelList = Arrays.asList(userModel);
        Page<UserModel> pageUserModel = new PageImpl<>(userModelList, pageRequest, userModelList.size());

        when(userServiceMock.findAll(Mockito.any(PageRequest.class))).thenReturn(pageUserModel);

        ResponseEntity<Page<UserModel>> responseEntity = userController.getAllUsers(pageRequest);

        verify(userServiceMock).findAll(pageRequest);
        PageImpl returnedPage = (PageImpl) responseEntity.getBody();
        var returnedPageContentList = returnedPage.getContent();
        assertThat(responseEntity.getStatusCodeValue(), is(200));
        assertThat(returnedPage, is(notNullValue()));
        assertThat(returnedPageContentList.size(), is(1));
    }

    @Test
    public void should_return_an_userModel_when_find_user_by_cpf_and_exists() {
        UserModel userModel = UserModel.builder().build();
        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.of(userModel));

        ResponseEntity<Object> responseEntity = userController.getUser("1234567890");

        verify(userServiceMock).findByCpf("1234567890");
        assertThat(responseEntity.getStatusCodeValue(), is(200));
    }

    @Test
    public void should_return_an_userModel_when_find_user_by_cpf_and_not_exists() {
        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.ofNullable(null));

        ResponseEntity<Object> responseEntity = userController.getUser("1234567890");

        verify(userServiceMock).findByCpf("1234567890");
        assertThat(responseEntity.getStatusCodeValue(), is(404));
        assertThat(responseEntity.getBody(), is("User not found"));
    }

    @Test
    public void should_return_a_list_with_userModel_records_when_find_user_by_firstName() {
        List<UserModel> userModelList = new ArrayList<>();

        when(userServiceMock.findByFirstNameContains("name")).thenReturn(userModelList);

        ResponseEntity<List<UserModel>> responseEntity = userController.getUserbyFirstName("name");

        verify(userServiceMock).findByFirstNameContains("name");
        assertThat(responseEntity.getStatusCodeValue(), is(200));
    }

    @Test
    public void should_return_a_list_with_userModel_records_when_find_user_by_lastName() {
        List<UserModel> userModelList = new ArrayList<>();

        when(userServiceMock.findByLastNameContains("name")).thenReturn(userModelList);

        ResponseEntity<List<UserModel>> responseEntity = userController.getUserbyLastName("name");

        verify(userServiceMock).findByLastNameContains("name");
        assertThat(responseEntity.getStatusCodeValue(), is(200));
    }

    @Test
    public void should_delete_an_user_when_find_user_by_cpf_and_exists() {
        UserModel userModel = UserModel.builder().build();
        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.of(userModel));
        doNothing().when(userServiceMock).delete(userModel);

        ResponseEntity<Object> responseEntity = userController.deleteUser("1234567890");

        verify(userServiceMock).findByCpf("1234567890");
        verify(userServiceMock).delete(userModel);
        assertThat(responseEntity.getStatusCodeValue(), is(200));
        assertThat(responseEntity.getBody(), is("User has been deleted"));
    }

    @Test
    public void should_not_delete_an_user_when_find_user_by_cpf_and_this_not_exists() {
        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.ofNullable(null));

        ResponseEntity<Object> responseEntity = userController.deleteUser("1234567890");

        verify(userServiceMock).findByCpf("1234567890");
        assertThat(responseEntity.getStatusCodeValue(), is(404));
        assertThat(responseEntity.getBody(), is("User not found"));
    }

    @Test
    public void should_update_all_field_an_some_user_when_this_exists() {
        UserDto userDto = UserDto.builder()
                .firstName("New First Name")
                .lastName("New Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1989, 01, 1)))
                .email(JsonNullable.of("new_email@fake.com"))
                .build();

        UserModel userModel = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1998, 12, 31)))
                .email("old_email@fake.com")
                .registrationDate(LocalDateTime.of(2022, 9, 15, 1, 1, 1))
                .build();

        Set<ConstraintViolation<UserDto>> constraintViolations = new HashSet<>();

        ArgumentCaptor<UserModel> userModelArgumentCaptor = ArgumentCaptor.forClass(UserModel.class);

        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.of(userModel));
        validationMock.when(() -> Validation.buildDefaultValidatorFactory()).thenReturn(validatorFactoryImpMock);
        when(validatorFactoryImpMock.getValidator()).thenReturn(validatorImpMock);
        when(validatorImpMock.validate(any(UserDto.class))).thenReturn(constraintViolations);
        when(userServiceMock.validateUser(any(UserModel.class), eq(false))).thenReturn(StringUtils.EMPTY);
        when(userServiceMock.save(any(UserModel.class))).thenReturn(any(UserModel.class));

        ResponseEntity<Object> responseEntity = userController.updateUser("1234567890", userDto);

        verify(userServiceMock).findByCpf("1234567890");
        validationMock.verify(Validation::buildDefaultValidatorFactory, times(1));
        verify(validatorFactoryImpMock).getValidator();
        verify(validatorImpMock).validate(any(UserDto.class));
        verify(userServiceMock).validateUser(userModelArgumentCaptor.capture(), eq(false));
        verify(userServiceMock).save((any(UserModel.class)));

        UserModel userModelSaved = userModelArgumentCaptor.getValue();

        assertThat(userModelSaved.getCpf(), is("1234567890"));
        assertThat(userModelSaved.getFirstName(), is("New First Name"));
        assertThat(userModelSaved.getLastName(), is("New Last Name"));
        assertThat(userModelSaved.getDateOfBirth(), is(Date.valueOf(LocalDate.of(1989, 1, 1))));
        assertThat(userModelSaved.getEmail(), is("new_email@fake.com"));
        assertThat(userModelSaved.getRegistrationDate(), is(LocalDateTime.of(2022, 9, 15, 1, 1, 1)));
        assertThat(responseEntity.getStatusCodeValue(), is(200));
    }

    @Test
    public void should_update_one_field_an_some_user_when_this_exists() {
        UserDto userDto = UserDto.builder()
                .firstName("New First Name")
                .build();

        UserModel userModel = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1998, 12, 31)))
                .email("old_email@fake.com")
                .registrationDate(LocalDateTime.of(2022, 9, 15, 1, 1, 1))
                .build();

        Set<ConstraintViolation<UserDto>> constraintViolations = new HashSet<>();

        ArgumentCaptor<UserModel> userModelArgumentCaptor = ArgumentCaptor.forClass(UserModel.class);

        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.of(userModel));
        validationMock.when(Validation::buildDefaultValidatorFactory).thenReturn(validatorFactoryImpMock);
        when(validatorFactoryImpMock.getValidator()).thenReturn(validatorImpMock);
        when(validatorImpMock.validate(any(UserDto.class))).thenReturn(constraintViolations);
        when(userServiceMock.validateUser(any(UserModel.class), eq(false))).thenReturn(StringUtils.EMPTY);
        when(userServiceMock.save(any(UserModel.class))).thenReturn(any(UserModel.class));

        ResponseEntity<Object> responseEntity = userController.updateUser("1234567890", userDto);

        verify(userServiceMock).findByCpf("1234567890");
        validationMock.verify(Validation::buildDefaultValidatorFactory, times(1));
        verify(validatorFactoryImpMock).getValidator();
        verify(validatorImpMock).validate(any(UserDto.class));
        verify(userServiceMock).validateUser(userModelArgumentCaptor.capture(), eq(false));
        verify(userServiceMock).save((any(UserModel.class)));

        UserModel userModelSaved = userModelArgumentCaptor.getValue();

        assertThat(userModelSaved.getCpf(), is("1234567890"));
        assertThat(userModelSaved.getFirstName(), is("New First Name"));
        assertThat(userModelSaved.getLastName(), is("Old Last Name"));
        assertThat(userModelSaved.getDateOfBirth(), is(Date.valueOf(LocalDate.of(1998, 12, 31))));
        assertThat(userModelSaved.getEmail(), is("old_email@fake.com"));
        assertThat(userModelSaved.getRegistrationDate(), is(LocalDateTime.of(2022, 9, 15, 1, 1, 1)));
        assertThat(responseEntity.getStatusCodeValue(), is(200));
    }

    @Test
    public void should_update_only_nullable_field_with_null_value_an_some_user_when_this_exists() {
        UserDto userDto = UserDto.builder()
                .email(JsonNullable.of(null))
                .build();

        UserModel userModel = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1998, 12, 31)))
                .email("old_email@fake.com")
                .registrationDate(LocalDateTime.of(2022, 9, 15, 1, 1, 1))
                .build();

        Set<ConstraintViolation<UserDto>> constraintViolations = new HashSet<>();

        ArgumentCaptor<UserModel> userModelArgumentCaptor = ArgumentCaptor.forClass(UserModel.class);

        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.of(userModel));
        validationMock.when(() -> Validation.buildDefaultValidatorFactory()).thenReturn(validatorFactoryImpMock);
        when(validatorFactoryImpMock.getValidator()).thenReturn(validatorImpMock);
        when(validatorImpMock.validate(any(UserDto.class))).thenReturn(constraintViolations);
        when(userServiceMock.validateUser(any(UserModel.class), eq(false))).thenReturn(StringUtils.EMPTY);
        when(userServiceMock.save(any(UserModel.class))).thenReturn(any(UserModel.class));

        ResponseEntity<Object> responseEntity = userController.updateUser("1234567890", userDto);

        verify(userServiceMock).findByCpf("1234567890");
        validationMock.verify(Validation::buildDefaultValidatorFactory, times(1));
        verify(validatorFactoryImpMock).getValidator();
        verify(validatorImpMock).validate(any(UserDto.class));
        verify(userServiceMock).validateUser(userModelArgumentCaptor.capture(), eq(false));
        verify(userServiceMock).save((any(UserModel.class)));

        UserModel userModelSaved = userModelArgumentCaptor.getValue();

        assertThat(userModelSaved.getCpf(), is("1234567890"));
        assertThat(userModelSaved.getFirstName(), is("Old First Name"));
        assertThat(userModelSaved.getLastName(), is("Old Last Name"));
        assertThat(userModelSaved.getDateOfBirth(), is(Date.valueOf(LocalDate.of(1998, 12, 31))));
        assertThat(userModelSaved.getEmail(), is(nullValue()));
        assertThat(userModelSaved.getRegistrationDate(), is(LocalDateTime.of(2022, 9, 15, 1, 1, 1)));
        assertThat(responseEntity.getStatusCodeValue(), is(200));
    }

    @Test
    public void should_no_update_field_cpf_when_this_is_present_in_the_request_body() {
        UserDto userDto = UserDto.builder()
                .cpf("4565789798")
                .build();

        UserModel userModel = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1998, 12, 31)))
                .email("old_email@fake.com")
                .registrationDate(LocalDateTime.of(2022, 9, 15, 1, 1, 1))
                .build();

        Set<ConstraintViolation<UserDto>> constraintViolations = new HashSet<>();

        ArgumentCaptor<UserModel> userModelArgumentCaptor = ArgumentCaptor.forClass(UserModel.class);

        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.of(userModel));
        validationMock.when(() -> Validation.buildDefaultValidatorFactory()).thenReturn(validatorFactoryImpMock);
        when(validatorFactoryImpMock.getValidator()).thenReturn(validatorImpMock);
        when(validatorImpMock.validate(any(UserDto.class))).thenReturn(constraintViolations);
        when(userServiceMock.validateUser(any(UserModel.class), eq(false))).thenReturn(StringUtils.EMPTY);
        when(userServiceMock.save(any(UserModel.class))).thenReturn(any(UserModel.class));

        ResponseEntity<Object> responseEntity = userController.updateUser("1234567890", userDto);

        verify(userServiceMock).findByCpf("1234567890");
        validationMock.verify(Validation::buildDefaultValidatorFactory, times(1));
        verify(validatorFactoryImpMock).getValidator();
        verify(validatorImpMock).validate(any(UserDto.class));
        verify(userServiceMock).validateUser(userModelArgumentCaptor.capture(), eq(false));
        verify(userServiceMock).save((any(UserModel.class)));

        UserModel userModelSaved = userModelArgumentCaptor.getValue();

        assertThat(userModelSaved.getCpf(), is("1234567890"));
        assertThat(userModelSaved.getFirstName(), is("Old First Name"));
        assertThat(userModelSaved.getLastName(), is("Old Last Name"));
        assertThat(userModelSaved.getDateOfBirth(), is(Date.valueOf(LocalDate.of(1998, 12, 31))));
        assertThat(userModelSaved.getEmail(), is("old_email@fake.com"));
        assertThat(userModelSaved.getRegistrationDate(), is(LocalDateTime.of(2022, 9, 15, 1, 1, 1)));
        assertThat(responseEntity.getStatusCodeValue(), is(200));
    }

    @Test
    public void should_no_update_an_user_when_cpf_not_exists() {
        UserDto userDto = UserDto.builder().build();
        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.ofNullable(null));

        ResponseEntity<Object> responseEntity = userController.updateUser("1234567890", userDto);

        verify(userServiceMock).findByCpf("1234567890");
        assertThat(responseEntity.getStatusCodeValue(), is(404));
        assertThat(responseEntity.getBody(), is("User not found"));
    }

    @Test
    public void should_no_update_an_user_when_constraint_violation_is_present() {
        ConstraintViolation<UserDto> constraintViolationMock = mock(ConstraintViolation.class);

        UserDto userDto = UserDto.builder()
                .email(JsonNullable.of("incorrectEmail"))
                .build();

        UserModel userModel = UserModel.builder()
                .cpf("1234567890")
                .firstName("Old First Name")
                .lastName("Old Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1998, 12, 31)))
                .email("old_email@fake.com")
                .registrationDate(LocalDateTime.of(2022, 9, 15, 1, 1, 1))
                .build();

        Set<ConstraintViolation<UserDto>> constraintViolationsMock = new HashSet<>();
        constraintViolationsMock.add(constraintViolationMock);

        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.of(userModel));
        validationMock.when(() -> Validation.buildDefaultValidatorFactory()).thenReturn(validatorFactoryImpMock);
        when(validatorFactoryImpMock.getValidator()).thenReturn(validatorImpMock);
        when(validatorImpMock.validate(any(UserDto.class))).thenReturn(constraintViolationsMock);

        ResponseEntity<Object> responseEntity = userController.updateUser("1234567890", userDto);

        verify(userServiceMock).findByCpf("1234567890");
        validationMock.verify(Validation::buildDefaultValidatorFactory, times(1));
        verify(validatorFactoryImpMock).getValidator();
        verify(validatorImpMock).validate(any(UserDto.class));

        assertThat(responseEntity.getStatusCodeValue(), is(409));
        ArrayList bodyArrayList = (ArrayList) responseEntity.getBody();
        assertThat(bodyArrayList.size(), is(1));
    }

    @Test
    public void should_no_update_an_user_when_exists_validateModifiedUserLeyend() {
        UserDto userDto = UserDto.builder().build();
        UserModel userModel = UserModel.builder().build();
        Set<ConstraintViolation<UserDto>> constraintViolations = new HashSet<>();

        when(userServiceMock.findByCpf("1234567890")).thenReturn(Optional.of(userModel));
        validationMock.when(() -> Validation.buildDefaultValidatorFactory()).thenReturn(validatorFactoryImpMock);
        when(validatorFactoryImpMock.getValidator()).thenReturn(validatorImpMock);
        when(validatorImpMock.validate(any(UserDto.class))).thenReturn(constraintViolations);
        when(userServiceMock.validateUser(any(UserModel.class), eq(false))).thenReturn("There is conflicts");
        when(userServiceMock.save(any(UserModel.class))).thenReturn(any(UserModel.class));

        ResponseEntity<Object> responseEntity = userController.updateUser("1234567890", userDto);

        verify(userServiceMock).findByCpf("1234567890");
        validationMock.verify(Validation::buildDefaultValidatorFactory);
        verify(validatorFactoryImpMock).getValidator();
        verify(validatorImpMock).validate(any(UserDto.class));
        verify(userServiceMock).validateUser(userModel, false);

        assertThat(responseEntity.getStatusCodeValue(), is(409));
        assertThat(responseEntity.getBody(), is("There is conflicts"));
    }
}
