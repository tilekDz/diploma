package thesisproject.diploma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.entity.Stock;
import thesisproject.diploma.service.HardwareService;
import thesisproject.diploma.service.StockService;

@Controller
public class MainController {

    @Autowired
    private StockService stockService;

    @Autowired
    private HardwareService hardwareService;

    @RequestMapping(value={"/homePage"}, method = RequestMethod.GET)
    public ModelAndView homePage(){
        ModelAndView modelAndView = new ModelAndView("homePage");
        modelAndView.addObject("stock", stockService.getAllStock());
        return modelAndView;
    }

    @RequestMapping(value = {"/addStock"}, method = RequestMethod.GET)
    public ModelAndView addStock(){
        ModelAndView modelAndView = new ModelAndView("addToStock");
        Stock stock = new Stock();
        modelAndView.addObject("stock", stock);
        return modelAndView;
    }

    @RequestMapping(value = {"/saveStock"}, method = RequestMethod.POST)
    public ModelAndView saveStock(@ModelAttribute Stock stock){
        if (stock != null){
            stockService.save(stock);
        }
        ModelAndView modelAndView = new ModelAndView("redirect:/homePage");
        modelAndView.addObject("stock", stockService.getAllStock());
        return modelAndView;
    }

    @RequestMapping("/getStock/{id}")
    public String showHotel(Model model, @PathVariable("id") Long id) {
        Stock stock = stockService.getById(id);
        model.addAttribute("stock", stock);
        return "stockHardwareDetail";
    }

    @RequestMapping("/addToHardware/{id}")
    public ModelAndView addToHard(@PathVariable("id") Long id){
        ModelAndView modelAndView = new ModelAndView("addToHardware");
        modelAndView.addObject("stock", stockService.getById(id));
        return modelAndView;
    }

    @RequestMapping(value = "/saveToHardware", method = RequestMethod.POST)
    public ModelAndView addHardware(@RequestParam("id") Long id,
                                    @RequestParam("name") String name,
                                    @RequestParam("description") String description,
                                    @RequestParam("roomNumber") Long roomNumber,
                                    @RequestParam("campus") String campusBlock){
        hardwareService.addToHardwareFromStock(id, name, description, roomNumber, campusBlock);
        ModelAndView modelAndView = new ModelAndView("homePage");
        modelAndView.addObject("stock", stockService.getAllStock());
        return modelAndView;
    }
}
