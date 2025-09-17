package org.chgk.controller;

import org.chgk.model.GameDto;
import org.chgk.model.GameWrapper;
import org.chgk.service.AnnouncementParser;
import org.chgk.service.TelegramService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

@Controller
public class PollCreationController {
    private final AnnouncementParser parser;
    private final TelegramService telegramService;

    public PollCreationController(AnnouncementParser announcementParser,
                                  TelegramService telegramService) {
        this.parser = announcementParser;
        this.telegramService = telegramService;

    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("rawText", "");
        model.addAttribute("gameWrapper", new GameWrapper());
        return "index";
    }

    @PostMapping("/parse")
    public String parse(@RequestParam String rawText, Model model,
                        @ModelAttribute("gameWrapper") GameWrapper wrapper) {
        // теперь парсер возвращает List<GameDto>
        List<GameDto> gameDtos = parser.parseGames(rawText);

        wrapper.setGames(gameDtos);

        model.addAttribute("gameWrapper", wrapper);
        return "select";
    }

    @PostMapping("/parseMore")
    public String parseMore(@RequestParam String rawText,
                            @ModelAttribute("gameWrapper") GameWrapper wrapper) {
        List<GameDto> gameDtos = parser.parseGames(rawText);
        wrapper.getGames().addAll(gameDtos); // добавляем к существующим
        return "select";
    }

    @PostMapping("/sendPoll")
    public String sendPoll(@ModelAttribute("games") GameWrapper wrapper) {
        // берем только выбранные турниры
        List<String> selectedGames = wrapper.getGames().stream()
                .filter(GameDto::isSelected)
                .map(dto -> String.format("%s %s, %s — %s",
                        dto.getDate(), dto.getTime(), dto.getPlace(), dto.getName()))
                .toList();

        if (!selectedGames.isEmpty()) {
            telegramService.sendPoll("Следующие игры", selectedGames);
        }
        return "done";
    }
}
