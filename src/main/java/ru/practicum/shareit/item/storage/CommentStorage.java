package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import javax.persistence.Table;
import java.util.List;

@Repository
@Table(name = "comments")
public interface CommentStorage extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByItem(Long item);
}
