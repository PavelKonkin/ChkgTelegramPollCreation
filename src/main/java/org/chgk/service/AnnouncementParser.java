package org.chgk.service;

import org.chgk.model.GameDto;

import java.util.List;

public interface AnnouncementParser {
    List<GameDto> parseGames(String text);
}
