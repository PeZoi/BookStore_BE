package com.example.web_bookstore_be.service.order;

import com.example.web_bookstore_be.dao.*;
import com.example.web_bookstore_be.entity.Book;
import com.example.web_bookstore_be.entity.Order;
import com.example.web_bookstore_be.entity.OrderDetail;
import com.example.web_bookstore_be.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImp implements OrderService{
    private final ObjectMapper objectMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private BookRepository bookRepository;
    public OrderServiceImp(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<?> save(JsonNode jsonData) {
        try{

            Order orderData = objectMapper.treeToValue(jsonData, Order.class);
            orderData.setTotalPrice(orderData.getTotalPriceProduct());
            orderData.setDateCreated(Date.valueOf(LocalDate.now()));
            orderData.setStatus("Đang xử lý");

            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idUser"))));
            Optional<User> user = userRepository.findById(idUser);
            orderData.setUser(user.get());

            Order newOrder = orderRepository.save(orderData);

            JsonNode jsonNode = jsonData.get("book");
            for (JsonNode node : jsonNode) {
                int quantity = Integer.parseInt(formatStringByJson(String.valueOf(node.get("quantity"))));
                Book bookResponse = objectMapper.treeToValue(node.get("book"), Book.class);
                Optional<Book> book = bookRepository.findById(bookResponse.getIdBook());
                book.get().setQuantity(book.get().getQuantity() - quantity);
                book.get().setSoldQuantity(quantity);

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setBook(book.get());
                orderDetail.setQuantity(quantity);
                orderDetail.setOrder(newOrder);
                orderDetail.setPrice(quantity * book.get().getSellPrice());
                orderDetailRepository.save(orderDetail);
                bookRepository.save(book.get());
            }

            cartItemRepository.deleteCartItemsByIdUser(user.get().getIdUser());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> update(JsonNode jsonData) {
        try{
            int idOrder =  Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idOrder"))));
            String status = formatStringByJson(String.valueOf(jsonData.get("status")));
            Optional<Order> order = orderRepository.findById(idOrder);
            order.get().setStatus(status);

            // Lấy ra order detail
            if (status.equals("Bị huỷ")) {
                List<OrderDetail> orderDetailList = orderDetailRepository.findOrderDetailsByOrder(order.get());
                for (OrderDetail orderDetail : orderDetailList) {
                    Book bookOrderDetail = orderDetail.getBook();
                    bookOrderDetail.setSoldQuantity(bookOrderDetail.getSoldQuantity() - orderDetail.getQuantity());
                    bookOrderDetail.setQuantity(bookOrderDetail.getQuantity() + orderDetail.getQuantity());
                    bookRepository.save(bookOrderDetail);
                }
            }

            orderRepository.save(order.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
