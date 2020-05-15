package thesisproject.diploma.controller;

import org.apache.catalina.User;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import thesisproject.diploma.entity.Stock;
import thesisproject.diploma.entity.UserDiploma;
import thesisproject.diploma.pagination.Pager;
import thesisproject.diploma.pagination.PaginationConstant;
import thesisproject.diploma.service.UserDiplomaService;
import thesisproject.diploma.specification.StockSpecification;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static thesisproject.diploma.pagination.PaginationConstant.BUTTONS_TO_SHOW;

@Controller
@Transactional
public class RegistrationController {

    @Autowired
    private UserDiplomaService userService;

    @RequestMapping(value="/regUser", method = RequestMethod.GET)
    public ModelAndView registrationUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        ModelAndView modelAndView = new ModelAndView();
        UserDiploma user = new UserDiploma();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("regUser");
        return modelAndView;
    }
    @RequestMapping(value = "/regUser", method = RequestMethod.POST)
    public ModelAndView saveNewUser(@Valid UserDiploma user, BindingResult bindingResult, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        UserDiploma userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "*There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("regUser");
        } else {
            userService.saveUser(user, "MANAGER");
        }
        return modelAndView;
    }

    @RequestMapping("/deleteUser/{id}")
    public ModelAndView showHotel(@PathVariable("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        userService.deleteUser(id);
        return new ModelAndView("redirect:/getAllUsers");
    }

    @RequestMapping(value = "/listUser", method = RequestMethod.GET)
    public ModelAndView listUser(@RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        return getModelAndView("allUsers", page, size);
    }

    @RequestMapping(value="/getAllUsers", method = RequestMethod.GET)
    public ModelAndView getAllUsers(@RequestParam("page") Optional<Integer> page,
                                    @RequestParam("size") Optional<Integer> size){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() == null || auth.getPrincipal().equals("anonymousUser")){
            return new ModelAndView("index");
        }
        return getModelAndView("allUsers", page, size);
    }

    private ModelAndView getModelAndView(String view, Optional<Integer> page, Optional<Integer> size){
        ModelAndView modelAndView = new ModelAndView(view);

        int currentPage = page.orElse(PaginationConstant.INITIAL_PAGE);
        int pageSize = size.orElse(PaginationConstant.INITIAL_PAGE_SIZE);
//        int evalPageSize = stockPattern.getPageSize() == null ? PaginationConstant.INITIAL_PAGE_SIZE : stockPattern.getPageSize();
//        int evalPage = (stockPattern.getPage() == null || stockPattern.getPage() < 1) ? PaginationConstant.INITIAL_PAGE : stockPattern.getPage()-1;

        Pageable pageable = PageRequest.of(currentPage-1, pageSize, Sort.Direction.DESC, "name");

        Page<UserDiploma> userDiplomas = userService.findAllUsers(pageable);

        int totalPages = userDiplomas.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }

        Pager pager = new Pager(userDiplomas.getTotalPages(),userDiplomas.getNumber(),BUTTONS_TO_SHOW);

        modelAndView.addObject("selectedPageNumber", pageSize);
        modelAndView.addObject("users", userDiplomas);
        modelAndView.addObject("pager", pager);
        modelAndView.addObject("pageSizes", PaginationConstant.PAGE_SIZE);
        return modelAndView;
    }
}
