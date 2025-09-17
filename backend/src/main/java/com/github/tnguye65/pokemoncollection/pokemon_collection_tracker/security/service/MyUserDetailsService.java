package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.UserDetailsImpl;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new UserDetailsImpl(user);
    }

}
