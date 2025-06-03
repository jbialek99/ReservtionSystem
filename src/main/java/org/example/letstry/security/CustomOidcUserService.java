package org.example.letstry.security;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        // Pobierz oryginalne claimy użytkownika
        Map<String, Object> claims = new HashMap<>(oidcUser.getClaims());

        // Dodaj access token do claimów
        claims.put("access_token", userRequest.getAccessToken().getTokenValue());

        // Zwróć nowego użytkownika z dodanym tokenem
        return new DefaultOidcUser(
                oidcUser.getAuthorities(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "email" // lub "preferred_username", jeśli chcesz używać innego atrybutu jako principal
        ) {
            @Override
            public Map<String, Object> getClaims() {
                return claims;
            }

            @Override
            public String getName() {
                return oidcUser.getEmail(); // lub coś innego, zależnie od preferencji
            }
        };
    }
}
