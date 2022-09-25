package ru.practicum.shareit.requests;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequester_Id(Long userId);

    Page<ItemRequest> getAllByRequester_IdNot(Long userId, Pageable pageable);

    default void checkItemRequestId(Long itemRequestId) {
        try {
            ItemRequest itemRequest = getReferenceById(itemRequestId);
            ItemRequestMapper.toItemRequestDto(itemRequest);
        } catch (EntityNotFoundException ex) {
            throw new ItemRequestNotFoundException("Запрос c таким id не найдено.");
        }
    }

}
