package in.natchapol.deliveryfoodapi.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenFacadeImpl implements AuthenFacade{
    @Override
    public Authentication getAuthen() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
