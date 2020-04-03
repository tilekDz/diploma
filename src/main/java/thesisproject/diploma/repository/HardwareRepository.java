package thesisproject.diploma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import thesisproject.diploma.entity.Hardware;

import java.util.List;

@Repository
public interface HardwareRepository extends CrudRepository<Hardware, Long>, JpaRepository<Hardware, Long>, JpaSpecificationExecutor {
    
    List<Hardware> findAllByRoomNumberAndIsDeletedFalse(Long number);

    List<Hardware> findAllByIsDeletedFalse();
}
