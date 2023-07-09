package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.IllegalUserException;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemRequestService {

    private RequestRepository requestRepository;

    private UserRepository userRepository;

    public ItemRequest addItemRequest(Long id, ItemRequest itemRequest) {
        itemRequest.setCreated(Timestamp.from(Instant.now()));
        itemRequest.setRequestor(userRepository.findById(id).orElseThrow(UnknownIdException::new));
        return requestRepository.save(itemRequest);
    }

    public List<ItemRequest> getAllRequests(Long id, Pageable pageable) {
        if (!userRepository.existsById(id)) {
            throw new UnknownIdException("User not found");
        }
        return requestRepository.findAllByRequestorId(id);
    }

    public ItemRequest getRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalUserException("No such user");
        }
        return requestRepository.findById(requestId).orElseThrow(UnknownIdException::new);
    }

    public List<ItemRequest> getAllRequestsWithPages(Long id, int from, int size) {
        return requestRepository.findAllWithPagination(id, from, size);
    }
}
