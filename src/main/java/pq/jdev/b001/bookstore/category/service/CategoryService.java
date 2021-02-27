package pq.jdev.b001.bookstore.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pq.jdev.b001.bookstore.category.model.Category;


@Service
public interface CategoryService {
	Category save(Category category);
	List<Category> findAll();
	List<Category> findByName(String name);
	Category findCategoryByID(Long id);
	void delete(Long id);
	List<Category> findCategoryByIdBook(Long idBook);

}
