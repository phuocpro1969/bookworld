package pq.jdev.b001.bookstore.cart.model;

import pq.jdev.b001.bookstore.books.model.BookInfo;

public class CartLineInfo {
    private BookInfo bookInfo;
    private int quantity;
  
    public CartLineInfo() {
        this.quantity = 0;
    }
  
    public BookInfo getBookInfo() {
        return bookInfo;
    }
  
    public void setBookInfo(BookInfo bookInfo) {
        this.bookInfo = bookInfo;
    }
  
    public int getQuantity() {
        return quantity;
    }
  
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
  
    public double getAmount() {
        return this.bookInfo.getPrice() * this.quantity;
    }
}
