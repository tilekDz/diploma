package thesisproject.diploma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import thesisproject.diploma.entity.UserDiploma;
import thesisproject.diploma.service.UserDiplomaService;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

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
        System.out.println(123);
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
}
