package pq.jdev.b001.bookstore.cart.service;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;



import pq.jdev.b001.bookstore.books.model.Book;
import pq.jdev.b001.bookstore.books.service.BookService;
import pq.jdev.b001.bookstore.cart.model.CartInfo;
import pq.jdev.b001.bookstore.cart.model.CartLineInfo;
import pq.jdev.b001.bookstore.cart.model.CustomerInfo;
import pq.jdev.b001.bookstore.cart.model.Order;
import pq.jdev.b001.bookstore.cart.model.OrderDetail;
import pq.jdev.b001.bookstore.cart.model.OrderDetailInfo;
import pq.jdev.b001.bookstore.cart.model.OrderInfo;
import pq.jdev.b001.bookstore.cart.repository.CartRepository;
import pq.jdev.b001.bookstore.cart.repository.OrderDetailRepository;
import pq.jdev.b001.bookstore.cart.pagination.PaginationResult;

import org.hibernate.Session;
import org.hibernate.SessionFactory;


@Transactional
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private BookService bookService;

   
  

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrder(CartInfo cartInfo) {
       
        System.out.println("getMaxOrderNum" + getMaxOrderNum());
        int orderNum = this.getMaxOrderNum() + 1;
        Order order = new Order();

        order.setId(UUID.randomUUID().toString());
        order.setOrderNum(orderNum);
        order.setOrderDate(new Date());
        order.setAmount(cartInfo.getAmountTotal());
 
        CustomerInfo customerInfo = cartInfo.getCustomerInfo();
        order.setCustomerName(customerInfo.getName());
        order.setCustomerEmail(customerInfo.getEmail());
        order.setCustomerPhone(customerInfo.getPhone());
        order.setCustomerAddress(customerInfo.getAddress());

        cartRepository.save(order);

        List<CartLineInfo> lines = cartInfo.getCartLines();

        for (CartLineInfo line : lines) {
            OrderDetail detail = new OrderDetail();
            detail.setId(UUID.randomUUID().toString());
            detail.setOrder(order);
            detail.setAmount(line.getAmount());
            detail.setPrice(line.getBookInfo().getPrice());
            detail.setQuanity(line.getQuantity());
 
            Long bookId = line.getBookInfo().getBookId();
            Book book = this.bookService.findBookByID(bookId);
            detail.setBook(book);
            orderDetailRepository.save(detail);
            
        }
        cartInfo.setOrderNum(orderNum);
    }



    @Override
    public Order findOrder(String orderId) {
        Order order = cartRepository.getOne(orderId);
		return order;
    }
    
    @Override
    public OrderInfo getOrderInfo(String orderId) {
        Order order = this.findOrder(orderId);
        if (order == null) {
            return null;
        }
        return new OrderInfo(order.getId(), order.getOrderDate(), //
                order.getOrderNum(), order.getAmount(), order.getCustomerName(), //
                order.getCustomerAddress(), order.getCustomerEmail(), order.getCustomerPhone());
    }

    @Override
    public List<OrderDetailInfo> listOrderDetailInfos(String orderId) {
        return cartRepository.listOrderDetailInfos(orderId);
    }

    @Override
    public List<Order> findAll() {
        return cartRepository.findAll();
    }

    @Override
    public int getMaxOrderNum() {
        Integer value = (Integer)cartRepository.getMaxOrderNum();
        if(value == null){
            return 0;
        }
        return value;
    }

    @Override
    public OrderDetail findOrderDedailById(String orderId) {
        return orderDetailRepository.getOne(orderId);
    }

    @Override
    public void deleteOrderDetail(String orderId) {
        orderDetailRepository.deleteById(orderId);

    }

    @Override
    public void deleteOrder(String orderId) {
        cartRepository.deleteById(orderId);
    }
    
}
