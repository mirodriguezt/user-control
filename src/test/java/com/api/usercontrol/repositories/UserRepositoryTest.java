package com.api.usercontrol.repositories;

import com.api.usercontrol.models.UserModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql("classpath:test-data.sql")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void should_return_an_user_record_when_is_searcher_by_cpf() {
        boolean exists = userRepository.existsByCpf("11111111101");
        assertTrue(exists);
    }

    @Test
    public void should_return_an_user_record_when_is_searcher_by_email() {
        boolean exists = userRepository.existsByEmail("one_email@fake.com");
        assertTrue(exists);
    }

    @Test
    public void findUserByEmailAndNotCpf() {
        List<UserModel> userModelList = userRepository.findUserByEmail("one_email@fake.com");

        assertThat(userModelList.size(), is(1));
    }

    @Test
    public void should_return_a_list_of_users_where_the_first_name_matches_the_given_search_text() {
        List<UserModel> userModelList = userRepository.findByFirstNameContains("Name");

        assertThat(userModelList.size(), is(2));
    }

    @Test
    public void should_return_a_list_of_users_where_the_Last_name_matches_the_given_search_text() {
        List<UserModel> userModelList = userRepository.findByLastNameContains("La");

        assertThat(userModelList.size(), is(2));
    }
}