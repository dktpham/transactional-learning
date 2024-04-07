package dev.dpham.transactional.rest.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PersonDto {
    private Long id;
    private String name;
}
