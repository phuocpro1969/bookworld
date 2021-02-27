package pq.jdev.b001.bookstore.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pq.jdev.b001.bookstore.users.model.Person;


@Repository
public interface UserRepository extends JpaRepository<Person, Long>, CrudRepository<Person, Long> {

    @Modifying
    @Query("update Person u set u.password = :password where u.id = :id")
    void updatePassword(@Param("password") String password, @Param("id") Long id);

    @Modifying
    @Query("Delete FROM Person where id = :id")
    void deleteByIdP(@Param("id") Long id);
    
	@Modifying
    @Query("Delete from PasswordResetToken where personid = :id")
    void deleteByIdPRT(@Param("id") Long id);
    
    Person findByEmail(String email);
    Person findByUsername(String username);
    Person findById(long id);
    
    @Modifying
	@Query("Update Book b set b.person.id = :idTo where b.person.id = :idFrom")
	void changeAuthorize(@Param("idTo")Long idTo, @Param("idFrom") Long idFrom);
}
