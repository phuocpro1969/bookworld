package pq.jdev.b001.bookstore.cart.repository;

import org.springframework.stereotype.Repository;

import pq.jdev.b001.bookstore.cart.model.Order;
import pq.jdev.b001.bookstore.cart.model.OrderDetailInfo;
import pq.jdev.b001.bookstore.cart.model.OrderInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@Repository("orderRepository")
public interface CartRepository extends JpaRepository<Order, String>, CrudRepository<Order, String>  {
   
    @Query("SELECT new pq.jdev.b001.bookstore.cart.model.OrderDetailInfo (d.id, d.book.id, d.book.title, d.quanity, d.price, d.amount) FROM OrderDetail d WHERE d.order.id =:orderId")
    List<OrderDetailInfo> listOrderDetailInfos(@Param("orderId") String orderId);
    
    @Query(value = "SELECT max(o.orderNum) FROM Order o")
    public Integer getMaxOrderNum();

    // @Query("SELECT new pq.jdev.b001.bookstore.cart.model.OrderInfo (ord.id, ord.orderDate, ord.orderNum, ord.amout, ord.customerName, ord.customerAddress, ord.customerEmail, ord.customerPhone) FROM Order ord order by ord.orderNum desc")
    // OrderInfo getOrderInfo();
}
