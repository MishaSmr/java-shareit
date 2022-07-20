package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.BookingNotFoundException;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndItem_Id(Long bookerId, Long itemId);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime date, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime date, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findByItem_Id(Long itemId);

    @Query(" select b from Booking b " +
            "where b.booker = ?1 " +
            "and b.start <= ?2 and b.end >= ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.start desc")
    List<Booking> getCurrentForBooker(Long bookerId, LocalDateTime date);

    default void checkBookingId(Long bookingId) {
        try {
            Booking booking = getReferenceById(bookingId);
            BookingMapper.toBookingDto(booking);
        } catch (EntityNotFoundException ex) {
            throw new BookingNotFoundException("Бронирование c таким id не найдено.");
        }
    }

}
