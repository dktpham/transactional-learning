package dev.dpham.transactional.persistence;

import dev.dpham.transactional.persistence.model.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
//    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query(nativeQuery = true, value = "select * from booking b where b.room_id = ?1 and (b.from_day, b.to_day) overlaps (?2, ?3)")
    List<Booking> findLatestBookingForRoom(Long room, LocalDate from, LocalDate until);
}
