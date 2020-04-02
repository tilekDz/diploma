package thesisproject.diploma.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import thesisproject.diploma.entity.Stock;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long>, JpaRepository<Stock, Long>, JpaSpecificationExecutor{

    Page<Stock> findAllByIsDeletedFalse(Specification specification, Pageable pageable);

    List<Stock> findAllByOrderByQuantityDesc();

    Stock getByIdAndIsDeletedFalse(Long id);
}
