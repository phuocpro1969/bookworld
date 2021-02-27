package pq.jdev.b001.bookstore.listbooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pq.jdev.b001.bookstore.books.model.Book;
import pq.jdev.b001.bookstore.books.repository.BookRepository;


@Service("listBookService")
@Transactional
public class ListBookServiceImpl implements ListBookService {
	@Autowired
	private BookRepository bookRepository;
	
    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

//    @Override
//    public List<Book> search(String q) {
//        return listBookRepository.findByTitleContaining(q);
//    }

    @Override
    public Book findOne(Long id) {
        return bookRepository.findById(id).get();
    }

    @Override
    public void save(Book book) {
    	bookRepository.save(book);
    }

    @Override
    public void delete(Long id) {
    	bookRepository.deleteByIdB(id);
    	//bookRepository.deleteById(id);
    }

	@Override
	public Book findByTitle(String title) {
		return bookRepository.findByTitle(title);
    }
    

	
}
