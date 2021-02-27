package pq.jdev.b001.bookstore.books.model;

public class BookInfo {
    private Long bookId;
    private String title;
    private String author;
    private double price;

    public BookInfo() {
    }

    public BookInfo(Book book) {
        this.bookId = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthors();
        this.price = book.getPrice();
    }
 
    // Using in JPA/Hibernate query
    public BookInfo(Long bookId, String title, String author, double price) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
    }
    
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
    }
    public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
    }
    public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
    }
    


 
}
