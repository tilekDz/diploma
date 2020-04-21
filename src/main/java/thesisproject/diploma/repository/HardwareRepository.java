package thesisproject.diploma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import thesisproject.diploma.entity.Hardware;

import java.util.List;

@Repository
public interface HardwareRepository extends CrudRepository<Hardware, Long>, JpaRepository<Hardware, Long>, JpaSpecificationExecutor {
    
    List<Hardware> findAllByRoomNumberAndIsDeletedFalse(Long number);

    @Query(value = "select * from hardware_diploma where room_number = ?1 and campus_block = ?2 and is_deleted = 0", nativeQuery = true)
    List<Hardware> findAllByRoomNumberAndCampusBlockAndIsDeletedFalse(Long number, String campus);

    List<Hardware> findAllByIsDeletedFalse();
}
