package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByUsername();
	
	Optional<User> findByEmail();
	
	boolean existsByUsername(String username);
	
    boolean existsByEmail(String email);
}
