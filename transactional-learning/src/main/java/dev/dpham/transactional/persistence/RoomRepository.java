package dev.dpham.transactional.persistence;

import dev.dpham.transactional.persistence.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
