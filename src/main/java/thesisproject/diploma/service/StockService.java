package thesisproject.diploma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thesisproject.diploma.entity.Stock;
import thesisproject.diploma.repository.StockRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public List<Stock> getAllStock(){
        return stockRepository.findAll();
    }

    public Stock save(Stock stock){
        return stockRepository.save(stock);
    }

    public Stock getById(Long id){
        return stockRepository.getById(id);
    }
}
