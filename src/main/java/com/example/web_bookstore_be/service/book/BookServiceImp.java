package com.example.web_bookstore_be.service.book;

import com.example.web_bookstore_be.dao.BookRepository;
import com.example.web_bookstore_be.entity.Book;
import com.example.web_bookstore_be.entity.Image;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImp implements BookService{
    @Autowired
    private BookRepository bookRepository;

    private final ObjectMapper objectMapper;
    public BookServiceImp(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<?> save(JsonNode bookJson) {
        try{
            Book book = objectMapper.treeToValue(bookJson, Book.class);

            // Nếu có giảm giá
            if (book.getDiscountPercent() != 0) {
                int sellPrice = (int) ((int) book.getListPrice() - Math.round(book.getListPrice() / book.getDiscountPercent()));
                book.setSellPrice(sellPrice);
            }

            // Lưu thumbnail cho ảnh
            Image thumbnail = new Image();
            thumbnail.setBook(book);
            thumbnail.setDataImage(formatStringByJson(String.valueOf((bookJson.get("thumbnail")))));
            thumbnail.setThumbnail(true);
            List<Image> imagesList = new ArrayList<Image>();
            imagesList.add(thumbnail);
            book.setListImages(imagesList);

            bookRepository.save(book);
            return ResponseEntity.ok("Success!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
