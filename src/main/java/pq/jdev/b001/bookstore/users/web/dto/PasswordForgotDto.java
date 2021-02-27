package pq.jdev.b001.bookstore.users.web.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@SuppressWarnings("deprecation")
public class PasswordForgotDto {
	@Email
    @NotEmpty
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
