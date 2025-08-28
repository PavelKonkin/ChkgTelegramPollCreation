package org.chgk.service.impl;

import org.chgk.config.BotConfig;
import org.chgk.service.TelegramService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TelegramServiceImpl implements TelegramService {
    private final BotConfig botConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public TelegramServiceImpl(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public void sendPoll(String question, List<String> options) {
        String url = "https://api.telegram.org/bot" + botConfig.getBotToken() + "/sendPoll";

        // Добавляем вариант "Думаю / не играю"
        List<String> pollOptions = new ArrayList<>(options);
        pollOptions.add("Думаю / не играю");

        restTemplate.postForObject(url, Map.of(
                "chat_id", botConfig.getChatId(),
                "question", question,
                "options", pollOptions,
                "is_anonymous", false,
                "allows_multiple_answers", true
        ), String.class);
    }
}
