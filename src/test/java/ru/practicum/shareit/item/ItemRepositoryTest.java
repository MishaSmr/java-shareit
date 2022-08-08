package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class ItemRepositoryTest {

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
        itemRepository.save(item);
        itemRepository.save(item2);
    }

    @Test
    void testFindByOwner_Id() {
        Item testItem = itemRepository.findByOwner_Id(user.getId()).get(0);
        assertThat(testItem.getName()).isEqualTo(item.getName());
    }

    @Test
    void testFindByRequest_Id() {
        Item testItem = itemRepository.findByRequest_Id(itemRequest.getId()).get(0);
        assertThat(testItem.getName()).isEqualTo(item2.getName());
    }

    @Test
    void testSearch() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Item testItem = itemRepository.search("Дре", pageable).getContent().get(0);
        assertThat(testItem.getName()).isEqualTo(item.getName());
    }
}