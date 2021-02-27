package pq.jdev.b001.bookstore.books.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import pq.jdev.b001.bookstore.category.model.Category;
import pq.jdev.b001.bookstore.publishers.model.Publishers;
import pq.jdev.b001.bookstore.users.model.Person;

@Entity
@Table(name = "books")
public class Book implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@NotEmpty
	@Column(name = "TITLE", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin")
	private String title;
	
	@NotNull
	@Column(name = "PRICE")
	private double price;

	@Column(name = "OK")
	private int ok;

	@Column(name = "DOMAIN", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin")
	private String domain;

	@Lob
	@Column(name = "PICTURE", length = Integer.MAX_VALUE, nullable = true)
	private byte[] picture;

	@Column(name = "UPLOADED_DATE")
	private Date uploadedDate;

	@Column(name = "AUTHORS", columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin")
	private String authors;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "PERSON_ID")
	private Person person;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "PUBLISHER_ID ")
	private Publishers publisher;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "BOOK_CATEGORY", joinColumns = { @JoinColumn(name = "BOOK_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "CATEGORY_ID") })
	private Set<Category> categories;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "UPLOAD_ID")
	private Upload uploads;
	
	@Column(name = "PUBLISHED_YEAR")
	private Integer publishedYear;

	@Column(name = "DESCRIPTION")
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public byte[] getPicture() {
        return picture;
    }
 
    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

	public Date getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Publishers getPublisher() {
		return publisher;
	}

	public void setPublisher(Publishers publisher) {
		this.publisher = publisher;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public Upload getUploads() {
		return uploads;
	}

	public void setUploads(Upload uploads) {
		this.uploads = uploads;
	}

	public Integer getPublishedYear() {
		return publishedYear;
	}

	public void setPublishedYear(Integer publishedYear) {
		this.publishedYear = publishedYear;
	}

	public int getOk() {
		return ok;
	}

	public void setOk(int ok) {
		this.ok = ok;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getCategoriesToString() {
		String text = "";
		for (Category cate : categories) {
			text += " " + cate.getId();
		}
		return text;
	}
}
