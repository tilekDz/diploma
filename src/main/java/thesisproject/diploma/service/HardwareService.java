package thesisproject.diploma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.entity.Stock;
import thesisproject.diploma.repository.HardwareRepository;

import java.util.List;

@Service
public class HardwareService {

    @Autowired
    private StockService stockService;

    @Autowired
    private HardwareRepository hardwareRepository;

    public Hardware save(Hardware hardware){
        return hardwareRepository.save(hardware);
    }

    public Page<Hardware> getAllHardwares(Specification specification, Pageable pageable) {
        return hardwareRepository.findAll(specification, pageable);
    }


    public List<Hardware> getAllByRoomNumber(Long number){
        return hardwareRepository.findAllByRoomNumberAndIsDeletedFalse(number);
    }

    public Hardware addToHardwareFromStock(Long stockId, String name, String description, String type, Long roomNumber, String campusBlock){
        Hardware hardware = new Hardware(name, description, campusBlock, type, roomNumber, false);

        Stock stock = stockService.getById(stockId);
        stock.setQuantity(stock.getQuantity()-1);
        stockService.save(stock);
        return save(hardware);
    }
}
