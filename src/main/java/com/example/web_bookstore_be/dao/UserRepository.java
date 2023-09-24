package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Role;
import com.example.web_bookstore_be.entity.User;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(excerptProjection = User.class, path = "users")
public interface UserRepository extends JpaRepository<User, Integer> {
}
