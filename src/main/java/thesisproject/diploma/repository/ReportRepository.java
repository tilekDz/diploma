package thesisproject.diploma.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import thesisproject.diploma.entity.Report;

import java.util.List;

@Repository
public interface ReportRepository extends CrudRepository<Report, Long>, JpaRepository<Report, Long>, JpaSpecificationExecutor {
}
