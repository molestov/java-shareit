package ru.practicum.shareit.request.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    private Long id;
    private String description;
    @Column(name = "requestor_id")
    private Long requestor;
    private Timestamp created;
}
