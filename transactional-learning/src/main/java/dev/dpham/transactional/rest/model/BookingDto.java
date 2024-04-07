package dev.dpham.transactional.rest.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class BookingDto {
    private Long id;
    private RoomDto room;
    private PersonDto person;
    private LocalDate fromDay;
    private LocalDate toDay;
}
