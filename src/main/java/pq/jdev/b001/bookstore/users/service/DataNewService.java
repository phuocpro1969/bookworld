package pq.jdev.b001.bookstore.users.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.category.repository.CategoryRepository;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.publishers.repository.PublisherRepository;
import pq.jdev.b001.bookstore.users.model.Person;
import pq.jdev.b001.bookstore.users.model.Role;
import pq.jdev.b001.bookstore.users.repository.RoleRepository;
import pq.jdev.b001.bookstore.users.repository.UserRepository;

@Component
public class DataNewService implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	PublisherRepository publisherRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		if (categoryRepository.findById((long) 1) == null) {
			categoryRepository.save(new Category("updatingCategory", getTimeNow(), getTimeNow(), (long) 1 , (long) 1));
		}
		
		if (publisherRepository.findById((long) 1) == null) {
			publisherRepository.save(new Publishers("updatingPublisher", getTimeNow(), getTimeNow(), (long) 1 , (long) 1));
		}
		
		if (roleRepository.findByName("ROLE_EMPLOYEE") == null) {
			roleRepository.save(new Role("ROLE_EMPLOYEE"));
		}

		if (roleRepository.findByName("ROLE_ADMIN") == null) {
			roleRepository.save(new Role("ROLE_ADMIN"));
		}

		if (userRepository.findByUsername("admin") == null) {
			Person admin = new Person();
			admin.setFirstname("admin");
			admin.setLastname("admin");
			admin.setEmail("phuocpro1969@gmail.com");
			admin.setAddress("HoChiMinh");
			String startDate = "1999-01-08";
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date date;
			try {
				date = sdf1.parse(startDate);
				java.sql.Date sqlStartDate = new java.sql.Date(date.getTime());
				admin.setBirthday(sqlStartDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			admin.setPhone("0981415287");
			admin.setPower(2);
			admin.setSex("Male");
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin"));

			java.util.Date dateu = new java.util.Date();
			long time = dateu.getTime();
			Timestamp ts = new Timestamp(time);
			admin.setUpdate_date(ts);

			HashSet<Role> roles = new HashSet<>();
			roles.add(roleRepository.findByName("ROLE_ADMIN"));
			admin.setRoles(roles);
			userRepository.save(admin);
		}
	}

	public Timestamp getTimeNow() {
		java.util.Date date = new java.util.Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		return ts;
	}
}
