package com.api.usercontrol.repositories;

import com.api.usercontrol.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserModel, String> {

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    List<UserModel> findUserByEmail(String email);

    List<UserModel> findByFirstNameContains(String firstName);

    List<UserModel> findByLastNameContains(String lastName);
}
