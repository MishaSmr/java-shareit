package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryUserRepository userRepository;

    @Override
    public User update(Long id, User user) {
        User oldUser = userRepository.get(id);
        if (user.getName() != null) oldUser.setName(user.getName());
        if (user.getEmail() != null) {
            userRepository.isEmailExists(user);
            oldUser.setEmail(user.getEmail());
        }
        return userRepository.update(oldUser);
    }
}
