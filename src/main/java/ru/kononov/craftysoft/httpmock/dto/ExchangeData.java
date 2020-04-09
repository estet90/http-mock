package ru.kononov.craftysoft.httpmock.dto;

import lombok.Value;

import java.util.Map;

@Value
public class ExchangeData {
    String method;
    String uri;
    byte[] response;
    int status;
    Map<String, String> headers;
    int timeout;
}
