package pq.jdev.b001.bookstore.cart.model;

import java.io.Serializable;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pq.jdev.b001.bookstore.books.model.Book;

@Entity
@Table(name = "order_details")
public class OrderDetail implements Serializable{
 
    
    @Id
    @Column(name = "ID", length = 50, nullable = false)
    private String id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_ID", nullable = false)
    private Book book;
 
    @Column(name = "Quanity", nullable = false)
    private int quanity;
 
    @Column(name = "Price", nullable = false)
    private double price;
 
    @Column(name = "Amount", nullable = false)
    private double amount;
 
    public String getId() {
        return id;
    }
 
    public void setId(String id) {
        this.id = id;
    }
 
    public Order getOrder() {
        return order;
    }
 
    public void setOrder(Order order) {
        this.order = order;
    }
 
    public Book getBook() {
        return book;
    }
 
    public void setBook(Book book) {
        this.book = book;
    }
 
    public int getQuanity() {
        return quanity;
    }
 
    public void setQuanity(int quanity) {
        this.quanity = quanity;
    }
 
    public double getPrice() {
        return price;
    }
 
    public void setPrice(double price) {
        this.price = price;
    }
 
    public double getAmount() {
        return amount;
    }
 
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
