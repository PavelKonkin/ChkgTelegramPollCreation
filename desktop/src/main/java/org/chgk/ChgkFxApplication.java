package org.chgk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ChgkFxApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent root;

    // 1. Spring запускается до JavaFX
    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(ChgkFxApplication.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        // 2. Говорим FXMLLoader'у брать контроллеры из контекста Spring
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
    }

    // 3. Запускаем UI
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ЧГК Голосовалки");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 4. Закрываем контекст Spring при выходе из приложения
    @Override
    public void stop() {
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}