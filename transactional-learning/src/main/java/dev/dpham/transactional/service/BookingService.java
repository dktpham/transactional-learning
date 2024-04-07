package dev.dpham.transactional.service;

import dev.dpham.transactional.persistence.BookingRepository;
import dev.dpham.transactional.persistence.PersonRepository;
import dev.dpham.transactional.persistence.RoomRepository;
import dev.dpham.transactional.persistence.model.Booking;
import dev.dpham.transactional.persistence.model.Person;
import dev.dpham.transactional.persistence.model.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PersonRepository personRepository;
    private final ReentrantLock lock = new ReentrantLock();

    public Booking createNewBookingWithLock(Booking b) {
        lock.lock();
        List<Booking> latestBookings = bookingRepository.findLatestBookingForRoom(b.getRoom().getId(), b.getFromDay(), b.getToDay());
        if (!latestBookings.isEmpty()) {
            throw new IllegalStateException("Booking for room already exists");
        } else {
            Room room = roomRepository.findById(b.getRoom().getId()).orElseThrow(() -> new IllegalStateException("Room not found"));
            Person person = personRepository.findById(b.getPerson().getId()).orElseThrow(() -> new IllegalStateException("Person not found"));

            Booking booking = bookingRepository.saveAndFlush(b.toBuilder()
                    .room(room)
                    .person(person)
                    .build());
            lock.unlock();
            return booking;
        }
    }

    @Retryable
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Booking createNewBookingWithSerializableIsolation(Booking b) {
        List<Booking> latestBookings = bookingRepository.findLatestBookingForRoom(b.getRoom().getId(), b.getFromDay(), b.getToDay());
        if (!latestBookings.isEmpty()) {
            throw new IllegalStateException("Booking for room already exists");
        } else {
            Room room = roomRepository.findById(b.getRoom().getId()).orElseThrow(() -> new IllegalStateException("Room not found"));
            Person person = personRepository.findById(b.getPerson().getId()).orElseThrow(() -> new IllegalStateException("Person not found"));

            return bookingRepository.saveAndFlush(b.toBuilder()
                    .room(room)
                    .person(person)
                    .build());
        }
    }

}