package com.example.demo.repository;

import com.example.demo.entity.Cart;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    Optional<Cart> findByNguoiDung(User user);
    
    Optional<Cart> findByNguoiDungId(Long userId);
    
    boolean existsByNguoiDung(User user);
}
