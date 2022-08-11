package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final UserServiceImpl userService;

    UserDto userDto = new UserDto(11L, "name", "1@1.com");
    UserDto userDto2 = new UserDto(11L, "name2", "2@2.com");

    @Test
    void testCreateUser() {
        Long userId = userService.create(userDto).getId();
        UserDto testUserDto = userService.getUser(userId);
        assertThat(testUserDto.getId()).isEqualTo(userId);
        assertThat(testUserDto.getName()).isEqualTo(userDto.getName());
    }

    @Test
    void testUpdateUser() {
        Long userId = userService.create(userDto).getId();
        userDto.setName("Новое имя");
        userService.update(userId, userDto);
        UserDto testUserDto = userService.getUser(userId);
        assertThat(testUserDto.getId()).isEqualTo(userId);
        assertThat(testUserDto.getName()).isEqualTo("Новое имя");
    }

    @Test
    void testGetAllUsers() {
        userService.create(userDto);
        userService.create(userDto2);
        Collection<UserDto> testUsersDto = userService.getAll();
        assertThat(testUsersDto).hasSize(2);
    }

    @Test
    void testGetUserById() {
        Long userId = userService.create(userDto).getId();
        userService.create(userDto2);
        UserDto testUserDto = userService.getUser(userId);
        assertThat(testUserDto.getId()).isEqualTo(userId);
        assertThat(testUserDto.getName()).isEqualTo(userDto.getName());
    }

    @Test
    void testDeleteUser() {
        Long userId = userService.create(userDto).getId();
        Collection<UserDto> testUsersDto = userService.getAll();
        assertThat(testUsersDto).hasSize(1);
        userService.delete(userId);
        testUsersDto = userService.getAll();
        assertThat(testUsersDto).hasSize(0);
    }
}