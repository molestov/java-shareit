package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertNull;

public class UserMapperTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void testToUserDto() {
        UserDto result = userMapper.toUserDto(null);
        assertNull(result);
    }

    @Test
    public void testToUser() {
        User result = userMapper.toUser(null);
        assertNull(result);
    }
}
