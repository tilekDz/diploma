package thesisproject.diploma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import thesisproject.diploma.entity.Stock;
import thesisproject.diploma.repository.StockRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public Page<Stock> getAllStock(Specification specification, Pageable pageable){
        return stockRepository.findAll(specification, pageable);
    }

    public List<Stock> getTop3FromStock(){
        List<Stock> stockList = stockRepository.findAllByOrderByQuantityDesc();
        return stockList.subList(0, 3);
    }

    public Stock save(Stock stock){
        stock.setIsDeleted(false);
        return stockRepository.save(stock);
    }

    public void deleteStock(Long id){
        Stock stock = getById(id);
        if(stock!=null){
            stock.setIsDeleted(true);
            stockRepository.save(stock);
        }
    }

    public Stock getById(Long id){
        return stockRepository.getByIdAndIsDeletedFalse(id);
    }
}
