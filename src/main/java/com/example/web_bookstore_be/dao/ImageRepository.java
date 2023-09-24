package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Image;
import com.example.web_bookstore_be.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "images")
public interface ImageRepository extends JpaRepository<Image, Integer> {
}
