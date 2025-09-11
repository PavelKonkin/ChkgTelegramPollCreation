module org.chgk.chgkfx {
    // Correct module name for spring-core
    requires spring.core;

    // Correct `opens` for spring to be able to use reflection
    opens org.chgk.controller to javafx.fxml, spring.core, spring.beans;
    opens org.chgk.desktopconfig to javafx.fxml, spring.core, spring.beans, spring.context;
    opens org.chgk to spring.core;

    // Other required modules
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.web;
    requires com.fasterxml.jackson.databind;

    requires spring.beans;

    // You'll also need to explicitly require your core module
    requires org.chgk.core;

    exports org.chgk;
}