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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import thesisproject.diploma.commons.CyrilicToAsciiConvertUtil;
import thesisproject.diploma.dto.FileDTO;
import thesisproject.diploma.dto.FileInfoDTO;
import thesisproject.diploma.entity.FileInfo;
import thesisproject.diploma.entity.Report;
import thesisproject.diploma.pagination.PaginationConstant;
import thesisproject.diploma.pattern.ReportPattern;
import thesisproject.diploma.service.*;
import thesisproject.diploma.entity.Hardware;
import thesisproject.diploma.pagination.Pager;
import thesisproject.diploma.pattern.HardwarePattern;
import thesisproject.diploma.specification.HardwareSpecification;
import thesisproject.diploma.specification.ReportSpecification;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static thesisproject.diploma.pagination.PaginationConstant.BUTTONS_TO_SHOW;

@Controller
public class HardwareController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private HardwareService hardwareService;

    @Autowired
    private UserDiplomaService userDiplomaService;

    @Autowired
    private ReportDocxCreate reportDocxCreate;

    @RequestMapping("/getHardware/{id}")
    public ModelAndView showHotel(Model model, @PathVariable("id") Long id) {
        boolean isNotLogged = noLogged();
        if(isNotLogged){
            return new ModelAndView("index");
        }
        Hardware hardware = hardwareService.findById(id);
        model.addAttribute("hardware", hardware);
        return new ModelAndView("hardwareDetail");
    }

    @RequestMapping(value = {"/getHardwarePage"}, method = RequestMethod.GET)
    public ModelAndView getHardwarePage(@RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size){
        boolean isNotLogged = noLogged();
        if(isNotLogged){
            return new ModelAndView("index");
        }
        return getModelAndView("hardwarePage", new HardwarePattern(), page, size);
    }

    @RequestMapping(value = "/searchHardware")
    public ModelAndView searchFromStock(@ModelAttribute HardwarePattern hardwarePattern,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size){
        boolean isNotLogged = noLogged();
        if(isNotLogged){
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
        boolean isNotLogged = noLogged();
        if(isNotLogged){
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
        boolean isNotLogged = noLogged();
        if(isNotLogged){
            return new ModelAndView("index");
        }
        ModelAndView modelAndView = new ModelAndView("createReport");
        return modelAndView;
    }

    @RequestMapping(value = "/createReportWord", method = RequestMethod.GET)
    public String outToWord(@RequestParam("campusBlock") String campusBlock,
                          @RequestParam("numberRoom") Long numberRoom,
                          @RequestParam("date") String date,
                          @RequestParam("paperNum") String paperNum,
                          HttpServletResponse response, RedirectAttributes redirectAttributes) throws IOException {
        List<Hardware> hardwareList = hardwareService.getAllByRoomNumberAndCampusAndDeletedFalse(numberRoom, campusBlock);
        if(hardwareList!=null && !hardwareList.isEmpty()) {
            response.setHeader("Content-Disposition", "attachment; filename=\"imsReport.docx\"");
            reportDocxCreate.createDocxFile(response, numberRoom, campusBlock, date, paperNum);
            return null;
        }
        redirectAttributes.addAttribute("error", true);
        return "redirect:/createReport";
    }

    @RequestMapping("/deleteHardware/{id}")
    public ModelAndView deleteHardware(@PathVariable("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        hardwareService.deleteHardware(id);
        return new ModelAndView("redirect:/getHardwarePage");
    }


    //Report Page
    @RequestMapping(value = {"/getReportPage"}, method = RequestMethod.GET)
    public ModelAndView getReportPage(@RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size){
        boolean isNotLogged = noLogged();
        if(isNotLogged){
            return new ModelAndView("index");
        }
        return getModelAndViewReport("reportPage", new ReportPattern(), page, size);
    }

    @RequestMapping(value = "/listReport")
    public ModelAndView listReport(
            @ModelAttribute ReportPattern reportPattern,
            @RequestParam("campusBlock") String campusBlock,
            @RequestParam("numberRoom") Long numberRoom,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        boolean isNotLogged = noLogged();
        if(isNotLogged){
            return new ModelAndView("index");
        }
        reportPattern.setCampusBlock(campusBlock);
        reportPattern.setRoomNumber(numberRoom);
        return getModelAndViewReport("reportPage", reportPattern, page, size);
    }

    @RequestMapping(value = "/searchReport")
    public ModelAndView searchReport(@ModelAttribute ReportPattern reportPattern,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size){
        boolean isNotLogged = noLogged();
        if(isNotLogged){
            return new ModelAndView("index");
        }
        return getModelAndViewReport("reportPage", reportPattern, page, size);
    }

    @RequestMapping(value = "/downloadReport/{id}")
    @ResponseBody
    public String downloadReport(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        FileInfo downFile = reportService.getById(id).getFileTemplate();
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setContentLength((int) downFile.getFileData().getContent().length);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + CyrilicToAsciiConvertUtil.transliterate(downFile.getName()) + "\"");

        FileCopyUtils.copy(downFile.getFileData().getContent(), response.getOutputStream());
//        IOUtils.copy(new FileInputStream(new File(String.valueOf(downFile))), response.getOutputStream());
        return "redirect:/getReportPage";
    }

    private ModelAndView getModelAndView(String view, HardwarePattern hardwarePattern, Optional<Integer> page, Optional<Integer> size){
        ModelAndView modelAndView = new ModelAndView(view);

        int currentPage = page.orElse(PaginationConstant.INITIAL_PAGE);
        int pageSize = size.orElse(PaginationConstant.INITIAL_PAGE_SIZE);
//        int evalPageSize = stockPattern.getPageSize() == null ? PaginationConstant.INITIAL_PAGE_SIZE : stockPattern.getPageSize();
//        int evalPage = (stockPattern.getPage() == null || stockPattern.getPage() < 1) ? PaginationConstant.INITIAL_PAGE : stockPattern.getPage()-1;

        Specification<Hardware> specification = new HardwareSpecification(hardwarePattern);
        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.Direction.DESC, "createdDate");

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
        modelAndView.addObject("pageSizes", PaginationConstant.PAGE_SIZE);
        modelAndView.addObject("hardwares", hardwares);
        modelAndView.addObject("pattern", hardwarePattern);
        return modelAndView;
    }

    private ModelAndView getModelAndViewReport(String reportPage, ReportPattern reportPattern, Optional<Integer> page, Optional<Integer> size) {

        ModelAndView modelAndView = new ModelAndView(reportPage);

        int currentPage = page.orElse(PaginationConstant.INITIAL_PAGE);
        int pageSize = size.orElse(PaginationConstant.INITIAL_PAGE_SIZE);
//        int evalPageSize = stockPattern.getPageSize() == null ? PaginationConstant.INITIAL_PAGE_SIZE : stockPattern.getPageSize();
//        int evalPage = (stockPattern.getPage() == null || stockPattern.getPage() < 1) ? PaginationConstant.INITIAL_PAGE : stockPattern.getPage()-1;

        Specification<Report> specification = new ReportSpecification(reportPattern);
        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.Direction.DESC, "createdDate");

        Page<Report> reports = reportService.findAll(specification, pageable);

        int totalPages = reports.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }

        Pager pager = new Pager(reports.getTotalPages(),reports.getNumber(),BUTTONS_TO_SHOW);

        modelAndView.addObject("selectedPageNumber", pageSize);
        modelAndView.addObject("pager", pager);
        modelAndView.addObject("pageSizes", PaginationConstant.PAGE_SIZE);
        modelAndView.addObject("reports", reports);
        modelAndView.addObject("pattern", reportPattern);
        return modelAndView;
    }

    private boolean noLogged(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return true;
        }
        return false;
    }
}
