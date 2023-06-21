package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.Table;
import java.util.List;
import java.util.Optional;

@Repository
@Table(name = "items")
public interface ItemStorage extends JpaRepository<Item, Long> {
    Item save(Item item);
    Optional<Item> findById(Long id);
    List<Item> findAllByOwnerOrderById(Long id);
    boolean existsById(Long id);
    @Query(value = "select * from items where lower(name) like concat('%',lower(:keyword),'%') or lower(description) " +
            "like concat('%',lower(:keyword),'%') and available=true", nativeQuery = true)
    List<Item> findItemsByKeyword(@Param("keyword") String keyword);
}
