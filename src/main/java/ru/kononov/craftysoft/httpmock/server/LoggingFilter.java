package ru.kononov.craftysoft.httpmock.server;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.CloseableThreadContext;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Log4j2
public class LoggingFilter extends Filter {

    private final String serviceName;

    @Inject
    LoggingFilter(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        try (var ignored = CloseableThreadContext.put("requestId", UUID.randomUUID().toString())) {
            if (log.isDebugEnabled()) {
                logRequest(exchange, chain);
                logResponse(exchange);
            } else {
                chain.doFilter(exchange);
            }
        }
    }

    private void logRequest(HttpExchange exchange, Chain chain) throws IOException {
        try (var inputStream = exchange.getRequestBody()) {
            var byteStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, byteStream);
            var body = byteStream.toString(StandardCharsets.UTF_8.name());
            exchange.setStreams(new ByteArrayInputStream(byteStream.toByteArray()), exchange.getResponseBody());
            if (nonNull(body) && body.length() > 0) {
                logIn(exchange, body);
            } else {
                logIn(exchange);
            }
            chain.doFilter(exchange);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private void logIn(HttpExchange exchange, String body) {
        log.debug(
                "{}.request\n\tmethod={}\n\turi={}\n\theaders={}\n\tbody={}",
                exchange.getRequestMethod() + serviceName,
                exchange.getRequestMethod(),
                exchange.getRequestURI(),
                exchange.getRequestHeaders(),
                body
        );
    }

    private void logIn(HttpExchange exchange) {
        log.debug(
                "{}.request\n\tmethod={}\n\turi={}\n\theaders={}",
                exchange.getRequestMethod() + serviceName,
                exchange.getRequestMethod(),
                exchange.getRequestURI(),
                exchange.getRequestHeaders()
        );
    }

    private void logResponse(HttpExchange exchange) throws IOException {
        try (var bodyStream = exchange.getResponseBody()) {
            if (bodyStream instanceof ByteArrayOutputStream && ((ByteArrayOutputStream) bodyStream).size() > 0) {
                var body = ((ByteArrayOutputStream) bodyStream).toString(StandardCharsets.UTF_8.name());
                logOut(exchange, body);
            } else {
                logOut(exchange);
            }
        }
    }

    private void logOut(HttpExchange exchange, String body) {
        log.debug(
                "{}.response\n\tstatus={}\n\theaders={}\n\tbody={}",
                exchange.getRequestMethod() + serviceName,
                exchange.getResponseCode(),
                exchange.getResponseHeaders(),
                body
        );
    }

    private void logOut(HttpExchange exchange) {
        log.debug(
                "{}.response\n\tstatus={}\n\theaders={}",
                exchange.getRequestMethod() + serviceName,
                exchange.getResponseCode(),
                exchange.getResponseHeaders()
        );
    }

    @Override
    public String description() {
        return serviceName;
    }

}
