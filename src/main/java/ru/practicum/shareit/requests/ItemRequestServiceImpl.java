package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotDefinedException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestExtDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestValidator validator;

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        isUserDefined(userId);
        userRepository.checkUserId(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        validator.validateItemRequest(itemRequest);
        itemRequest.setRequester(userRepository.getReferenceById(userId));
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestExtDto> getForUser(Long userId) {
        isUserDefined(userId);
        userRepository.checkUserId(userId);
        List<ItemRequestExtDto> result = new ArrayList<>();
        List<ItemRequest> requests = itemRequestRepository.findByRequester_Id(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        for (ItemRequest r : requests) {
            result.add(createItemRequestExtDto(r));
        }
        result.sort(Comparator.comparing(ItemRequestExtDto::getCreated));
        return result;
    }

    @Override
    public Collection<ItemRequestExtDto> getAll(Long userId, Integer from, Integer size) {
        List<ItemRequestExtDto> result = new ArrayList<>();
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of(from, size, sortByCreated);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.getAllByRequester_IdNot(userId, pageable);
        for (ItemRequest r : itemRequestPage.getContent()) {

            result.add(createItemRequestExtDto(r));
        }
        return result;
    }

    @Override
    public ItemRequestExtDto getById(Long userId, Long requestId) {
        itemRequestRepository.checkItemRequestId(requestId);
        userRepository.checkUserId(userId);
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(requestId);
        return createItemRequestExtDto(itemRequest);
    }

    private ItemRequestExtDto createItemRequestExtDto(ItemRequest request) {
        ItemRequestExtDto itemRequestExtDto = ItemRequestMapper.toItemRequestExtDto(request);
        List<Item> items = itemRepository.findByRequest_Id(request.getId());
        if (items.isEmpty()) {
            return itemRequestExtDto;
        }
        itemRequestExtDto.setItems(items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
        return itemRequestExtDto;
    }

    private void isUserDefined(Long userId) {
        if (userId == null) {
            log.warn("Пользователь не определен");
            throw new UserNotDefinedException("Пользователь не определен");
        }
    }
}
