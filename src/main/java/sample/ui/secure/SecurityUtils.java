package sample.ui.secure;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import java.util.Collection;

/**
 * Created by igor.mukhin on 05.10.2015.
 */
public class SecurityUtils {

    public static boolean isImpersonating() {
        return (getImpersonator() != null);
    }

    public static UserDetails getImpersonator() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            return null;
        }

        Authentication authWorkFor = securityContext.getAuthentication();
        if (authWorkFor == null) {
            return null;
        }

        Authentication authActual = getSourceAuthentication(authWorkFor);
        if (authActual == null) {
            return null;
        }

        return (UserDetails) authActual.getPrincipal();
    }

    private static Authentication getSourceAuthentication(Authentication current) {
        Authentication original = null;

        // iterate over granted authorities and find the 'switch user' authority
        Collection<? extends GrantedAuthority> authorities = current.getAuthorities();

        for (GrantedAuthority auth : authorities) {
            // check for switch user type of authority
            if (auth instanceof SwitchUserGrantedAuthority) {
                original = ((SwitchUserGrantedAuthority) auth).getSource();
            }
        }

        return original;
    }

}
