package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Book;
import com.example.web_bookstore_be.entity.FavoriteBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "favorite-books")
public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Integer> {
}
