package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorId(Long id);

    @Query(value = "select * from requests where requestor_id != ?1 order by created desc limit ?2 offset ?3",
            nativeQuery = true)
    List<ItemRequest> findAllWithPagination(Long id, int limit, int offset);
}
