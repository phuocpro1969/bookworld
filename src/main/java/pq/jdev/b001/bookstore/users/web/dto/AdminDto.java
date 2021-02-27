package pq.jdev.b001.bookstore.users.web.dto;

import java.sql.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class AdminDto {

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

	private String phone;
	
	private String address;
	
	private Date birthday;
	
	private String sex;
	
	private int power;
	
	private String dropdownSelectedValue;
	
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

	public String getDropdownSelectedValue() {
		return dropdownSelectedValue;
	}

	public void setDropdownSelectedValue(String dropdownSelectedValue) {
		this.dropdownSelectedValue = dropdownSelectedValue;
	}
}
