package org.chgk.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.chgk.model.GameDto;
import org.chgk.service.AnnouncementParser;
import org.chgk.service.TelegramService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component // Делаем контроллер Spring-бином для инъекции сервисов
public class FxController {

    // Инъектируем наши существующие сервисы
    private final AnnouncementParser parser;
    private final TelegramService telegramService;

    // Связываем элементы из FXML с полями в классе
    @FXML private TextArea parseTextArea;
    @FXML private TextArea moreParseTextArea;
    @FXML private VBox gamesContainer;

    public FxController(AnnouncementParser parser, TelegramService telegramService) {
        this.parser = parser;
        this.telegramService = telegramService;
    }

    // Метод для кнопки "Распарсить и отобразить"
    @FXML
    private void parseAndDisplay() {
        List<GameDto> games = parser.parseGames(parseTextArea.getText());
        gamesContainer.getChildren().clear(); // Очищаем старый список
        games.forEach(this::addGameRow);
        parseTextArea.clear();
    }

    // Метод для кнопки "Распарсить и добавить"
    @FXML
    private void addMoreAnnouncements() {
        List<GameDto> games = parser.parseGames(parseTextArea.getText());
        games.forEach(this::addGameRow);
        parseTextArea.clear();
    }

    // Метод для кнопки "+ Добавить пустой турнир"
    @FXML
    private void addEmptyGame() {
        addGameRow(new GameDto()); // Добавляем пустой объект
    }

    // Метод для кнопки "Сформировать и отправить"
    @FXML
    private void sendPoll() {
        List<String> selectedGames = gamesContainer.getChildren().stream()
                .map(HBox.class::cast) // Каждая строка - это HBox
                .filter(row -> ((CheckBox) row.getChildren().get(0)).isSelected()) // Проверяем CheckBox
                .map(this::formatGameFromRow)
                .toList();

        if (!selectedGames.isEmpty()) {
            telegramService.sendPoll("Следующие игры", selectedGames);
            // Показываем сообщение об успехе
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Голосование отправлено! ✅");
            alert.setHeaderText("Готово");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не выбрано ни одного турнира.");
            alert.setHeaderText("Внимание");
            alert.showAndWait();
        }
    }

    // Вспомогательный метод для создания одной строки с игрой
    private void addGameRow(GameDto game) {
        HBox row = new HBox(5); // HBox для горизонтального расположения элементов

        CheckBox selected = new CheckBox();
        selected.setSelected(game.isSelected());

        TextField date = new TextField(game.getDate());
        date.setPromptText("Дата");
        date.setPrefWidth(100);

        TextField time = new TextField(game.getTime());
        time.setPromptText("Время");
        time.setPrefWidth(60);

        TextField place = new TextField(game.getPlace());
        place.setPromptText("Место");
        place.setPrefWidth(180);

        TextField name = new TextField(game.getName());
        name.setPromptText("Название");
        name.setPrefWidth(350);
        HBox.setHgrow(name, javafx.scene.layout.Priority.ALWAYS); // Растягиваем поле с названием

        Button up = new Button("↑");
        up.setOnAction(e -> moveUp(row));

        Button down = new Button("↓");
        down.setOnAction(e -> moveDown(row));

        Button remove = new Button("✕");
        remove.setOnAction(e -> gamesContainer.getChildren().remove(row));

        row.getChildren().addAll(selected, date, time, place, name, up, down, remove);
        gamesContainer.getChildren().add(row);
    }

    private void moveUp(Node row) {
        int index = gamesContainer.getChildren().indexOf(row);
        if (index > 0) {
            gamesContainer.getChildren().remove(index);
            gamesContainer.getChildren().add(index - 1, row);
        }
    }

    private void moveDown(Node row) {
        int index = gamesContainer.getChildren().indexOf(row);
        if (index < gamesContainer.getChildren().size() - 1) {
            gamesContainer.getChildren().remove(index);
            gamesContainer.getChildren().add(index + 1, row);
        }
    }

    // Собирает строку для опроса из данных в полях строки
    private String formatGameFromRow(HBox row) {
        String date = ((TextField) row.getChildren().get(1)).getText();
        String time = ((TextField) row.getChildren().get(2)).getText();
        String place = ((TextField) row.getChildren().get(3)).getText();
        String name = ((TextField) row.getChildren().get(4)).getText();
        return String.format("%s %s, %s — %s", date, time, place, name);
    }
}