package pq.jdev.b001.bookstore.books.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "upload")
public class Upload implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2713424559932800332L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@Column(name = "ORIGINAL_FILE_NAME")
	private String originalFileName;

	@Column(name = "MODIFIED_FILE_NAME")
	private String modifiedFileName;

	@Column(name = "MODIFIED_FILE_PATH")
	private String modifiedFilePath;

	@Column(name = "UPLOADED_DATE")
	private Date uploadedDate;

	@OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
	private Set<Book> books;
	
	@Column(name = "BOOK_ID")
	private long bookId;

	public long getBookId() {
		return bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getModifiedFileName() {
		return modifiedFileName;
	}

	public void setModifiedFileName(String modifiedFileName) {
		this.modifiedFileName = modifiedFileName;
	}

	public String getModifiedFilePath() {
		return modifiedFilePath;
	}

	public void setModifiedFilePath(String modifiedFilePath) {
		this.modifiedFilePath = modifiedFilePath;
	}

	public Date getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public Set<Book> getBooks() {
		return books;
	}

	public void setBooks(Set<Book> books) {
		this.books = books;
	}

}
