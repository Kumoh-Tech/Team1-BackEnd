package com.club_board.club_board_server.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordWebhookService {

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    private final WebClient webClient;

    // WebClient Bean 정의가 필요합니다 (application 클래스나 config 클래스에 추가)

    public void sendReservationNotification(String userName, String bookTitle) {
        Map<String, Object> message = new HashMap<>();
        message.put("content", "📚 **도서 예약 알림** 📚");

        Map<String, Object> embed = new HashMap<>();
        embed.put("title", "새로운 도서 예약이 등록되었습니다");
        embed.put("description", String.format("사용자 **%s**님이 도서 **%s**를 예약하였습니다.", userName, bookTitle));
        embed.put("color", 5814783); // 푸른색

        Map<String, Object>[] embeds = new Map[]{embed};
        message.put("embeds", embeds);

        sendDiscordMessage(message)
                .subscribe(
                        response -> log.info("Discord webhook notification sent successfully"),
                        error -> log.error("Failed to send Discord webhook notification: {}", error.getMessage())
                );
    }

    private Mono<String> sendDiscordMessage(Map<String, Object> message) {
        return webClient.post()
                .uri(webhookUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(message)
                .retrieve()
                .bodyToMono(String.class);
    }
}