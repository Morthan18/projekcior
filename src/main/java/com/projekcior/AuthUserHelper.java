package com.projekcior;

import com.projekcior.model.Authority;
import com.projekcior.model.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class AuthUserHelper {

    public String getAuthenticatedUsername() {
        var auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
