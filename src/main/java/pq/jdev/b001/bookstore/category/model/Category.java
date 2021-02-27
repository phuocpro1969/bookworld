package pq.jdev.b001.bookstore.category.model;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import pq.jdev.b001.bookstore.books.model.Book;

@Entity
@Table(name="category")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin")
	private String name;
	
	@CreationTimestamp
	@Column(name="CREATEDATE")
	private Timestamp createDate;
	
	@UpdateTimestamp
	@Column(name="UPDATEDATE")
	private Timestamp updateDate;
	
	@Column(name = "CREATEDID")
	private Long createId;
	
	@Column(name = "UPLOADID")
	private Long updateId;

	@ManyToMany(mappedBy = "categories", fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	private Set<Book> books;
	
	
	public Category() {	
	}
	
	public Category(String name, Timestamp createDate, Timestamp updateDate, Long createId, Long updateId) {
		this.name = name;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.createId = createId;
		this.updateId = updateId;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((books == null) ? 0 : books.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (books == null) {
			if (other.books != null)
				return false;
		} else if (!books.equals(other.books))
			return false;
		return true;
	}

	public Category(Long createId, Long updateId) {
		this.createId = createId;
		this.updateId = updateId;
	}



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Set<Book> getBooks() {
		return books;
	}

	public void setBooks(Set<Book> books) {
		this.books = books;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Timestamp getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}

	public Long getCreateId() {
		return createId;
	}

	public void setCreateId(Long createId) {
		this.createId = createId;
	}

	public Long getUpdateId() {
		return updateId;
	}

	public void setUpdateId(Long updateId) {
		this.updateId = updateId;
	}
	
}
