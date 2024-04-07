package dev.dpham.transactional.rest;

import dev.dpham.transactional.persistence.model.Booking;
import dev.dpham.transactional.persistence.model.Person;
import dev.dpham.transactional.persistence.model.Room;
import dev.dpham.transactional.rest.model.BookingDto;
import dev.dpham.transactional.rest.model.PersonDto;
import dev.dpham.transactional.rest.model.RoomDto;
import org.springframework.stereotype.Component;

@Component
public class BookingDtoConverter {
    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .room(RoomDto.builder()
                        .id(booking.getRoom().getId())
                        .name(booking.getRoom().getName())
                        .build())
                .person(PersonDto.builder()
                        .id(booking.getPerson().getId())
                        .name(booking.getPerson().getName())
                        .build())
                .fromDay(booking.getFromDay())
                .toDay(booking.getToDay())
                .build();
    }

    public Booking toEntity(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .room(Room.builder()
                        .id(bookingDto.getRoom().getId())
                        .name(bookingDto.getRoom().getName())
                        .build())
                .person(Person.builder()
                        .id(bookingDto.getPerson().getId())
                        .name(bookingDto.getPerson().getName())
                        .build())
                .fromDay(bookingDto.getFromDay())
                .toDay(bookingDto.getToDay())
                .build();
    }


}
