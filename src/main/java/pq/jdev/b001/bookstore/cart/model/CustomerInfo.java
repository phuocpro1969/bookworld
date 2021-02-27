package pq.jdev.b001.bookstore.cart.model;

import pq.jdev.b001.bookstore.cart.dto.CustomerDTO;

public class CustomerInfo {
 
    private String name;
    private String address;
    private String email;
    private String phone;
 
    private boolean valid;
 
    public CustomerInfo() {
 
    }
 
    public CustomerInfo(CustomerDTO customerDTO) {
        this.name = customerDTO.getName();
        this.address = customerDTO.getAddress();
        this.email = customerDTO.getEmail();
        this.phone = customerDTO.getPhone();
        this.valid = customerDTO.isValid();
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getAddress() {
        return address;
    }
 
    public void setAddress(String address) {
        this.address = address;
    }
 
    public String getPhone() {
        return phone;
    }
 
    public void setPhone(String phone) {
        this.phone = phone;
    }
 
    public boolean isValid() {
        return valid;
    }
 
    public void setValid(boolean valid) {
        this.valid = valid;
    }
 
}
