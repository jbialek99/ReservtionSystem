package org.example.letstry.controller;

import org.example.letstry.model.GraphEventsResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
public class LoginController {

    private static final String GRAPH_API_BASE_URL = "https://graph.microsoft.com/v1.0";

/*
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }
 */

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User principal,
                       @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient authorizedClient,
                       Model model) {
        addUserAttributesToModel(principal, model);
        if (authorizedClient != null) {
            model.addAttribute("accessToken", authorizedClient.getAccessToken().getTokenValue());
        }
        return "/menu/home";
    }


    @GetMapping("/events")
    public String getEvents(Model model,
                            @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient authorizedClient,
                            @AuthenticationPrincipal OAuth2User principal) {

        addUserAttributesToModel(principal, model);

        if (authorizedClient == null) {
            model.addAttribute("error", "Nie jesteś zalogowany lub brak klienta autoryzacyjnego.");
            return "events";
        }

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        WebClient client = WebClient.builder()
                .baseUrl(GRAPH_API_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();

        try {
            GraphEventsResponse response = client.get()
                    .uri("/me/events")
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                model.addAttribute("error", "Błąd pobierania wydarzeń: " + errorBody);
                                return Mono.error(new RuntimeException("HTTP Error: " + clientResponse.statusCode() + ", " + errorBody));
                            })
                    )
                    .bodyToMono(GraphEventsResponse.class)
                    .block();

            if (response != null && response.getValue() != null && !response.getValue().isEmpty()) {
                model.addAttribute("subjects", response.getValue());
            } else {
                model.addAttribute("error", "Brak wydarzeń do wyświetlenia.");
            }

        } catch (Exception e) {
            model.addAttribute("error", "Wyjątek przy pobieraniu wydarzeń: " + e.getMessage());
        }

        return "events";
    }

    @PostMapping("/createEvent")
    public String createEvent(@RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient authorizedClient,
                              Model model) {
        if (authorizedClient == null) {
            model.addAttribute("eventStatus", "Brak autoryzacji.");
            return "eventCreationResult";
        }

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        WebClient client = WebClient.builder()
                .baseUrl("https://graph.microsoft.com/v1.0")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Map<String, Object> event = Map.of(
                "subject", "My event123",
                "start", Map.of(
                        "dateTime", "2025-05-06T15:12:59",
                        "timeZone", "UTC"
                ),
                "end", Map.of(
                        "dateTime", "2025-05-13T15:12:59",
                        "timeZone", "UTC"
                )
        );

        try {
            client.post()
                    .uri("/me/events")
                    .bodyValue(event)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            model.addAttribute("eventStatus", "Wydarzenie zostało utworzone pomyślnie!");
        } catch (WebClientResponseException e) {
            model.addAttribute("eventStatus", "Błąd podczas tworzenia wydarzenia: " + e.getResponseBodyAsString());
        }

        return "eventCreationResult";
    }




    private void addUserAttributesToModel(OAuth2User principal, Model model) {
        if (principal != null) {
            model.addAttribute("username", principal.getAttribute("name"));
            model.addAttribute("email", principal.getAttribute("email"));
        }
    }
}
