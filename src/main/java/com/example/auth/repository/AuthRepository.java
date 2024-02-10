//package com.example.auth.repository;
//
//import com.example.auth.domain.User;
//import com.example.auth.domain.dto.IdAndPassword;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface AuthRepository extends CrudRepository<User, Long> {
//
//    Optional<User> findByUid(String uid);
//
//    Optional<User> findByUidAndPassword(IdAndPassword idAndPassword);
//}