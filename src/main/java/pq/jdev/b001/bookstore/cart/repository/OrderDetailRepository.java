package pq.jdev.b001.bookstore.cart.repository;

import org.springframework.stereotype.Repository;

import pq.jdev.b001.bookstore.cart.model.OrderDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;


@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String>, CrudRepository<OrderDetail, String>  {
   
    // OrderDetail findOrderById(String orderId);
  
}