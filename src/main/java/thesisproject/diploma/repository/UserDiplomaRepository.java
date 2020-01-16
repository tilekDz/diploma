package thesisproject.diploma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesisproject.diploma.entity.UserDiploma;

@Repository
public interface UserDiplomaRepository extends JpaRepository<UserDiploma, Long> {

    UserDiploma findByEmail(String email);
}
