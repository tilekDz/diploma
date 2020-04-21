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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import thesisproject.diploma.service.ReportDocxCreate;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.pagination.Pager;
import thesisproject.diploma.pattern.HardwarePattern;
import thesisproject.diploma.service.HardwareService;
import thesisproject.diploma.service.StockService;
import thesisproject.diploma.service.UserDiplomaService;
import thesisproject.diploma.specification.HardwareSpecification;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static thesisproject.diploma.pagination.PaginationConstant.BUTTONS_TO_SHOW;

@Controller
public class HardwareController {

    private static final int[] PAGE_SIZES = { 5, 10};

    @Autowired
    private StockService stockService;

    @Autowired
    private HardwareService hardwareService;

    @Autowired
    private UserDiplomaService userDiplomaService;

    @Autowired
    private ReportDocxCreate reportDocxCreate;

    @RequestMapping("/getHardware/{id}")
    public ModelAndView showHotel(Model model, @PathVariable("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        Hardware hardware = hardwareService.findById(id);
        model.addAttribute("hardware", hardware);
        return new ModelAndView("hardwareDetail");
    }

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

    @RequestMapping(value={"/createReport"}, method = RequestMethod.GET)
    public ModelAndView reportPage(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        ModelAndView modelAndView = new ModelAndView("createReport");
        return modelAndView;
    }

    @RequestMapping(value = "/createReportWord", method = RequestMethod.GET)
    public void outToWord(@RequestParam("campusBlock") String campusBlock,
                          @RequestParam("numberRoom") Long numberRoom,
                          HttpServletResponse response, RedirectAttributes redirectAttributes){
        response.setHeader("Content-Disposition", "attachment; filename=\"word.docx\"");
        reportDocxCreate.createDocxFile(response, numberRoom, campusBlock);
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

        Pager pager = new Pager(hardwares.getTotalPages(),hardwares.getNumber(),BUTTONS_TO_SHOW);

        modelAndView.addObject("selectedPageNumber", pageSize);
        modelAndView.addObject("pager", pager);
        modelAndView.addObject("pageSizes", PAGE_SIZES);
        modelAndView.addObject("hardwares", hardwares);
        modelAndView.addObject("pattern", hardwarePattern);
        return modelAndView;
    }
}
