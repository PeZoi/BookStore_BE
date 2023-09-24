package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Payment;
import com.example.web_bookstore_be.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "reviews")
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
