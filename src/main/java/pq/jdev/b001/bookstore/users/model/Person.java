package pq.jdev.b001.bookstore.users.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import pq.jdev.b001.bookstore.books.model.Book;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"), name="person")
public class Person implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	@Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String firstname;
	@Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String lastname;
    private String phone;
    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin")
    private String Address;
    private String email;
    private String sex;
    private int power;
    private String username;
    private String password;
    private Date birthday;
    
    @OneToMany(mappedBy = "person")
    private Set<Book> books;
    

    @CreationTimestamp
    private Timestamp create_date;
    
    @UpdateTimestamp
    private Timestamp update_date;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "role_person",
			joinColumns = @JoinColumn(name = "personid"),
			inverseJoinColumns = @JoinColumn(name = "roleid")
	)
	private Set<Role> roles;

	public Person(long id){
        this.id = id;
    }
	
	public Person() {
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public Timestamp getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(Timestamp update_date) {
		this.update_date = update_date;
	}

}