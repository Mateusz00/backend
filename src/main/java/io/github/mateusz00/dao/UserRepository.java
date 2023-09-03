package io.github.mateusz00.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.mateusz00.entity.User;

public interface UserRepository extends MongoRepository<User, Integer>
{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}