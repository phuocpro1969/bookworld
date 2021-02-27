package pq.jdev.b001.bookstore.users.web.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import pq.jdev.b001.bookstore.users.constraint.FieldMatch;
import pq.jdev.b001.bookstore.users.model.Role;

@FieldMatch.List({
    @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match"),
})
public class UserUpdateInfoDto {
	
	private long id;
	
	@NotEmpty
	private String firstName;

	@NotEmpty
	private String lastName;
	
	@NotEmpty
	private String userName;
	
	@Email
	@NotEmpty
	private String email;
	
	@NotEmpty
	private String password;

	@NotEmpty
	private String confirmPassword;

	private String phone;
	
	private String address;
	
	private Date birthday;
	
	private int power;
	
	private Timestamp update_date;
	
	private Set<Role> roles;
	
	private String sex;

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public Timestamp getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date() {
		java.util.Date date= new java.util.Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		this.update_date = ts;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
}
