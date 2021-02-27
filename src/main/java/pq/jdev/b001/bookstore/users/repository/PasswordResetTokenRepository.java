package pq.jdev.b001.bookstore.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pq.jdev.b001.bookstore.users.model.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	PasswordResetToken findByToken(String token);
	
}
