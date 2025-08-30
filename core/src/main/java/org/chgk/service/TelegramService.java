package org.chgk.service;

import java.util.List;

public interface TelegramService {
    void sendPoll(String question, List<String> options);
}
