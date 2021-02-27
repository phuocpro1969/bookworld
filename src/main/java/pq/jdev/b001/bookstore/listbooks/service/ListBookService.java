package pq.jdev.b001.bookstore.listbooks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pq.jdev.b001.bookstore.books.model.Book;


@Service
public interface ListBookService {
	List<Book> findAll();

	Book findByTitle(String title);
	
//    List<Book> search(String q);

    Book findOne(Long id);

    void save(Book contact);

    void delete(Long id);
}
