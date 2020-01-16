package thesisproject.diploma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;

@Controller
public class LoginController {

    @RequestMapping(value={"/"}, method = RequestMethod.GET)
    public String homePage(){
        return "login";
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginPage(@RequestParam(value = "error",required = false) String error,
                                  @RequestParam(value = "logout",	required = false) String logout) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Почта или параоль неверны");
        }

        if (logout != null) {
            model.addObject("message", "Logged out from JournalDEV successfully.");
        }
        //System.out.println("I am Login");
        return model;
    }

}
