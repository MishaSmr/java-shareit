package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestExtDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {


    private final UserRepository userRepository;
    private final ItemRequestServiceImpl itemRequestService;

    User user = new User(1L, "name", "1@1.com");
    ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Описание запроса",
            null,
            LocalDateTime.now()
    );
    ItemRequestDto itemRequestDto2 = new ItemRequestDto(
            2L,
            "Описание второго запроса",
            null,
            LocalDateTime.now()
    );

    @Test
    void testCreateItemRequest() {
        Long userId = userRepository.save(user).getId();
        Long itemRequestId = itemRequestService.create(userId, itemRequestDto).getId();
        ItemRequestExtDto testItemRequestDto = itemRequestService.getById(userId, itemRequestId);
        assertThat(testItemRequestDto.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(testItemRequestDto.getRequesterId()).isEqualTo(userId);
    }

    @Test
    void testGetItemRequestsForUser() {
        Long userId = userRepository.save(user).getId();
        itemRequestService.create(userId, itemRequestDto);
        itemRequestService.create(userId, itemRequestDto2);
        Collection<ItemRequestExtDto> testItemRequestsDto = itemRequestService.getForUser(userId);
        assertThat(testItemRequestsDto).hasSize(2);
    }

    @Test
    void testGetAllItemRequests() {
        Long userId = userRepository.save(user).getId();
        itemRequestService.create(userId, itemRequestDto);
        itemRequestService.create(userId, itemRequestDto2);
        Collection<ItemRequestExtDto> testItemRequestsDto = itemRequestService.getAll(1010L, 0, 10);
        assertThat(testItemRequestsDto).hasSize(2);
    }

    @Test
    void testGetItemRequestById() {
        Long userId = userRepository.save(user).getId();
        Long itemRequestId = itemRequestService.create(userId, itemRequestDto).getId();
        itemRequestService.create(userId, itemRequestDto2);
        ItemRequestExtDto testItemRequestDto = itemRequestService.getById(userId, itemRequestId);
        assertThat(testItemRequestDto.getDescription()).isEqualTo(itemRequestDto.getDescription());
        assertThat(testItemRequestDto.getRequesterId()).isEqualTo(userId);
    }
}