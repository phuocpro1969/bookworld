package pq.jdev.b001.bookstore.category.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.category.repository.CategoryRepository;


@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Override
	public Category save(Category category) {
		return categoryRepository.save(category);
	}
	@Override
	public List<Category> findAll() {
		return (List<Category>) categoryRepository.findAll();
	}
	@Override
	public void delete(Long id) {
		categoryRepository.deleteById(id);
	}
	@Override
	public List<Category> findByName(String name) {
		return categoryRepository.findByName(name);
	}
	@Override
	public Category findCategoryByID(Long id) {
		return categoryRepository.findById(id).get();
	}
	@Override
	public List<Category> findCategoryByIdBook(Long idBook) {
		return categoryRepository.findCategoryByIdBook(idBook);
	}



}
