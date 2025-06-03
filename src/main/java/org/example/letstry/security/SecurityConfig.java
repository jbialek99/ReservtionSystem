package org.example.letstry.security;

import jakarta.servlet.http.HttpServletRequest;
import org.example.letstry.errorHandler.CustomAuthenticationFailureHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomOidcUserService customOidcUserService;

    public SecurityConfig(CustomAuthenticationFailureHandler customAuthenticationFailureHandler, CustomOidcUserService customOidcUserService) {
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/login", "/home", "/css/**", "/js/**", "/logout", "/welcome",
                                "/images/**", "/localization", "/current-localization",
                                "/other-localization", "/building/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestResolver(
                                        customAuthorizationRequestResolver(clientRegistrationRepository)
                                )
                        )
                        .failureHandler(customAuthenticationFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService))
                        .defaultSuccessUrl("/home", false)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                );

        return http.build();
    }

    private OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest originalRequest = defaultResolver.resolve(request);
                return customize(originalRequest);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest originalRequest = defaultResolver.resolve(request, clientRegistrationId);
                return customize(originalRequest);
            }

            private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest originalRequest) {
                if (originalRequest == null) return null;

                Map<String, Object> additionalParams = new HashMap<>(originalRequest.getAdditionalParameters());
                additionalParams.put("prompt", "select_account");

                return OAuth2AuthorizationRequest.from(originalRequest)
                        .additionalParameters(additionalParams)
                        .build();
            }
        };
    }
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://graph.microsoft.com/v1.0")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();
    }
}

