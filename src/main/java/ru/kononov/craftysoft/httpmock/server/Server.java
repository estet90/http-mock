package ru.kononov.craftysoft.httpmock.server;

import com.sun.net.httpserver.HttpServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.kononov.craftysoft.httpmock.controller.HttpController;
import ru.kononov.craftysoft.httpmock.dto.ExchangeData;
import ru.kononov.craftysoft.httpmock.util.ConfigurationResolver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class Server {

    private final int port;
    private final ConfigurationResolver configurationResolver;

    public void start() throws IOException {
        var start = LocalDateTime.now();
        var inetSocketAddress = new InetSocketAddress(port);
        var server = HttpServer.create(inetSocketAddress, 0);
        var executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);
        addHandlers(server);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Сервер остановлен...");
            server.stop(0);
        }));
        server.start();
        log.info("Сервер запущен за {}мс и слушает порт {}", start.until(LocalDateTime.now(), ChronoUnit.MILLIS), port);
    }

    private void addHandlers(HttpServer server) {
        configurationResolver.getExchangeData().stream()
                .collect(Collectors.groupingBy(ExchangeData::getUri))
                .forEach((uri, exchangeData) -> {
                    var context = server.createContext(uri, new HttpController(exchangeData));
                    context.getFilters().add(new LoggingFilter(uri.replace("/", ".")));
                    exchangeData.forEach(data -> log.info("Добавлен мок {}{}", data.getMethod(), data.getUri()));
                });
    }

}
