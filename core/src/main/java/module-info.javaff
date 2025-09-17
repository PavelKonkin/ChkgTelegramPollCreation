module org.chgk.core {
    requires spring.core;

//    opens org.chgk.service to spring.core;
//    opens org.chgk.model to spring.core;
//    opens org.chgk.config to spring.core;

    opens org.chgk.service to spring.beans;
    opens org.chgk.model to spring.beans;
    opens org.chgk.config to spring.beans, spring.core;
    opens org.chgk.service.impl to spring.beans;

    exports org.chgk.service;
    exports org.chgk.model;
    exports org.chgk.config;
    exports org.chgk.service.impl;

    requires spring.context;
    requires spring.boot;
    requires spring.web;
    requires spring.boot.autoconfigure;
    requires spring.beans;

    requires static lombok;

}