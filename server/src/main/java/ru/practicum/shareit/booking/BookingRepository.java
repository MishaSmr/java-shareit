package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.BookingNotFoundException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBooker_Id(Long bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndItem_Id(Long bookerId, Long itemId);

    Page<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime date, Pageable pageable);

    Page<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime date, Pageable pageable);

    Page<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime date1, LocalDateTime date2,
                                                               Pageable pageable);

    Page<Booking> findByBooker_IdAndStatus(Long bookerId, Status status, Pageable pageable);

    List<Booking> findByItem_Id(Long itemId);

    @Query(" select new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) from Booking b " +
            "left outer join Item i on b.item = i " +
            "left outer join User u on u = i.owner " +
            "where u.id = ?1")
    Page<Booking> getAllForOwner(Long userId, Pageable pageable);

    @Query(" select new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) from Booking b " +
            "left outer join Item i on b.item = i " +
            "left outer join User u on u = i.owner " +
            "where u.id = ?1 " +
            "and b.start < ?2 and b.end > ?2")
    Page<Booking> getCurrentForOwner(Long userId, LocalDateTime localDateTime, Pageable pageable);

    @Query(" select new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) from Booking b " +
            "left outer join Item i on b.item = i " +
            "left outer join User u on u = i.owner " +
            "where u.id = ?1 " +
            "and b.end < ?2")
    Page<Booking> getPastForOwner(Long userId, LocalDateTime localDateTime, Pageable pageable);

    @Query(" select new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) from Booking b " +
            "left outer join Item i on b.item = i " +
            "left outer join User u on u = i.owner " +
            "where u.id = ?1 " +
            "and b.start > ?2")
    Page<Booking> getFutureForOwner(Long userId, LocalDateTime localDateTime, Pageable pageable);

    @Query(" select new Booking(b.id, b.start, b.end, b.item, b.booker, b.status) from Booking b " +
            "left outer join Item i on b.item = i " +
            "left outer join User u on u = i.owner " +
            "where u.id = ?1 " +
            "and b.status = ?2")
    Page<Booking> getAllForOwnerAndStatus(Long userId, Status status, Pageable pageable);

    default void checkBookingId(Long bookingId) {
        try {
            Booking booking = getReferenceById(bookingId);
            BookingMapper.toBookingDto(booking);
        } catch (EntityNotFoundException ex) {
            throw new BookingNotFoundException("Бронирование c таким id не найдено.");
        }
    }

}
