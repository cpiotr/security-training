package pl.ciruk.security.spring;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/standard/hello")
    @PreAuthorize("hasRole('MANAGER')")
    public String getStandard() {
        return "Meh, straightforward.";
    }

    @GetMapping("/secured/hello")
//    @PreAuthorize("hasRole('manager')")
//    @Secured("ROLE_MANAGER")
    public String getSecured() {

        LdapUserDetails user = (LdapUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return "This is a secret!";
    }
}
