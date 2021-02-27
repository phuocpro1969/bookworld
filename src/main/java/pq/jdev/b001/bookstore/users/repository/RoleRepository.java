package pq.jdev.b001.bookstore.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pq.jdev.b001.bookstore.users.model.Role;

@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Long>, CrudRepository<Role, Long>{
	Role findByName(String name);

	Role findById(long power);

}
