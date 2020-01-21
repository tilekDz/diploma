package thesisproject.diploma.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Hardware> getAllHardwares(){
        return hardwareRepository.findAll();
    }


    public List<Hardware> getAllByRoomNumber(Long number){
        return hardwareRepository.findAllByRoomNumber(number);
    }

    public Hardware addToHardwareFromStock(Long stockId, String name, String description, Long roomNumber, String campusBlock){
        Hardware hardware = new Hardware();
        hardware.setName(name);
        hardware.setDescription(description);
        hardware.setRoomNumber(roomNumber);
        hardware.setCampusBlock(campusBlock);

        Stock stock = stockService.getById(stockId);
        stock.setQuantity(stock.getQuantity()-1);
        stockService.save(stock);
        return save(hardware);
    }
}
