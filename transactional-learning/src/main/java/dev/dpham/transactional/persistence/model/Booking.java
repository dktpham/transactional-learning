package dev.dpham.transactional.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Builder(toBuilder = true)
@Getter
@Setter
@Entity
@Table(name = "booking")
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private Person person;
    @ManyToOne
    private Room room;
    @Column(name = "from_day", nullable = false)
    private LocalDate fromDay;
    @Column(name = "to_day", nullable = false)
    private LocalDate toDay;

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) && Objects.equals(person, booking.person) && Objects.equals(room, booking.room) && Objects.equals(fromDay, booking.fromDay) && Objects.equals(toDay, booking.toDay);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(person);
        result = 31 * result + Objects.hashCode(room);
        result = 31 * result + Objects.hashCode(fromDay);
        result = 31 * result + Objects.hashCode(toDay);
        return result;
    }
}
