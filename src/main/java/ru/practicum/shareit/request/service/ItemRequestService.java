package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.IllegalUserException;
import ru.practicum.shareit.error.exception.UnknownIdException;
import ru.practicum.shareit.error.exception.WrongStateException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemRequestService {
    @Autowired
    RequestRepository requestRepository;

    @Autowired
    UserRepository userRepository;

    public ItemRequest addItemRequest(Long id, ItemRequest itemRequest) {
        itemRequest.setCreated(Timestamp.from(Instant.now()));
        itemRequest.setRequestor(userRepository.findById(id).orElseThrow(UnknownIdException::new));
        return requestRepository.save(itemRequest);
    }

    public List<ItemRequest> getAllRequests(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UnknownIdException("User not found");
        }
        List<ItemRequest> result = requestRepository.findAllByRequestorId(id);
        if (result != null) {
            return result;
        }
        return new ArrayList<>();
    }

    public ItemRequest getRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalUserException("No such user");
        }
        return requestRepository.findById(requestId).orElseThrow(UnknownIdException::new);
    }

    public List<ItemRequest> getAllRequestsWithPages(Long id, int from, int size) {
        if (from < 0) {
            throw new WrongStateException("From cannot be less then 0");
        }
        if (size < 1) {
            throw new WrongStateException("Size cannot be less then 1");
        }
        return requestRepository.findAllWithPagination(id, size, from);
    }
}
