package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    LocalDateTime currentDateTime = LocalDateTime.now();
    User user = new User(1L, "name", "1@1.com");
    User user2 = new User(2L, "name2", "2@2.com");
    User user3 = new User(3L, "name3", "3@3.com");
    User user4 = new User(4L, "name4", "4@4.com");

    Item item = new Item(
            1,
            "Дрель",
            "Просто дрель",
            true,
            user,
            null
    );

    Booking futureBooking;
    Booking pastBooking;
    Booking currentBooking;
    Sort sort = Sort.by(Sort.Direction.DESC, "start");
    Pageable pageable = PageRequest.of(0, 10, sort);

    @BeforeEach
    public void beforeEach() {
        long userId = userRepository.save(user).getId();
        long user2Id = userRepository.save(user2).getId();
        long user3Id = userRepository.save(user3).getId();
        long user4Id = userRepository.save(user4).getId();
        user.setId(userId);
        user2.setId(user2Id);
        user3.setId(user3Id);
        user4.setId(user4Id);
        long itemId = itemRepository.save(item).getId();
        item.setId(itemId);
        futureBooking = new Booking(
                1L,
                currentDateTime.plusDays(1),
                currentDateTime.plusDays(2),
                item,
                user2,
                Status.WAITING
        );
        pastBooking = new Booking(
                2L,
                currentDateTime.minusDays(3),
                currentDateTime.minusDays(2),
                item,
                user3,
                Status.APPROVED
        );
        currentBooking = new Booking(
                3L,
                currentDateTime.minusDays(2),
                currentDateTime.plusDays(2),
                item,
                user4,
                Status.APPROVED
        );
        bookingRepository.save(futureBooking);
        bookingRepository.save(pastBooking);
        bookingRepository.save(currentBooking);
    }

    @Test
    void testFindByBooker_Id() {
        Booking testBooking = bookingRepository.findByBooker_Id(futureBooking.getBooker().getId(), pageable)
                .getContent().get(0);
        assertThat(testBooking.getBooker()).isEqualTo(futureBooking.getBooker());
        assertThat(testBooking.getItem()).isEqualTo(futureBooking.getItem());
    }

    @Test
    void testFindByBooker_IdAndItem_Id() {
        Booking testBooking = bookingRepository.findByBooker_IdAndItem_Id(pastBooking.getBooker().getId(), item.getId())
                .get(0);
        assertThat(testBooking.getBooker()).isEqualTo(pastBooking.getBooker());
        assertThat(testBooking.getItem()).isEqualTo(pastBooking.getItem());
    }

    @Test
    void testFindByBooker_IdAndEndIsBefore() {
        Booking testBooking = bookingRepository.findByBooker_IdAndEndIsBefore(pastBooking.getBooker().getId(),
                currentDateTime, pageable).getContent().get(0);
        assertThat(testBooking.getBooker()).isEqualTo(pastBooking.getBooker());
        assertThat(testBooking.getItem()).isEqualTo(pastBooking.getItem());
    }

    @Test
    void testFindByBooker_IdAndStartIsAfter() {
        Booking testBooking = bookingRepository.findByBooker_IdAndStartIsAfter(futureBooking.getBooker().getId(),
                currentDateTime, pageable).getContent().get(0);
        assertThat(testBooking.getBooker()).isEqualTo(futureBooking.getBooker());
        assertThat(testBooking.getItem()).isEqualTo(futureBooking.getItem());
    }

    @Test
    void testFindByBooker_IdAndStartIsBeforeAndEndIsAfter() {
        Booking testBooking = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                        currentBooking.getBooker().getId(), currentDateTime, currentDateTime, pageable)
                .getContent().get(0);
        assertThat(testBooking.getBooker()).isEqualTo(currentBooking.getBooker());
        assertThat(testBooking.getItem()).isEqualTo(currentBooking.getItem());
    }

    @Test
    void testFindByBooker_IdAndStatus() {
        Booking testBooking = bookingRepository.findByBooker_IdAndStatus(futureBooking.getBooker().getId(),
                Status.WAITING, pageable).getContent().get(0);
        assertThat(testBooking.getBooker()).isEqualTo(futureBooking.getBooker());
        assertThat(testBooking.getItem()).isEqualTo(futureBooking.getItem());
    }

    @Test
    void testFindByItem_Id() {
        List<Booking> testBookings = bookingRepository.findByItem_Id(item.getId());
        assertThat(testBookings).hasSize(3);
    }

    @Test
    void testGetAllForOwner() {
        Page<Booking> testBookings = bookingRepository.getAllForOwner(user.getId(), pageable);
        assertThat(testBookings).hasSize(3);
    }

    @Test
    void testGetPastForOwner() {
        Booking testBooking = bookingRepository.getPastForOwner(user.getId(),
                currentDateTime, pageable).getContent().get(0);
        assertThat(testBooking.getBooker()).isEqualTo(pastBooking.getBooker());
        assertThat(testBooking.getItem()).isEqualTo(pastBooking.getItem());
    }

    @Test
    void testGetFutureForOwner() {
        Booking testBooking = bookingRepository.getFutureForOwner(user.getId(),
                currentDateTime, pageable).getContent().get(0);
        assertThat(testBooking.getBooker()).isEqualTo(futureBooking.getBooker());
        assertThat(testBooking.getItem()).isEqualTo(futureBooking.getItem());
    }

    @Test
    void testGetCurrentForOwner() {
        Booking testBooking = bookingRepository.getCurrentForOwner(user.getId(),
                currentDateTime, pageable).getContent().get(0);
        assertThat(testBooking.getBooker()).isEqualTo(currentBooking.getBooker());
        assertThat(testBooking.getItem()).isEqualTo(currentBooking.getItem());
    }

    @Test
    void testGetAllForOwnerAndStatus() {
        Booking testBooking = bookingRepository.getAllForOwnerAndStatus(user.getId(),
                Status.WAITING, pageable).getContent().get(0);
        assertThat(testBooking.getBooker()).isEqualTo(futureBooking.getBooker());
        assertThat(testBooking.getItem()).isEqualTo(futureBooking.getItem());
    }
}