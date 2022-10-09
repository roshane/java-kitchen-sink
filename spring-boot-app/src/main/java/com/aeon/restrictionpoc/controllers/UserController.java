package com.aeon.restrictionpoc.controllers;

import com.aeon.restrictionpoc.domain.User;
import com.aeon.restrictionpoc.provider.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final UserProvider userProvider;

    private final Comparator<User> userSortCriteria = Comparator.comparing(User::getId);

    protected UserController(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> all(@RequestParam(value = "ids", required = false, defaultValue = "") List<String> ids) {
        if (ids.isEmpty()) {
            final List<User> result = userProvider.getAll();
            result.sort(userSortCriteria);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        final List<User> result = userProvider.getAll(ids);
        result.sort(userSortCriteria);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        return userProvider.get(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/users")
    public ResponseEntity<?> save(@RequestBody User user) {
        try {
            log.info("try saving the user: {}", user);
            userProvider.put(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception exception) {
            log.error("Error saving new user", exception);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
