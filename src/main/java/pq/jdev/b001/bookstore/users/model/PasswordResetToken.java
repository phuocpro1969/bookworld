package pq.jdev.b001.bookstore.users.model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;


@Entity
@Table(name="password_reset_token")
public class PasswordResetToken {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = Person.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "PERSONID")
    private Person person;

    @Column(nullable = false)
    private Date expiryDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setExpiryDate(){
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 180);
        this.expiryDate = now.getTime();
    }

    public boolean isExpired() {
        return new Date().after(this.expiryDate);
    }
}
