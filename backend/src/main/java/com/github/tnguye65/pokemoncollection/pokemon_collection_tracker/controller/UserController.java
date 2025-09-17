package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.user.UserProfileResponse;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.user.UpdateProfileRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.user.ChangePasswordRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.UserDetailsImpl;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Get current user's profile
	 */
	@GetMapping("/profile")
	public ResponseEntity<UserProfileResponse> getProfile() {
		User authUser = getAuthenticatedUser();
		if (authUser == null) {
			log.warn("Unauthorized attempt to get profile");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		log.info("GET /api/users/profile for user {}", authUser.getEmail());

		try {
			UserProfileResponse response = new UserProfileResponse(
					authUser.getUserId(),
					authUser.getUsername(),
					authUser.getEmail(),
					authUser.getCreatedAt(),
					authUser.getUpdatedAt());

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			log.error("Error fetching user profile", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * Update current user's profile
	 */
	@PutMapping("/profile")
	public ResponseEntity<UserProfileResponse> updateProfile(
			@Valid @RequestBody UpdateProfileRequest request) {

		User authUser = getAuthenticatedUser();
		if (authUser == null) {
			log.warn("Unauthorized attempt to update profile");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		log.info("PUT /api/users/profile for user {}", authUser.getEmail());

		try {
			User updatedUser = userService.updateUserProfile(
					authUser.getUserId(),
					request.getUsername(),
					request.getEmail());

			UserProfileResponse response = new UserProfileResponse(
					updatedUser.getUserId(),
					updatedUser.getUsername(),
					updatedUser.getEmail(),
					updatedUser.getCreatedAt(),
					updatedUser.getUpdatedAt());

			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			log.error("Bad request when updating profile: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("Error updating user profile", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * Change current user's password
	 */
	@PutMapping("/password")
	public ResponseEntity<Void> changePassword(
			@Valid @RequestBody ChangePasswordRequest request) {

		User authUser = getAuthenticatedUser();
		if (authUser == null) {
			log.warn("Unauthorized attempt to change password");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		log.info("PUT /api/users/password for user {}", authUser.getEmail());

		try {
			userService.changePassword(
					authUser.getUserId(),
					request.getCurrentPassword(),
					request.getNewPassword());

			return ResponseEntity.noContent().build();

		} catch (IllegalArgumentException e) {
			log.error("Bad request when changing password: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("Error changing password", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * Delete current user's account
	 */
	@DeleteMapping("/profile")
	public ResponseEntity<Void> deleteAccount() {
		User authUser = getAuthenticatedUser();
		if (authUser == null) {
			log.warn("Unauthorized attempt to delete account");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		log.info("DELETE /api/users/profile for user {}", authUser.getEmail());

		try {
			userService.deleteUserAccount(authUser.getUserId());
			return ResponseEntity.noContent().build();

		} catch (IllegalArgumentException e) {
			log.error("Bad request when deleting account: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			log.error("Error deleting user account", e);
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * Helper method to get the authenticated user from JWT token
	 * 
	 * @return User object if authenticated, null if not
	 */
	private User getAuthenticatedUser() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication != null && authentication.isAuthenticated() &&
					authentication.getPrincipal() instanceof UserDetailsImpl) {

				UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
				return userDetails.getUser();
			}

			return null;
		} catch (Exception e) {
			log.error("Error getting authenticated user", e);
			return null;
		}
	}
}