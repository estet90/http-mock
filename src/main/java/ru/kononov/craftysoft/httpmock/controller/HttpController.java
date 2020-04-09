package ru.kononov.craftysoft.httpmock.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.RequiredArgsConstructor;
import ru.kononov.craftysoft.httpmock.dto.ExchangeData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class HttpController implements HttpHandler {

    private final List<ExchangeData> exchangeData;

    @Override
    public void handle(HttpExchange httpExchange) {
        exchangeData.forEach(data -> {
            var method = data.getMethod().toUpperCase();
            if (httpExchange.getRequestMethod().equals(method)) {
                try {
                    Thread.sleep(data.getTimeout());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                data.getHeaders().forEach((key, value) -> httpExchange.getResponseHeaders().add(key, value));
                ofNullable(data.getResponse())
                        .ifPresentOrElse(response -> {
                            try {
                                httpExchange.sendResponseHeaders(data.getStatus(), response.length);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            try (var outputStream = httpExchange.getResponseBody();
                                 var bodyStream = new ByteArrayOutputStream()) {
                                outputStream.write(response);
                                bodyStream.write(response);
                                httpExchange.setStreams(httpExchange.getRequestBody(), bodyStream);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }, () -> {
                            try {
                                httpExchange.sendResponseHeaders(data.getStatus(), -1);
                                httpExchange.getResponseBody().close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        });
    }

}
