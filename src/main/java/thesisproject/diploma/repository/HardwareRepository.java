package thesisproject.diploma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesisproject.diploma.entity.Hardware;

import java.util.List;

@Repository
public interface HardwareRepository extends JpaRepository<Hardware, Long> {
    
    List<Hardware> findAllByRoomNumber(Long number);
}
