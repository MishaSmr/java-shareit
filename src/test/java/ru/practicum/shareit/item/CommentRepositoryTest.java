package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;

    User user = new User(1L, "name", "1@1.com");
    Item item = new Item(
            1,
            "Дрель",
            "Просто дрель",
            true,
            user,
            null
    );
    Comment comment = new Comment(
            1L,
            "Text",
            null,
            user,
            LocalDate.now()
    );

    @BeforeEach
    public void beforeEach() {
        long userId = userRepository.save(user).getId();
        user.setId(userId);
        comment.setItem(itemRepository.save(item));
        commentRepository.save(comment);
    }

    @Test
    void testFindByItem_Id() {
        Comment testComment = commentRepository.findByItem_Id(comment.getItem().getId()).get(0);
        assertThat(testComment.getText()).isEqualTo(comment.getText());
    }
}