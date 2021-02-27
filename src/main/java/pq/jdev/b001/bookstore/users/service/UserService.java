package pq.jdev.b001.bookstore.users.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import pq.jdev.b001.bookstore.users.model.PasswordResetToken;
import pq.jdev.b001.bookstore.users.model.Person;
import pq.jdev.b001.bookstore.users.model.Role;
import pq.jdev.b001.bookstore.users.web.dto.AdminDto;
import pq.jdev.b001.bookstore.users.web.dto.AdminUpdateInfoUserDto;
import pq.jdev.b001.bookstore.users.web.dto.UserChangePassDto;
import pq.jdev.b001.bookstore.users.web.dto.UserDto;
import pq.jdev.b001.bookstore.users.web.dto.UserUpdateInfoDto;

public interface UserService extends UserDetailsService {

	Person findByUsername(String userName);

	Person findByEmail(String email);
	
	Person findById(Long id);
	
	List<Person> findAll();
	
	List<Role> findAllRole();

	Person save(AdminDto adminDto);

	Person save(UserDto userDto);
	
	Person save(UserUpdateInfoDto userDto);
	
	Person save(AdminUpdateInfoUserDto userDto);
	
	UserUpdateInfoDto updateInfo(Person p);
	
	AdminUpdateInfoUserDto updateUserInfo(Person p);	

	void updatePassword(String password, Long personId);
	
	void delete(Long id);

	PasswordResetToken findByToken(String token);

	void deleteByToken(PasswordResetToken token);

	void saveToken(PasswordResetToken token);
	
	void deleteTokenByIdPerson(long id);

	UserChangePassDto updateInfoP(Person p);
	
	Role findByIdRole(long id);
	
	void autoLogin(String username);

	void changeAuthorize(Long idTo, long idFrom);
}