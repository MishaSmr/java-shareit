package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user = new User(1L, "name", "1@1.com");
    User user2 = new User(2L, "name2", "2@2.com");
    ItemRequest itemRequest = new ItemRequest(
            1,
            "Description",
            user2,
            LocalDateTime.now()
    );
    ItemRequest itemRequest2 = new ItemRequest(
            2,
            "Description two",
            user,
            LocalDateTime.now()
    );
    Item item = new Item(
            1,
            "Дрель",
            "Просто дрель",
            true,
            user,
            null
    );
    Item item2 = new Item(
            2,
            "Второй предмет",
            "Описание",
            true,
            user2,
            null
    );

    @BeforeEach
    public void beforeEach() {
        long userId = userRepository.save(user).getId();
        long user2Id = userRepository.save(user2).getId();
        user.setId(userId);
        user2.setId(user2Id);
        item2.setRequest(itemRequestRepository.save(itemRequest));
        itemRequestRepository.save(itemRequest2);
        itemRepository.save(item);
        itemRepository.save(item2);
    }

    @Test
    void testFindByRequester_Id() {
        ItemRequest testItemRequest = itemRequestRepository.findByRequester_Id(user2.getId()).get(0);
        assertThat(testItemRequest.getDescription()).isEqualTo(itemRequest.getDescription());
    }

    @Test
    void testGetAllByRequester_IdNot() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        ItemRequest testItemRequest = itemRequestRepository.getAllByRequester_IdNot(user.getId(), pageable)
                .getContent().get(0);
        assertThat(testItemRequest.getDescription()).isEqualTo(itemRequest.getDescription());
    }
}