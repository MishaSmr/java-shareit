package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId,
                         @RequestHeader("X-Sharer-User-Id") Long userId,
                         @RequestBody ItemDto itemDto) {
        log.info("Update item {}, userId={}", itemDto.getId(), userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items for user, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllForUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search items, text={}, from={}, size={}", text, from, size);
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                             @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Add comment {}, itemId={}, userId={}", commentDto, itemId, userId);
        return itemClient.createComment(itemId, userId, commentDto);
    }
}
