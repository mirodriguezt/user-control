package com.api.usercontrol.models;

import com.api.usercontrol.configs.Config;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_USER")
public class UserModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
    @Column(nullable = false, length = 100)
    private String firstName;
    @Column(nullable = false, length = 100)
    private String lastName;
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = Config.DATE_FORMAT,
            locale = Config.LOCALIZATION,
            timezone = Config.TIME_ZONE)
    private Date dateOfBirth;
    @Column(nullable = true, length = 50)
    private String email;
    @Column(nullable = false)
    private LocalDateTime registrationDate;

    public UserModel() {
    }

    private UserModel(UserModel.Builder builder) {
        setCpf(builder.cpf);
        setFirstName(builder.firstName);
        setLastName(builder.lastName);
        setDateOfBirth(builder.dateOfBirth);
        setEmail(builder.email);
        setRegistrationDate(builder.registrationDate);
    }

    public static UserModel.Builder builder() {
        return new UserModel.Builder();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "CPF: " + this.cpf + " Name: " + this.getLastName() + ", " + this.getFirstName();
    }

    public static final class Builder {
        private String cpf;
        private String firstName;
        private String lastName;
        private Date dateOfBirth;
        private String email;
        private LocalDateTime registrationDate;

        private Builder() {
        }

        public UserModel.Builder cpf(String val) {
            cpf = val;
            return this;
        }

        public UserModel.Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public UserModel.Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public UserModel.Builder dateOfBirth(Date val) {
            dateOfBirth = val;
            return this;
        }

        public UserModel.Builder email(String val) {
            email = val;
            return this;
        }

        public UserModel.Builder registrationDate(LocalDateTime val) {
            registrationDate = val;
            return this;
        }

        public UserModel build() {
            return new UserModel(this);
        }
    }
}
