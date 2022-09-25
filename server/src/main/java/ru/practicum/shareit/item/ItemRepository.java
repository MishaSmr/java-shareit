package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_Id(Long userId);

    Page<Item> findByOwner_Id(Long userId, Pageable pageable);

    List<Item> findByRequest_Id(Long requestId);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            " and i.available = true")
    Page<Item> search(String text, Pageable pageable);

    default void checkItemId(Long itemId) {
        try {
            Item item = getReferenceById(itemId);
            ItemMapper.toItemDto(item);
        } catch (EntityNotFoundException ex) {
            throw new UserNotFoundException("Предмет c таким id не найден.");
        }
    }
}
