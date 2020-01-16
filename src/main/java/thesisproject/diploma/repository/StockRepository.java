package thesisproject.diploma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesisproject.diploma.entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

}
