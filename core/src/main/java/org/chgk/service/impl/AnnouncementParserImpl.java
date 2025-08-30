package org.chgk.service.impl;

import org.chgk.model.GameDto;
import org.chgk.service.AnnouncementParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AnnouncementParserImpl implements AnnouncementParser {

    private static final Map<Pattern, String> PLACES = Map.ofEntries(
            Map.entry(Pattern.compile("We\\s*Cidreria", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), "Сидрерия"),
            Map.entry(Pattern.compile("Молод(е|ё)жь", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), "Молодежь"),
            Map.entry(Pattern.compile("Guns?\\s*&\\s*Bears", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), "Пушки Мишки"),
            Map.entry(Pattern.compile("Золотая\\s+Вобла", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), "Золотая Вобла"),
            Map.entry(Pattern.compile("Beer\\s+Happens", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), "Beer Happens Jr"),
            Map.entry(Pattern.compile("Ландрин", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE), "Ландрин")
    );

    private static final String MONTHS =
            "(?:января|февраля|марта|апреля|мая|июня|июля|августа|сентября|октября|ноября|декабря)";

    // дата может быть "24 августа", "24-го августа", "воскресенье, 24 августа"
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "(?:(?:в|во)\\s+\\S+[, ]+)?(\\d{1,2})(?:-го)?\\s+(" + MONTHS + ")",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    // время: 12:30, 12-30, 12.30, 13-00: и т.п.
    private static final Pattern GAME_PATTERN = Pattern.compile(
            "\\b(\\d{1,2}[:\\-.]\\d{2})\\b\\s*:?[\\s—–-]*\\s*([^\\n]+)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    @Override
    public List<GameDto> parseGames(String input) {
        String text = normalize(input);
        List<GameDto> games = new ArrayList<>();

        // 1. дата — берём только первую найденную
        Matcher dateMatcher = DATE_PATTERN.matcher(text);
        String currentDate = "Неизвестная дата";
        if (dateMatcher.find()) {
            String day = dateMatcher.group(1);
            String month = dateMatcher.group(2).toLowerCase(Locale.ROOT);
            currentDate = day + " " + month;
        }

        // 2. место — ищем по словарю по всему тексту
        String currentPlace = detectPlace(text);
        if (currentPlace == null) {
            currentPlace = "Неизвестное место";
        }

        // 3. все турниры по времени
        Matcher gameMatcher = GAME_PATTERN.matcher(text);
        while (gameMatcher.find()) {
            String time = normalizeTime(gameMatcher.group(1));
            String rawTail = gameMatcher.group(2);

            String name = cleanName(rawTail);
            if (!name.isEmpty()) {
                GameDto dto = new GameDto();
                dto.setSelected(true);
                dto.setDate(currentDate);
                dto.setTime(time);
                dto.setPlace(currentPlace);
                dto.setName(name);
                games.add(dto);
            }
        }

        return games;
    }

    private String detectPlace(String block) {
        for (var entry : PLACES.entrySet()) {
            if (entry.getKey().matcher(block).find()) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s
                .replace('\u00A0', ' ')
                .replace('\u2014', '—')
                .replace('\u2013', '-')
                .replace('\u2212', '-')
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ");
    }

    private String normalizeTime(String time) {
        return time.replace('-', ':').replace('.', ':');
    }

    private String cleanName(String raw) {
        String name = raw.trim();

        // обрезаем по "стоимость", "http", "3х"
        name = name.replaceAll("(стоимость.*|https?://\\S*|3[хx]\\S*)", "");

        // ищем только первые скобки и их содержимое
        Matcher m = Pattern.compile("\\(([^)]*)\\)").matcher(name);
        if (m.find()) {
            // оставляем только часть до первых скобок + сами скобки
            name = name.substring(0, m.end());
        }

        // чистим лишние пробелы/знаки препинания в конце
        name = name.replaceAll("[\\s.,:;!/?]+$", "").trim();

        if (name.length() <= 1) return "";
        return name;
    }

}
