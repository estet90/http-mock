package ru.kononov.craftysoft.httpmock.util;

import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;
import ru.kononov.craftysoft.httpmock.dto.ExchangeData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.*;
import static java.util.Optional.ofNullable;

@Log4j2
public class ConfigurationResolver {

    private final List<ExchangeData> exchangeData = new ArrayList<>();

    public ConfigurationResolver(String folder) {
        var files = new File(folder).listFiles();
        if (isNull(files) || files.length == 0) {
            throw new RuntimeException("Директория " + folder + " не содержит файлов");
        }
        for (var file : files) {
            if (file.getName().endsWith(".yaml") || file.getName().endsWith(".yml")) {
                log.info("Чтение данных из файла {}", file);
                try (var inputStream = new FileInputStream(file)) {
                    Map<String, Object> map = new Yaml().load(inputStream);
                    var method = (String) map.get("method");
                    var uri = (String) map.get("uri");
                    int status = (int) map.get("status");
                    int timeout = (int) map.get("timeout");
                    var headers = ofNullable((Map<String, String>) map.get("headers")).orElse(Map.of());
                    byte[] response = ofNullable((Map<String, String>) map.get("response"))
                            .map(responseMap -> {
                                if (nonNull(responseMap.get("file"))) {
                                    var responseEntryValue = responseMap.get("file");
                                    if (isNull(responseEntryValue) || responseEntryValue.length() == 0) {
                                        throw new RuntimeException("Некорректное значение в имени файла");
                                    }
                                    responseEntryValue = responseEntryValue.startsWith("/")
                                            ? responseEntryValue
                                            : "/" + responseEntryValue;
                                    var filePath = folder + responseEntryValue;
                                    try {
                                        return Files.readAllBytes(Paths.get(filePath));
                                    } catch (IOException e) {
                                        throw new RuntimeException("Ошибка при чтении файла " + filePath + ", содержащего ответ", e);
                                    }
                                } else {
                                    return ofNullable(responseMap.get("text")).map(String::getBytes).orElse(null);
                                }
                            }).orElse(new byte[]{});
                    exchangeData.add(new ExchangeData(
                            requireNonNull(method, "Параметр 'method' (HTTP-метод) должен быть заполнен"),
                            requireNonNull(uri, "Параметр 'uri' (URI сервиса) должен быть заполнен"),
                            response,
                            status,
                            headers,
                            timeout
                    ));
                } catch (IOException e) {
                    throw new RuntimeException("Ошибка при чтении файла " + file, e);
                }
            }
        }
    }

    public List<ExchangeData> getExchangeData() {
        return this.exchangeData;
    }

}
