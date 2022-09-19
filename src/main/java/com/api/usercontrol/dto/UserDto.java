package com.api.usercontrol.dto;

import com.api.usercontrol.configs.Config;
import com.api.usercontrol.models.UserModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.lang.Nullable;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.Objects;

public class UserDto {
    @NotBlank
    @Size(max = 11)
    private String cpf;

    @NotBlank
    @Size(max = 100)
    private String firstName;
    @NotBlank
    @Size(max = 100)
    private String lastName;
    @NotNull
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = Config.DATE_FORMAT,
            locale = Config.LOCALIZATION,
            timezone = Config.TIME_ZONE)
    private Date dateOfBirth;
    @Nullable
    @Email(regexp = Config.EMAIL_REGEXP_FORMAT, flags = Pattern.Flag.CASE_INSENSITIVE)
    private JsonNullable<String> email;

    public UserDto() {
    }

    private UserDto(Builder builder) {
        setCpf(builder.cpf);
        setFirstName(builder.firstName);
        setLastName(builder.lastName);
        setDateOfBirth(builder.dateOfBirth);
        setEmail(builder.email);
    }

    public static Builder builder() {
        return new Builder();
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

    public JsonNullable<String> getEmail() {
        return email;
    }

    public void setEmail(JsonNullable<String> email) {
        this.email = email;
    }

    public String getEmailString() {
        return Objects.isNull(email) ? "" : email.get();
    }


    public static final class Builder {
        private String cpf;
        private String firstName;
        private String lastName;
        private Date dateOfBirth;
        private JsonNullable<String> email;

        private Builder() {
        }

        public Builder cpf(String val) {
            cpf = val;
            return this;
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public Builder dateOfBirth(Date val) {
            dateOfBirth = val;
            return this;
        }

        public Builder email(JsonNullable<String> val) {
            email = val;
            return this;
        }

        public UserDto build() {
            return new UserDto(this);
        }

        public UserDto build(UserModel userModel) {
            var user = new UserDto();
            user.cpf = userModel.getCpf();
            user.firstName = userModel.getFirstName();
            user.lastName = userModel.getLastName();
            user.dateOfBirth = userModel.getDateOfBirth();
            user.email = JsonNullable.of(userModel.getEmail());

            return user;
        }
    }
}
