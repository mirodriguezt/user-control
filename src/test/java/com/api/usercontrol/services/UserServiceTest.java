package com.api.usercontrol.services;

import com.api.usercontrol.models.UserModel;
import com.api.usercontrol.repositories.UserRepository;
import com.api.usercontrol.utils.Tools;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {
    private UserRepository userRepositoryMock;
    private Logger loggerMock;
    private static MockedStatic<Tools> toolsMock;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepositoryMock = mock(UserRepository.class);
        loggerMock = mock(Logger.class);
        toolsMock = Mockito.mockStatic(Tools.class);

        WhiteboxImpl.setInternalState(UserService.class, "log", loggerMock);
        userService = new UserService(userRepositoryMock);
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(userRepositoryMock);
        verifyNoMoreInteractions(loggerMock);

        toolsMock.close();
    }

    @Test
    public void should_save_a_user() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .firstName("First Name")
                .lastName("Last Name")
                .dateOfBirth(Date.valueOf(LocalDate.of(1989, 1, 1)))
                .email("email@fake.com")
                .registrationDate(LocalDateTime.of(2022, 9, 15, 1, 1, 1))
                .build();

        when(userRepositoryMock.save(any(UserModel.class))).thenReturn(userModelToSave);

        UserModel userModelReturned = userService.save(userModelToSave);

        verify(userRepositoryMock, times(1)).save(any(UserModel.class));
        verify(loggerMock).info("User saved -> cpf:{}", "1234567890");
        assertThat(userModelReturned.getCpf(), is("1234567890"));
        assertThat(userModelReturned.getFirstName(), is("First Name"));
        assertThat(userModelReturned.getLastName(), is("Last Name"));
        assertThat(userModelReturned.getDateOfBirth(), is(Date.valueOf(LocalDate.of(1989, 1, 1))));
        assertThat(userModelReturned.getEmail(), is("email@fake.com"));
        assertThat(userModelReturned.getRegistrationDate(), is(LocalDateTime.of(2022, 9, 15, 1, 1, 1)));
    }

    @Test
    public void should_return_true_when_an_user_is_searcher_by_cpf_and_exists() {
        when(userRepositoryMock.existsByCpf(any(String.class))).thenReturn(true);

        boolean existsCpf = userService.existsByCpf("1234567890");

        verify(userRepositoryMock).existsByCpf(any(String.class));
        assertTrue(existsCpf);
    }

    @Test
    public void should_return_true_when_an_user_is_searcher_by_cpf_and_not_exists() {
        when(userRepositoryMock.existsByCpf(any(String.class))).thenReturn(false);

        boolean existsCpf = userService.existsByCpf("1234567890");

        verify(userRepositoryMock).existsByCpf(any(String.class));
        assertFalse(existsCpf);
    }

    @Test
    public void should_return_true_when_an_email_is_already_registered_with_a_user() {
        when(userRepositoryMock.existsByEmail(any(String.class))).thenReturn(true);

        boolean existEmail = userService.existsByEmail("email@fake.com");

        verify(userRepositoryMock).existsByEmail(any(String.class));
        assertTrue(existEmail);
    }

    @Test
    public void should_return_false_when_an_email_is_not_registered_with_a_user() {
        when(userRepositoryMock.existsByEmail(any(String.class))).thenReturn(false);

        boolean emailexists = userService.existsByEmail("email@fake.com");

        verify(userRepositoryMock).existsByEmail(any(String.class));
        assertFalse(emailexists);
    }

    @Test
    void should_return_a_page_with_user_found() {
        UserModel userModel = UserModel.builder().build();
        PageRequest pageRequest = PageRequest.of(1, 10);
        List<UserModel> userModelList = Arrays.asList(userModel);
        Page<UserModel> pageUserModel = new PageImpl<>(userModelList, pageRequest, userModelList.size());

        when(userRepositoryMock.findAll(Mockito.any(PageRequest.class))).thenReturn(pageUserModel);

        Page<UserModel> returnedPage = userService.findAll(pageRequest);

        verify(userRepositoryMock).findAll(pageRequest);
        var returnedPageContentList = returnedPage.getContent();
        assertThat(returnedPage, is(notNullValue()));
        assertThat(returnedPageContentList.size(), is(1));
    }

    @Test
    void should_return_a_list_of_users_where_the_first_name_matches_the_given_search_text() {
        UserModel userModel = UserModel.builder().build();
        List<UserModel> userModelList = Arrays.asList(userModel);

        when(userRepositoryMock.findByFirstNameContains("name")).thenReturn(userModelList);

        List<UserModel> userModelListReturned = userService.findByFirstNameContains("name");

        verify(userRepositoryMock).findByFirstNameContains("name");
        assertThat(userModelListReturned, is(notNullValue()));
        assertThat(userModelListReturned.size(), is(1));
    }

    @Test
    void should_return_a_list_of_users_where_the_last_name_matches_the_given_search_text() {
        UserModel userModel = UserModel.builder().build();
        List<UserModel> userModelList = Arrays.asList(userModel);

        when(userRepositoryMock.findByLastNameContains("name")).thenReturn(userModelList);

        List<UserModel> userModelListReturned = userService.findByLastNameContains("name");

        verify(userRepositoryMock).findByLastNameContains("name");
        assertThat(userModelListReturned, is(notNullValue()));
        assertThat(userModelListReturned.size(), is(1));
    }

    @Test
    void should_return_a_user_where_the_cpf_matches_the_given_search_cpf() {
        UserModel userModel = UserModel.builder().build();

        when(userRepositoryMock.findById("1234567890")).thenReturn(Optional.of(userModel));

        Optional<UserModel> userModelReturned = userService.findByCpf("1234567890");

        verify(userRepositoryMock).findById("1234567890");
        userModelReturned.ifPresent(user -> {
            assertThat(user, is(userModel));
        });
    }

    @Test
    void should_return_a_user_where_the_cpf_not_matches_the_given_search_cpf() {
        when(userRepositoryMock.findById("1234567890")).thenReturn(Optional.empty());

        Optional<UserModel> userModelReturned = userService.findByCpf("1234567890");

        verify(userRepositoryMock).findById("1234567890");
        assertThat(userModelReturned.isPresent(), is(false));
    }


    @Test
    public void should_delete_a_user() {
        UserModel userModelTodelete = UserModel.builder().cpf("1234567890").build();

        doNothing().when(userRepositoryMock).delete(userModelTodelete);

        userService.delete(userModelTodelete);

        verify(userRepositoryMock).delete(any(UserModel.class));
        verify(loggerMock).info("User deleted -> cpf:{}", "1234567890");
    }

    @Test
    void should_validate_a_new_user_and_his_record_is_valid() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .dateOfBirth(Date.valueOf(LocalDate.of(1989, 1, 1)))
                .email("email@fake.com")
                .build();

        toolsMock.when(() -> Tools.isValidCpf("1234567890")).thenReturn(true);
        when(userRepositoryMock.existsByCpf("1234567890")).thenReturn(false);
        toolsMock.when(() -> Tools.isValidEmail("email@fake.com")).thenReturn(true);
        when(userRepositoryMock.existsByEmail("email@fake.com")).thenReturn(false);

        String leyend = userService.validateUser(userModelToSave, true);

        toolsMock.verify(() -> Tools.isValidCpf("1234567890"));
        verify(userRepositoryMock).existsByCpf("1234567890");
        toolsMock.verify(() -> Tools.isValidEmail("email@fake.com"));
        verify(userRepositoryMock).existsByEmail("email@fake.com");
        assertThat(leyend, is(StringUtils.EMPTY));
    }

    @Test
    void should_validate_a_new_user_and_his_cpf_is_not_valid() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email("@fake.com")
                .build();

        toolsMock.when(() -> Tools.isValidCpf("1234567890")).thenReturn(false);

        String leyend = userService.validateUser(userModelToSave, true);

        toolsMock.verify(() -> Tools.isValidCpf(anyString()));
        assertThat(leyend, is("Conflict: invalid CPF!"));
    }

    @Test
    void should_validate_a_new_user_and_his_cpf_is_already_exists() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email("@fake.com")
                .build();

        toolsMock.when(() -> Tools.isValidCpf("1234567890")).thenReturn(true);
        when(userRepositoryMock.existsByCpf("1234567890")).thenReturn(true);

        String leyend = userService.validateUser(userModelToSave, true);

        toolsMock.verify(() -> Tools.isValidCpf(anyString()));
        verify(userRepositoryMock).existsByCpf("1234567890");

        assertThat(leyend, is("Conflict: CPF exist already!"));
    }

    @Test
    void should_validate_a_new_user_and_his_age_is_not_admited() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email("@fake.com")
                .build();

        toolsMock.when(() -> Tools.isValidCpf("1234567890")).thenReturn(true);
        when(userRepositoryMock.existsByCpf("1234567890")).thenReturn(false);

        String leyend = userService.validateUser(userModelToSave, true);

        toolsMock.verify(() -> Tools.isValidCpf(anyString()));
        verify(userRepositoryMock).existsByCpf("1234567890");

        assertThat(leyend, is("Conflict: Only users over 18 years of age must be registered!"));
    }

    @Test
    void should_validate_a_new_user_and_his_email_is_not_valid() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .dateOfBirth(Date.valueOf(LocalDate.of(1989, 1, 1)))
                .email("@fake.com")
                .build();

        toolsMock.when(() -> Tools.isValidCpf("1234567890")).thenReturn(true);
        when(userRepositoryMock.existsByCpf("1234567890")).thenReturn(false);
        toolsMock.when(() -> Tools.isValidEmail("@fake.com")).thenReturn(false);

        String leyend = userService.validateUser(userModelToSave, true);

        toolsMock.verify(() -> Tools.isValidCpf(anyString()));
        verify(userRepositoryMock).existsByCpf("1234567890");
        toolsMock.verify(() -> Tools.isValidEmail(anyString()));

        assertThat(leyend, is("Conflict: This email is invalid!"));
    }

    @Test
    void should_validate_a_new_user_and_his_email_already_exists() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .dateOfBirth(Date.valueOf(LocalDate.of(1989, 1, 1)))
                .email("email@fake.com")
                .build();

        toolsMock.when(() -> Tools.isValidCpf("1234567890")).thenReturn(true);
        when(userRepositoryMock.existsByCpf("1234567890")).thenReturn(false);
        toolsMock.when(() -> Tools.isValidEmail("email@fake.com")).thenReturn(true);
        when(userRepositoryMock.existsByEmail("email@fake.com")).thenReturn(true);

        String leyend = userService.validateUser(userModelToSave, true);

        toolsMock.verify(() -> Tools.isValidCpf("1234567890"));
        verify(userRepositoryMock).existsByCpf("1234567890");
        toolsMock.verify(() -> Tools.isValidEmail("email@fake.com"));
        verify(userRepositoryMock).existsByEmail("email@fake.com");
        assertThat(leyend, is("Conflict: This email has been assigned another user!"));
    }

    @Test
    void should_validate_a_registed_user_and_his_record_is_valid() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .dateOfBirth(Date.valueOf(LocalDate.of(1989, 1, 1)))
                .email("email@fake.com")
                .build();

        toolsMock.when(() -> Tools.isValidEmail("email@fake.com")).thenReturn(true);

        when(userRepositoryMock.findUserByEmail("email@fake.com")).
                thenReturn(new ArrayList<>());

        String leyend = userService.validateUser(userModelToSave, false);

        toolsMock.verify(() -> Tools.isValidEmail("email@fake.com"));
        verify(userRepositoryMock).findUserByEmail("email@fake.com");
        assertThat(leyend, is(StringUtils.EMPTY));
    }

    @Test
    void should_validate_a_registed_user_and_his_age_is_not_admited() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .dateOfBirth(Date.valueOf(LocalDate.of(2020, 1, 1)))
                .email("email@fake.com")
                .build();

        String leyend = userService.validateUser(userModelToSave, false);

        assertThat(leyend, is("Conflict: Only users over 18 years of age must be registered!"));
    }

    @Test
    void should_validate_a_registed_user_and_his_email_is_invalid() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .dateOfBirth(Date.valueOf(LocalDate.of(1989, 1, 1)))
                .email("@fake.com")
                .build();

        toolsMock.when(() -> Tools.isValidEmail("email@fake.com")).thenReturn(false);

        String leyend = userService.validateUser(userModelToSave, false);

        toolsMock.verify(() -> Tools.isValidEmail("@fake.com"));

        assertThat(leyend, is("Conflict: This email is invalid!"));
    }

    @Test
    void should_validate_a_registed_user_and_his_email_already_exists_in_other_user() {
        UserModel userModelToSave = UserModel.builder()
                .cpf("1234567890")
                .dateOfBirth(Date.valueOf(LocalDate.of(1989, 1, 1)))
                .email("email@fake.com")
                .build();

        UserModel userWithExistingEmail = UserModel.builder().build();
        List<UserModel> userWithExistingEmailList = Arrays.asList(userWithExistingEmail);

        toolsMock.when(() -> Tools.isValidEmail("email@fake.com")).thenReturn(true);
        when(userRepositoryMock.findUserByEmail("email@fake.com"))
                .thenReturn(userWithExistingEmailList);

        String leyend = userService.validateUser(userModelToSave, false);

        toolsMock.verify(() -> Tools.isValidEmail("email@fake.com"));
        verify(userRepositoryMock).findUserByEmail("email@fake.com");
        assertThat(leyend, is("Conflict: This email has been assigned another user!"));
    }
}