package pq.jdev.b001.bookstore.users.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class Role implements Serializable{
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Person> person;
    
    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Person> getPerson() {
		return person;
	}

	public void setPerson(Set<Person> person) {
		this.person = person;
	}
}