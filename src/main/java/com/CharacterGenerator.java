package com;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class CharacterGenerator {
    private static CharacterGenerator characterGenerator;

    private static final String BASE_URL = "//randomall.ru/custom/gen/1538/";

    private String token;
    private int retries = 0;

    private CharacterGenerator() {
    }

    public static CharacterGenerator getInstance() {
        return characterGenerator == null ? new CharacterGenerator() : characterGenerator;
    }

    public String generate() throws Exception {
        if (token == null) {
            token = extractToken();
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(createPostRequest())) {

            int responseStatusCode = response.getStatusLine().getStatusCode();
            if (responseStatusCode != 200) {
                throw new Exception("Request was unsuccessful. Status code " + responseStatusCode);
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String responseStr = EntityUtils.toString(entity);

                if (responseStr.contains("<!DOCTYPE html>") && retries < 5) {
                    retries++;
                    token = extractToken();
                    return generate();
                }

                return responseStr;
            }
        }

        throw new Exception("Unexpected exception");
    }

    private HttpPost createPostRequest() throws Exception {
        HttpPost request = new HttpPost(new URI("https", BASE_URL, null));

        request.addHeader("x-csrftoken", token);
        request.addHeader("x-requested-with", "XMLHttpRequest");
        request.addHeader("referer", "https://randomall.ru/custom/gen/1538/");
        request.addHeader("cookie", "csrftoken=" + token);

        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("csrftoken", token));
        request.setEntity(new UrlEncodedFormEntity(params));

        return request;
    }

    private String extractToken() throws Exception {
        HttpGet request = new HttpGet(new URI("https", BASE_URL, null));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {

            int responseStatusCode = response.getStatusLine().getStatusCode();
            if (responseStatusCode != 200) {
                throw new Exception("Request was unsuccessful. Status code " + responseStatusCode);
            }

            Header[] headers = response.getHeaders("set-cookie");

            for (Header header : headers) {
                String contains = "csrftoken=";
                if (header.getValue().contains(contains)) {
                    return StringUtils.substringBetween(header.getValue(), contains, ";");
                }
            }
        }

        throw new Exception("Unexpected exception");
    }
}
