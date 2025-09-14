package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository.UserRepository;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Get user profile by user ID
	 */
	@Transactional(readOnly = true)
	public Optional<User> getUserProfile(UUID userId) {
		log.debug("Fetching profile for user ID: {}", userId);
		return userRepository.findById(userId);
	}

	/**
	 * Update user profile information
	 */
	@Transactional
	public User updateUserProfile(UUID userId, String username, String email) {
		log.info("Updating profile for user ID: {}", userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

		// Check if new username is taken by another user
		if (username != null && !username.equals(user.getUsername())) {
			if (userRepository.existsByUsername(username)) {
				throw new IllegalArgumentException("Username '" + username + "' is already taken");
			}
			user.setUsername(username);
		}

		// Check if new email is taken by another user
		if (email != null && !email.equals(user.getEmail())) {
			if (userRepository.existsByEmail(email)) {
				throw new IllegalArgumentException("Email '" + email + "' is already registered");
			}
			user.setEmail(email);
		}

		return userRepository.save(user);
	}

	/**
	 * Change user password
	 */
	@Transactional
	public void changePassword(UUID userId, String currentPassword, String newPassword) {
		log.info("Changing password for user ID: {}", userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

		// Verify current password
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new IllegalArgumentException("Current password is incorrect");
		}

		// Validate new password
		if (newPassword == null || newPassword.trim().isEmpty()) {
			throw new IllegalArgumentException("New password cannot be empty");
		}

		if (newPassword.length() < 6) {
			throw new IllegalArgumentException("New password must be at least 6 characters long");
		}

		// Update password
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	/**
	 * Delete user account
	 */
	@Transactional
	public void deleteUserAccount(UUID userId) {
		log.info("Deleting account for user ID: {}", userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

		// You might want to add logic here to handle related data:
		// - Delete user's collection items
		// - Handle any other related entities

		userRepository.delete(user);
	}

	// Utility methods for authentication (these might be used by other services)
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public boolean checkIfUsernameExists(String username) {
		return userRepository.existsByUsername(username);
	}

	public boolean checkIfEmailExists(String email) {
		return userRepository.existsByEmail(email);
	}
}