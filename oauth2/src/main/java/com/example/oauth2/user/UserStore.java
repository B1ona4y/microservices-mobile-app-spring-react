package com.example.oauth2.user;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class UserStore {

	private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();

	public Optional<User> findByEmail(String email) {
		return Optional.ofNullable(usersByEmail.get(email));
	}

	public User save(User user) {
		usersByEmail.put(user.email(), user);
		return user;
	}

}
