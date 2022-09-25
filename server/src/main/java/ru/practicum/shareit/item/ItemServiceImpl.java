package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.CommentCreateException;
import ru.practicum.shareit.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.exceptions.UserNotDefinedException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    private final ItemValidator validator;

    @Transactional
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        isUserDefined(userId);
        userRepository.checkUserId(userId);
        Item item = ItemMapper.toItem(itemDto);
        validator.validateItem(item);
        item.setOwner(userRepository.getReferenceById(userId));
        Long requestId = itemDto.getRequestId();
        item.setRequest(requestId != null ? itemRequestRepository.getReferenceById(requestId) : null);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        isUserDefined(userId);
        userRepository.checkUserId(userId);
        Item item = itemRepository.getReferenceById(itemId);
        if (item.getOwner().getId() != userId) {
            log.warn("Пользователь не владелец предмета");
            throw new UserIsNotOwnerException("Пользователь не владелец предмета");
        }
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        validator.validateItem(item);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemExtDto getItem(Long itemId, Long userId) {
        itemRepository.checkItemId(itemId);
        userRepository.checkUserId(userId);
        Item item = itemRepository.getReferenceById(itemId);
        if (item.getOwner().getId() == userId) {
            return createItemExtDto(item);
        }
        return addCommentToItemExtDto(item);
    }

    @Override
    public Collection<ItemExtDto> getAllForUser(Long userId, Integer from, Integer size) {
        isUserDefined(userId);
        List<ItemExtDto> result = new ArrayList<>();
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        Page<Item> items = itemRepository.findByOwner_Id(userId, pageable);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        for (Item i : items) {
            result.add(createItemExtDto(i));
        }
        return result;
    }

    @Override
    public Collection<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));
        return itemRepository.search(text, pageable).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Комментарий не может быть пустым");
        }
        isUserDefined(userId);
        itemRepository.checkItemId(itemId);
        userRepository.checkUserId(userId);
        Comment comment = CommentMapper.toComment(commentDto);
        List<Booking> bookings = bookingRepository.findByBooker_IdAndItem_Id(userId, itemId);
        if (bookings.isEmpty()) {
            throw new CommentCreateException("Пользователь не брал предмет в аренду");
        }
        for (Booking b : bookings) {
            if (!b.getEnd().isAfter(LocalDateTime.now())) {
                comment.setItem(itemRepository.getReferenceById(itemId));
                comment.setAuthor(userRepository.getReferenceById(userId));
                comment.setCreated(LocalDate.now());
                return CommentMapper.toCommentDto(commentRepository.save(comment));
            }
        }
        throw new CommentCreateException("Пользователь не закончил ни одной аренды предмета");
    }

    private void isUserDefined(Long userId) {
        if (userId == null) {
            log.warn("Пользователь не определен");
            throw new UserNotDefinedException("Пользователь не определен");
        }
    }

    private ItemExtDto createItemExtDto(Item i) {
        ItemExtDto itemDto = ItemMapper.toItemExtDto(i);
        BookingDto lastBooking = bookingRepository.findByItem_Id(i.getId()).stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now())
                        || (b.getStart().isBefore(LocalDateTime.now()) && b.getEnd().isAfter(LocalDateTime.now())))
                .max(Comparator.comparing(Booking::getEnd))
                .map(BookingMapper::toBookingDto).orElse(null);
        BookingDto nextBooking = bookingRepository.findByItem_Id(i.getId()).stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::toBookingDto).orElse(null);

        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        List<CommentDto> comments = commentRepository.findByItem_Id(i.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
        return itemDto;
    }

    private ItemExtDto addCommentToItemExtDto(Item i) {
        ItemExtDto itemDto = ItemMapper.toItemExtDto(i);
        List<CommentDto> comments = commentRepository.findByItem_Id(i.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
        return itemDto;
    }

    public ItemValidator getValidator() {
        return validator;
    }
}
