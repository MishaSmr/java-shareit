package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final ItemServiceImpl itemService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    LocalDateTime currentDateTime = LocalDateTime.now();

    User user = new User(1L, "name", "1@1.com");

    ItemDto itemDto = new ItemDto(
            1,
            "Дрель",
            "Просто дрель",
            true,
            null,
            null);

    @Test
    void testCreateItem() {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemService.create(userId, itemDto).getId();
        ItemExtDto testItemDto = itemService.getItem(itemId, userId);
        assertThat(testItemDto.getName()).isEqualTo(itemDto.getName());
        assertThat(testItemDto.getOwnerId()).isEqualTo(userId);
    }

    @Test
    void testUpdateItem() {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemService.create(userId, itemDto).getId();
        itemDto.setName("Новое имя");
        itemDto.setAvailable(false);
        itemService.update(itemId, userId, itemDto);
        ItemExtDto testItemDto = itemService.getItem(itemId, userId);
        assertThat(testItemDto.getName()).isEqualTo("Новое имя");
        assertThat(testItemDto.getAvailable()).isFalse();
    }

    @Test
    void testGetItemByIdForUser() {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemService.create(userId, itemDto).getId();
        ItemExtDto testItemDto = itemService.getItem(itemId, userId);
        assertThat(testItemDto.getName()).isEqualTo(itemDto.getName());
    }

    @Test
    void testGetAllItemForUser() {
        Long userId = userRepository.save(user).getId();
        itemService.create(userId, itemDto);
        Collection<ItemExtDto> itemsDto = itemService.getAllForUser(userId, 0, 10);
        assertThat(itemsDto).hasSize(1);
    }

    @Test
    void testSearchItem() {
        Long userId = userRepository.save(user).getId();
        itemService.create(userId, itemDto);
        Collection<ItemDto> itemsDto = itemService.search("дре", 0, 10);
        assertThat(new ArrayList<>(itemsDto).get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void testCreateComment() {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemService.create(userId, itemDto).getId();
        user.setId(userId);
        itemDto.setId(itemId);
        Booking booking = new Booking(
                2L,
                currentDateTime.minusDays(2),
                currentDateTime.minusDays(1),
                ItemMapper.toItem(itemDto),
                user,
                Status.APPROVED
        );
        bookingRepository.save(booking);
        itemService.createComment(itemId, userId,
                new CommentDto(
                        1L,
                        "Text",
                        "Name",
                        LocalDate.now()
                ));
        ItemExtDto itemDto = itemService.getItem(itemId, userId);
        assertThat(itemDto.getComments()).hasSize(1);
    }
}