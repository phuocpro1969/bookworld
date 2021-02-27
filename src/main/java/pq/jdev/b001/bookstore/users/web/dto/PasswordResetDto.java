package pq.jdev.b001.bookstore.users.web.dto;

import org.hibernate.validator.constraints.NotEmpty;

import pq.jdev.b001.bookstore.users.constraint.FieldMatch;
import pq.jdev.b001.bookstore.users.constraint.ValidPassword;

@SuppressWarnings("deprecation")
@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
public class PasswordResetDto {

    @NotEmpty
    @ValidPassword
    private String password;

    @NotEmpty
    @ValidPassword
    private String confirmPassword;

	@NotEmpty
    private String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
