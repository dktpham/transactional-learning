package dev.dpham.transactional.rest.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomDto {
    private Long id;
    private String name;
}
