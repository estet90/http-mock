package ru.kononov.craftysoft.httpmock;

import lombok.extern.log4j.Log4j2;
import ru.kononov.craftysoft.httpmock.module.ConfigurationModule;
import ru.kononov.craftysoft.httpmock.module.ServerModule;

@Log4j2
public class Application {

    public static void main(String[] args) {
        try {
            var server = DaggerApplicationComponent.builder()
                    .configurationModule(new ConfigurationModule(args))
                    .serverModule(new ServerModule(args))
                    .build()
                    .server();
            server.start();
        } catch (Exception e) {
            log.error("Application.main.thrown", e);
        }
    }

}
