package thesisproject.diploma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.entity.Stock;
import thesisproject.diploma.pattern.HardwarePattern;
import thesisproject.diploma.pattern.StockPattern;
import thesisproject.diploma.service.HardwareService;
import thesisproject.diploma.service.StockService;
import thesisproject.diploma.service.UserDiplomaService;
import thesisproject.diploma.specification.HardwareSpecification;
import thesisproject.diploma.specification.StockSpecification;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class HardwareController {

    @Autowired
    private StockService stockService;

    @Autowired
    private HardwareService hardwareService;

    @Autowired
    private UserDiplomaService userDiplomaService;

    @RequestMapping(value = {"/getHardwarePage"}, method = RequestMethod.GET)
    public ModelAndView getHardwarePage(@RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        return getModelAndView("hardwarePage", new HardwarePattern(), page, size);
    }

    @RequestMapping(value = "/searchHardware")
    public ModelAndView searchFromStock(@ModelAttribute HardwarePattern hardwarePattern,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        return getModelAndView("hardwarePage", hardwarePattern, page, size);
    }

    @RequestMapping(value = "/listHardware")
    public ModelAndView listHardware(
            @ModelAttribute HardwarePattern hardwarePattern,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("description") String description,
            @RequestParam("campusBlock") String campusBlock,
            @RequestParam("numberRoom") Long numberRoom,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        hardwarePattern.setName(name);
        hardwarePattern.setDescription(description);
        hardwarePattern.setType(type);
        hardwarePattern.setCampusBlock(campusBlock);
        hardwarePattern.setRoomNumber(numberRoom);
        return getModelAndView("hardwarePage", hardwarePattern, page, size);
    }

    private ModelAndView getModelAndView(String view, HardwarePattern hardwarePattern, Optional<Integer> page, Optional<Integer> size){
        ModelAndView modelAndView = new ModelAndView(view);

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
//        int evalPageSize = stockPattern.getPageSize() == null ? PaginationConstant.INITIAL_PAGE_SIZE : stockPattern.getPageSize();
//        int evalPage = (stockPattern.getPage() == null || stockPattern.getPage() < 1) ? PaginationConstant.INITIAL_PAGE : stockPattern.getPage()-1;

        Specification<Hardware> specification = new HardwareSpecification(hardwarePattern);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.Direction.DESC, "createdDate");

        Page<Hardware> hardwares = hardwareService.getAllHardwares(specification, pageable);

        int totalPages = hardwares.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }

        modelAndView.addObject("hardwares", hardwares);
        modelAndView.addObject("pattern", hardwarePattern);
        return modelAndView;
    }
}
