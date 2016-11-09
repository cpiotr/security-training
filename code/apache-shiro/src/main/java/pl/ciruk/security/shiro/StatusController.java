package pl.ciruk.security.shiro;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/standard/hello")
    public String getStandard() {
        return "Meh, straightforward.";
    }

    @GetMapping("/secured/hello")
    public String getSecured() {
        return "This is a secret!";
    }
}
