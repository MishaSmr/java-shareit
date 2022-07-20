package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtDto;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto create(Long bookerId, BookingDto bookingDto) {
        isUserDefined(bookerId);
        userRepository.checkUserId(bookerId);
        itemRepository.checkItemId(bookingDto.getItemId());
        Item item = itemRepository.getReferenceById(bookingDto.getItemId());
        if (item.getOwner().getId() == bookerId) {
            throw new UserNotFoundException("Пользователь - владелец предмета");
        }
        if (item.getAvailable()) {
            checkDate(bookingDto.getStart(), bookingDto.getEnd());
            Booking booking = BookingMapper.toBooking(bookingDto);
            booking.setItem(item);
            booking.setBooker(userRepository.getReferenceById(bookerId));
            booking.setStatus(Status.WAITING);
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        }
        log.warn("Предмет не доступен для аренды");
        throw new ItemNotAvailableException("Предмет не доступен для аренды");
    }

    @Transactional
    @Override
    public BookingExtDto changeStatus(Long userId, Long bookingId, Boolean approved) {
        isUserDefined(userId);
        userRepository.checkUserId(userId);
        bookingRepository.checkBookingId(bookingId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = booking.getItem();
        if (item.getOwner().getId() != userId) {
            log.warn("Пользователь не владелец предмета");
            throw new UserIsNotOwnerException("Пользователь не владелец предмета");
        }
        if (approved) {
            if (booking.getStatus() == Status.APPROVED) {
                throw new StatusAlreadyChangedException("Уже подтверждено");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingExtDto(bookingRepository.save(booking));
    }

    @Override
    public BookingExtDto getById(Long userId, Long bookingId) {
        isUserDefined(userId);
        userRepository.checkUserId(userId);
        bookingRepository.checkBookingId(bookingId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = booking.getItem();
        if (item.getOwner().getId() == userId || booking.getBooker().getId() == userId) {
            return BookingMapper.toBookingExtDto(booking);
        }
        log.warn("Пользователь не владелец предмета или не автор бронирования");
        throw new BookingNotFoundException("Пользователь не владелец предмета или не автор бронирования");
    }

    @Override
    public List<BookingExtDto> getForBooker(Long bookerId, String state) {
        isUserDefined(bookerId);
        userRepository.checkUserId(bookerId);
        State st = State.valueOf(state.toUpperCase());
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (st) {
            case ALL:
                bookings = bookingRepository.findByBooker_Id(bookerId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.getCurrentForBooker(bookerId, LocalDate.now());
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(bookerId, LocalDate.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(bookerId, LocalDate.now(), sort);
                break;
            default:
                bookings = bookingRepository.findByBooker_IdAndStatus(bookerId, Status.valueOf(state), sort);
        }
        return bookings.stream().map(BookingMapper::toBookingExtDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingExtDto> getForOwner(Long userId, String state) {
        isUserDefined(userId);
        userRepository.checkUserId(userId);
        if (itemRepository.findByOwner_Id(userId).isEmpty()) {
            log.warn("У пользователя нет предметов");
            return Collections.emptyList();
        }
        State st = State.valueOf(state.toUpperCase());
        List<Booking> bookings = new ArrayList<>();
        List<Item> items = itemRepository.findByOwner_Id(userId);
        for (Item i : items) {
            bookings.addAll(bookingRepository.findByItem_Id(i.getId()));
        }
        bookings.sort((Comparator.comparing(Booking::getStart)).reversed());
        switch (st) {
            case ALL:
                return bookings.stream().map(BookingMapper::toBookingExtDto).collect(Collectors.toList());
            case CURRENT:
                return bookings.stream()
                        .filter(b -> (b.getStart().isBefore(LocalDate.now()) || b.getStart().isEqual(LocalDate.now()))
                                && (b.getEnd().isAfter(LocalDate.now()) || b.getEnd().isEqual(LocalDate.now()))
                                && b.getStatus().equals(Status.APPROVED))
                        .map(BookingMapper::toBookingExtDto).collect(Collectors.toList());
            case PAST:
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(LocalDate.now()))
                        .map(BookingMapper::toBookingExtDto).collect(Collectors.toList());
            case FUTURE:
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(LocalDate.now()))
                        .map(BookingMapper::toBookingExtDto).collect(Collectors.toList());
            default:
                return bookings.stream()
                        .filter(b -> b.getStatus() == Status.valueOf(state))
                        .map(BookingMapper::toBookingExtDto).collect(Collectors.toList());
        }
    }

    private void isUserDefined(Long userId) {
        if (userId == null) {
            log.warn("Пользователь не определен");
            throw new UserNotDefinedException("Пользователь не определен");
        }
    }

    private void checkDate(LocalDate start, LocalDate end) {
        if (start.isBefore(LocalDate.now()) || end.isBefore(LocalDate.now()) || start.isAfter(end)) {
            throw new BookingDateException("Указаны неверные даты бронирования");
        }
    }
}
