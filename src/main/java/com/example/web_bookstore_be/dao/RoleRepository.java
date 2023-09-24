package com.example.web_bookstore_be.dao;

import com.example.web_bookstore_be.entity.Review;
import com.example.web_bookstore_be.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(path = "roles")
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
