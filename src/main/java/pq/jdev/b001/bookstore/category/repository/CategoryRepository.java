package pq.jdev.b001.bookstore.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pq.jdev.b001.bookstore.category.model.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CrudRepository<Category, Long>{
	List<Category> findByName(String name);
	Category findById(long id);
	
	@Query("SELECT c FROM Book b INNER JOIN b.categories c WHERE b.id = :idBook") 
	List<Category> findCategoryByIdBook(@Param("idBook") Long idBook);


}
