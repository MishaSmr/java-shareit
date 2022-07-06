package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final InMemoryUserRepository userRepository;
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        return userRepository.getAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.get(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {
        return userRepository.create(user);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        userRepository.remove(id);
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody User user) throws ValidationException {
        return userService.update(id, user);
    }
}
