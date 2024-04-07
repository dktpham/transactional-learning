package dev.dpham.transactional.rest;

import dev.dpham.transactional.persistence.model.Booking;
import dev.dpham.transactional.rest.model.BookingDto;
import dev.dpham.transactional.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {
    private final BookingService bookingService;
    private final BookingDtoConverter bookingDtoConverter;

    @PostMapping("booking")
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingDto booking) {
        Booking newBooking = bookingService.createNewBookingWithLock(bookingDtoConverter.toEntity(booking));
        return ResponseEntity.ok(bookingDtoConverter.toDto(newBooking));
    }

    @PostMapping("booking_serializable")
    public ResponseEntity<BookingDto> createBooking2(@RequestBody BookingDto booking) {
        Booking newBooking = bookingService.createNewBookingWithSerializableIsolation(bookingDtoConverter.toEntity(booking));
        return ResponseEntity.ok(bookingDtoConverter.toDto(newBooking));
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(CONFLICT.value()).body(e.getMessage());
    }
}
