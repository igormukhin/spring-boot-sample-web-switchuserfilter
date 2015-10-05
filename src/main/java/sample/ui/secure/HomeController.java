package sample.ui.secure;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by igor.mukhin on 05.10.2015.
 */
@Controller
public class HomeController {

    @RequestMapping("/")
    public String home(Map<String, Object> model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        model.put("title", "Hello, " + user.getUsername());
        model.put("message", "Authorities: " + auth.getAuthorities());
        model.put("isImpersonating", SecurityUtils.isImpersonating());
        model.put("getImpersonator", SecurityUtils.getImpersonator());
        return "home";
    }

    @RequestMapping("/foo")
    public String foo() {
        throw new RuntimeException("Expected exception in controller");
    }
}
