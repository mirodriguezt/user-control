package com.api.usercontrol.controlllers;

import com.api.usercontrol.dto.UserDto;
import com.api.usercontrol.mappers.UserMapper;
import com.api.usercontrol.models.UserModel;
import com.api.usercontrol.services.UserService;
import com.api.usercontrol.utils.Tools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api")
@Api(value = "API REST Users")
public class UserController {
    final UserService userService;
    final UserMapper mapper;
    private final static String LEYEND_USER_NOT_FOUND = "User not found";
    private final static String LEYEND_USER_DELETED = "User has been deleted";

    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @RequestMapping(method = RequestMethod.POST,
            value = "/user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(value = "Add a user record")

    public ResponseEntity<Object> saveUser(@RequestBody @Valid UserDto userDto) {
        UserModel userModel = UserModel.builder()
                .cpf(userDto.getCpf())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .dateOfBirth(userDto.getDateOfBirth())
                .email(userDto.getEmailString())
                .build();

        String validateNewUserLeyend = userService.validateUser(userModel, true);

        if (!validateNewUserLeyend.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(validateNewUserLeyend);
        } else {
            userModel.setRegistrationDate(Tools.getLocalDateTime());
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userModel));
        }
    }

    @GetMapping("/user")
    @ApiOperation(value = "Return a list with all users per page")
    public ResponseEntity<Page<UserModel>> getAllUsers(@PageableDefault(page = 0, size = 10, sort = "cpf",
            direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(pageable));
    }

    @GetMapping("/user/{cpf}")
    @ApiOperation(value = "Return a unique user giving his CPF")
    public ResponseEntity<Object> getUser(@PathVariable(value = "cpf") String cpf) {
        Optional<UserModel> userModelOptional = userService.findByCpf(cpf);
        if (!userModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(LEYEND_USER_NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
    }

    @RequestMapping(value = {"/user/filter"}, method = RequestMethod.GET, params = "firstname")
    @ApiOperation(value = "Returns a list of users where their firstName matches the search text (case sensitive)")
    public ResponseEntity<List<UserModel>> getUserbyFirstName(
            @RequestParam(value = "firstname", required = true) String firstName) {
        List<UserModel> userModelList = userService.findByFirstNameContains(firstName);

        return ResponseEntity.status(HttpStatus.OK).body(userModelList);
    }

    @RequestMapping(value = {"/user/filter"}, method = RequestMethod.GET, params = "lastname")
    @ApiOperation(value = "Returns a list of users where their lastName matches the search text (case sensitive)")
    public ResponseEntity<List<UserModel>> getUserbyLastName(
            @RequestParam(value = "lastname", required = true) String lastName) {
        List<UserModel> userModelList = userService.findByLastNameContains(lastName);

        return ResponseEntity.status(HttpStatus.OK).body(userModelList);
    }

    @DeleteMapping("/user/{cpf}")
    @ApiOperation(value = "Delete a user giving his CPF")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "cpf") String cpf) {
        Optional<UserModel> userModelOptional = userService.findByCpf(cpf);
        if (!userModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(LEYEND_USER_NOT_FOUND);
        }

        userService.delete(userModelOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body(LEYEND_USER_DELETED);
    }

    @PutMapping("/user/{cpf}")
    @ApiOperation(value = "Modify one or several user fields giving their CPF")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "cpf") String cpf,
                                             @RequestBody UserDto userDto) {
        Optional<UserModel> userModelOptional = userService.findByCpf(cpf);
        if (!userModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(LEYEND_USER_NOT_FOUND);
        }

        UserModel userModel = userModelOptional.get();
        mapper.update(userDto, userModel);

        UserDto userDtoToVerify = UserDto.builder().build(userModel);
        ValidatorFactoryImpl validatorFactoryImp = (ValidatorFactoryImpl) Validation.buildDefaultValidatorFactory();
        ValidatorImpl validator = (ValidatorImpl) validatorFactoryImp.getValidator();

        Set<ConstraintViolation<UserDto>> constraintViolations = validator.validate(userDtoToVerify);
        if (!constraintViolations.isEmpty()) {
            List<String> errors = constraintViolations.stream().map(violation -> violation.getPropertyPath() + ": " + violation.getMessage()).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
        }

        String validateModifiedUserLeyend = userService.validateUser(userModel, false);
        if (!validateModifiedUserLeyend.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(validateModifiedUserLeyend);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(userService.save(userModel));
        }
    }
}


