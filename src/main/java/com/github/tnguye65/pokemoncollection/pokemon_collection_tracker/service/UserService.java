package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	//private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public UserService(UserRepository userRepository) {//, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		//this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public User registerUser(String username, String email, String password) {
		// 1. Validate input parameters
	    if (username == null || username.trim().isEmpty()) {
	        throw new IllegalArgumentException("Username cannot be empty");
	    }
	    if (email == null || email.trim().isEmpty()) {
	        throw new IllegalArgumentException("Email cannot be empty");
	    }
	    if (password == null || password.trim().isEmpty()) {
	        throw new IllegalArgumentException("Password cannot be empty");
	    }
	    
	    // 2. Check if username already exists
	    if (userRepository.existsByUsername(username)) {
	        throw new IllegalArgumentException("Username '" + username + "' is already taken");
	    }
	    
	    // 3. Check if email already exists  
	    if (userRepository.existsByEmail(email)) {
	        throw new IllegalArgumentException("Email '" + email + "' is already registered");
	    }
	    
	    // 4. Encode the password (you'll need to inject PasswordEncoder)
	    String encodedPassword = password;//passwordEncoder.encode(password);
	    
	    // 5. Create new user object
	    User newUser = new User(username, email, encodedPassword);
	    
	    // 6. Save to database (timestamps set automatically by @PrePersist)
	    return userRepository.save(newUser);
	}
	
	public Optional<User> findByUsername(String username) {
		return this.userRepository.findByUsername(username);
	}
	
	public Optional<User> findByEmail(String email) {
		return this.userRepository.findByEmail(email);
	}
	
	public boolean checkIfUsernameExists(String username) {
		return this.userRepository.existsByUsername(username);
	}
	
	public boolean checkIfEmailExists(String email) {
		return this.userRepository.existsByEmail(email);
	}
	
}
