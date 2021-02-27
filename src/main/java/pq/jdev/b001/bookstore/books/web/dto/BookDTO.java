package pq.jdev.b001.bookstore.books.web.dto;

import pq.jdev.b001.bookstore.books.model.Book;

public class BookDTO {

	private Book currentBook;

	private String stringCategories;

	public Book getCurrentBook() {
		return currentBook;
	}

	public void setCurrentBook(Book currentBook) {
		this.currentBook = currentBook;
	}

	public String getStringCategories() {
		return stringCategories;
	}

	public void setStringCategories(String stringCategories) {
		this.stringCategories = stringCategories;
	}

}
