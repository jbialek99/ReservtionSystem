package org.example.letstry.security;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Map;

/*
Dodane: pobranie danych z openid connect i w security wyswietlenie w logach przy probie logowania


Dodac:
Przypisanie roli,
Zapis danych do bazy,
Przekształcenie claimów

*/
@Service
public class CustomOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser user = super.loadUser(userRequest);


        Map<String, Object> userInfoClaims = user.getClaims();
        System.out.println("User Info Claims: " + userInfoClaims);


        userInfoClaims.forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });

        return user;
    }
}
