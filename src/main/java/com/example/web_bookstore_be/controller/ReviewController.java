package com.example.web_bookstore_be.controller;

import com.example.web_bookstore_be.dao.*;
import com.example.web_bookstore_be.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequestMapping("/review")
@RestController
public class ReviewController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @PostMapping("/")
    public ResponseEntity<?> save(@RequestBody JsonNode jsonNode) {
        try{
            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idUser"))));
            int idOrder = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idOrder"))));
            int idBook = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idBook"))));
            float ratingValue = Float.parseFloat(formatStringByJson(String.valueOf(jsonNode.get("ratingValue"))));
            String content = formatStringByJson(String.valueOf(jsonNode.get("content")));

            User user = userRepository.findById(idUser).get();
            Order order = orderRepository.findById(idOrder).get();
            List<OrderDetail> orderDetailList = orderDetailRepository.findOrderDetailsByOrder(order);
            Book book = bookRepository.findById(idBook).get();

            for (OrderDetail orderDetail : orderDetailList) {
                if (orderDetail.getBook().getIdBook() == idBook) {
                    orderDetail.setReview(true);
                    Review review = new Review();
                    review.setBook(book);
                    review.setUser(user);
                    review.setContent(content);
                    review.setRatingPoint(ratingValue);
                    // Lấy thời gian hiện tại
                    Instant instant = Instant.now();
                    // Chuyển đổi thành timestamp
                    Timestamp timestamp = Timestamp.from(instant);
                    review.setTimestamp(timestamp);
                    orderDetailRepository.save(orderDetail);
                    reviewRepository.save(review);
                    break;
                }
            }

            // Set lại rating trung bình của quyển sách đó
            List<Review> reviewList = reviewRepository.findAll();
            int sum = 0; // Tổng rating
            int n = 0; // Số lượng rating
            for (Review review : reviewList) {
                if (review.getBook().getIdBook() == idBook) {
                    n++;
                    sum += review.getRatingPoint();
                }
            }
            float ratingAvg = (float) sum / n;
            book.setAvgRating(ratingAvg);
            bookRepository.save(book);

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
