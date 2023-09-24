package com.example.web_bookstore_be.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int idUser; // id user
    @Column(name = "first_name")
    private String firstName; // Họ đệm
    @Column(name = "last_name")
    private String lastName; // Tên
    @Column(name = "username")
    private String username; // Tên tài khoản
    @Column(name = "password", length = 512)
    private String password; // Mật khẩu
    @Column(name = "gender")
    private char gender; // Giới tính
    @Column(name = "date_of_birth")
    private Date dateOfBirth; // Năm sinh
    @Column(name = "email")
    private String email; // Email
    @Column(name = "phone_number")
    private String phoneNumber; // Số điện thoại
    @Column(name = "purchase_address")
    private String purchaseAddress; // Địa chỉ mua hàng
    @Column(name = "delivery_address")
    private String deliveryAddress; // Địa chỉ giao hàng

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Review> listReviews; // Danh sách đánh giá của user

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FavoriteBook> listFavoriteBooks; // Danh sách nhưng quyển sách yêu thích của user

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_role"))
    private List<Role> listRoles; // Danh sách role của user

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Order> listOrders; // Danh sách đơn hàng của user
}
