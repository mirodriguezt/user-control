package com.api.usercontrol.services;

import com.api.usercontrol.models.UserModel;
import com.api.usercontrol.repositories.UserRepository;
import com.api.usercontrol.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final byte ALLOWED_AGE_USERS_REGISTRATION = 19;

    private static final String USER_LEYEND_INVALID_CPF = "Conflict: invalid CPF!";
    private static final String USER_LEYEND_CPF_EXISTS = "Conflict: CPF exist already!";
    private static final String USER_LEYEND_AGE_NOT_ADMITED = "Conflict: Only users over 18 years of age must be registered!";
    private static final String USER_LEYEND_EMAIL_IS_INVALID = "Conflict: This email is invalid!";
    private static final String USER_LEYEND_EMAIL_ALREADY_ASSIGNED = "Conflict: This email has been assigned another user!";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserModel save(UserModel userModel) {
        try {
            return userRepository.save(userModel);
        } finally {
            log.info("User saved -> cpf:{}", userModel.getCpf());
        }
    }

    public boolean existsByCpf(String cpf) {
        return userRepository.existsByCpf(cpf);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<UserModel> findUserByEmailAndCpfDiffersToGiven(String email, String cpf) {
        List<UserModel> userModelList = userRepository.findUserByEmail(email);
        return userModelList.stream()
                .filter(x -> !cpf.equals(x.getCpf()))
                .collect(Collectors.toList());
    }

    public Page<UserModel> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public List<UserModel> findByFirstNameContains(String firstName) {
        return userRepository.findByFirstNameContains(firstName);
    }

    public List<UserModel> findByLastNameContains(String lastName) {
        return userRepository.findByLastNameContains(lastName);
    }

    public Optional<UserModel> findByCpf(String cpf) {
        return userRepository.findById(cpf);
    }

    @Transactional
    public void delete(UserModel userModel) {
        try {
            userRepository.delete(userModel);
        } finally {
            log.info("User deleted -> cpf:{}", userModel.getCpf());
        }
    }

    public String validateUser(UserModel userModel, boolean isNewUser) {
        String cpf = userModel.getCpf();
        if (isNewUser) {
            if (!Tools.isValidCpf(cpf)) {
                return USER_LEYEND_INVALID_CPF;
            }

            if (existsByCpf(cpf)) {
                return USER_LEYEND_CPF_EXISTS;
            }
        }

        if (isUserAgeAllowed(userModel.getDateOfBirth())) {
            return USER_LEYEND_AGE_NOT_ADMITED;
        }

        var email = userModel.getEmail();
        if (Objects.nonNull(email)) {
            if (!Tools.isValidEmail(email)) {
                return USER_LEYEND_EMAIL_IS_INVALID;
            }

            if (isNewUser) {
                if (existsByEmail(userModel.getEmail())) {
                    return USER_LEYEND_EMAIL_ALREADY_ASSIGNED;
                }
            } else {
                List<UserModel> userModelList = findUserByEmailAndCpfDiffersToGiven(email, cpf);
                if (!userModelList.isEmpty()) {
                    return USER_LEYEND_EMAIL_ALREADY_ASSIGNED;
                }
            }
        }

        return "";
    }

    //region private methods
    private boolean isUserAgeAllowed(Date dateOfBirth) {
        LocalDate currentDate = LocalDate.now();
        var period = Period.between(dateOfBirth.toLocalDate(), currentDate);

        if (period.getYears() < ALLOWED_AGE_USERS_REGISTRATION) {
            return true;
        }

        return false;
    }
    //endregion
}
