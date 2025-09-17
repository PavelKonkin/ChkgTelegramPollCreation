package org.chgk.service;

import org.chgk.config.BotConfig;
import org.chgk.controller.FxController;
import org.chgk.service.impl.TelegramServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

@Service
@Primary
public class DesktopTelegramServiceImpl extends TelegramServiceImpl {
    private final Preferences prefs = Preferences.userNodeForPackage(FxController.class);
    private final RestTemplate restTemplate;

    public DesktopTelegramServiceImpl(BotConfig botConfig, RestTemplate restTemplate) {
        super(botConfig);
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendPoll(String question, List<String> options) {
        String botToken = prefs.get("botToken", "");
        String chatId = prefs.get("chatId", "");

        sendPoll(botToken, chatId, question, options); // вызов нового перегруженного метода
    }

    protected void sendPoll(String botToken, String chatId, String question, List<String> options) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendPoll";

        // Добавляем вариант "Думаю / не играю"
        List<String> pollOptions = new ArrayList<>(options);
        pollOptions.add("Думаю / не играю");

        // Создаем HashMap вместо Map.of()
        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("question", question);
        body.put("options", pollOptions);
        body.put("is_anonymous", false);
        body.put("allows_multiple_answers", true);

        restTemplate.postForObject(url, body, String.class);
    }
}
