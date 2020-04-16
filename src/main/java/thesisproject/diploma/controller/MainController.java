package thesisproject.diploma.controller;

import com.google.zxing.WriterException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.docx4j.org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import thesisproject.diploma.commons.CyrilicToAsciiConvertUtil;
import thesisproject.diploma.entity.FileInfo;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.entity.Stock;
import thesisproject.diploma.entity.UserDiploma;
import thesisproject.diploma.pagination.Pager;
import thesisproject.diploma.pagination.PaginationConstant;
import thesisproject.diploma.pattern.StockPattern;
import thesisproject.diploma.service.HardwareService;
import thesisproject.diploma.service.StockService;
import thesisproject.diploma.service.UserDiplomaService;
import thesisproject.diploma.specification.SpecificatinHelper;
import thesisproject.diploma.specification.StockSpecification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.data.domain.PageRequest.of;

@Controller
public class MainController {

    @Autowired
    private StockService stockService;

    @Autowired
    private HardwareService hardwareService;

    @Autowired
    private UserDiplomaService userDiplomaService;

    @RequestMapping(value={"/homePage"}, method = RequestMethod.GET)
    public ModelAndView homePage(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        ModelAndView modelAndView = new ModelAndView("homePage");
        modelAndView.addObject("email", auth.getPrincipal());
        return modelAndView;
    }

    @RequestMapping(value={"/userPage"}, method = RequestMethod.GET)
    public ModelAndView userPage(HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        ModelAndView modelAndView = new ModelAndView("userPage");
        UserDiploma user = userDiplomaService.findUserByEmail(request.getUserPrincipal().getName());
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @RequestMapping(value = {"/addStock"}, method = RequestMethod.GET)
    public ModelAndView addStock(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        ModelAndView modelAndView = new ModelAndView("addToStock");
        Stock stock = new Stock();
        modelAndView.addObject("stock", stock);
        return modelAndView;
    }

    @RequestMapping(value = {"/getStockPage"}, method = RequestMethod.GET)
    public ModelAndView getStockPage(@RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        return getModelAndView("stockPage", new StockPattern(), page, size);
    }

    @RequestMapping(value = {"/saveStock"}, method = RequestMethod.POST)
    public String saveStock(@ModelAttribute Stock stock){
        if (stock != null){
            stockService.save(stock);
        }
        return "redirect:/getStockPage";
    }

    @RequestMapping("/getStock/{id}")
    public ModelAndView showHotel(Model model, @PathVariable("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        Stock stock = stockService.getById(id);
        model.addAttribute("stock", stock);
        return new ModelAndView("stockHardwareDetail");
    }

    @RequestMapping("/addToHardware/{id}")
    public ModelAndView addToHard(@PathVariable("id") Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        ModelAndView modelAndView = new ModelAndView("addToHardware");
        modelAndView.addObject("stock", stockService.getById(id));
        return modelAndView;
    }

    @RequestMapping(value = "/saveToHardware", method = RequestMethod.POST)
    public String addHardware(@RequestParam("id") Long id,
                                    @RequestParam("name") String name,
                                    @RequestParam("description") String description,
                                    @RequestParam("type") String type,
                                    @RequestParam("roomNumber") Long roomNumber,
                                    @RequestParam("campus") String campusBlock) throws Exception {
        hardwareService.addToHardwareFromStock(id, name, description, type, roomNumber, campusBlock);

        return "redirect:/getHardwarePage";
    }

    @RequestMapping(value = "/listStock", method = RequestMethod.GET)
    public ModelAndView listStock(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        return getModelAndView("stockPage", new StockPattern(), page, size);
    }

    @RequestMapping(value = "/searchStock")
    public ModelAndView searchFromStock(@ModelAttribute StockPattern stockPattern,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        return getModelAndView("stockPage", stockPattern, page, size);
    }

    @RequestMapping(value = "/downloadQR/{id}")
    @ResponseBody
    public String downloadQR(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        FileInfo downFile = hardwareService.findById(id).getFileTemplate();
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setContentLength((int) downFile.getFileData().getContent().length);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + CyrilicToAsciiConvertUtil.transliterate(downFile.getName()) + "\"");

        FileCopyUtils.copy(downFile.getFileData().getContent(), response.getOutputStream());
//        IOUtils.copy(new FileInputStream(new File(String.valueOf(downFile))), response.getOutputStream());
        return "redirect:/getStockPage";
    }

    private ModelAndView getModelAndView(String view, StockPattern stockPattern, Optional<Integer> page, Optional<Integer> size){
        ModelAndView modelAndView = new ModelAndView(view);

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
//        int evalPageSize = stockPattern.getPageSize() == null ? PaginationConstant.INITIAL_PAGE_SIZE : stockPattern.getPageSize();
//        int evalPage = (stockPattern.getPage() == null || stockPattern.getPage() < 1) ? PaginationConstant.INITIAL_PAGE : stockPattern.getPage()-1;

        Specification<Stock> specification = new StockSpecification(stockPattern);
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.Direction.DESC, "quantity");

        Page<Stock> stocks = stockService.getAllStock(specification, pageable);

        int totalPages = stocks.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }

        modelAndView.addObject("stock", stocks);
        modelAndView.addObject("pattern", stockPattern);
        return modelAndView;
    }

//    @RequestMapping(value = "/downloadQR", method = RequestMethod.GET, produces = "application/pdf")
//    public byte[] downloadPdf(@RequestParam(value = "id") Long id) throws IOException {
//        FileInfo downFile = hardwareService.findById(id).getFileTemplate();
//        return (downFile.getFileData().getContent());
//    }
}
