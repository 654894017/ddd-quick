package com.damon.demo.common.http;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpUtils {
    private static OkHttpClient httpClient;

    static {
        ConnectionPool connectionPool = new ConnectionPool(256, 30, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(512);
        //请求同一域名同时存在的最大请求数量
        dispatcher.setMaxRequestsPerHost(512);
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .connectionPool(connectionPool)
                .retryOnConnectionFailure(false)
                .dispatcher(dispatcher)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory(), x509TrustManager())
                .build();
    }

    private static String buildUrlParams(Map<String, Object> params) {
        if (CollUtil.isEmpty(params)) {
            return StrUtil.EMPTY;
        }
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (encodedParams.length() > 0) {
                    encodedParams.append("&");
                } else {
                    encodedParams.append("?");
                }
                String value = ObjectUtil.isNull(entry.getValue()) ? "" : ObjectUtil.toString(entry.getValue());
                String encodedKey = URLEncoder.encode(entry.getKey(), "UTF-8");
                String encodedValue = URLEncoder.encode(value, "UTF-8");
                encodedParams.append(encodedKey).append("=").append(encodedValue);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return encodedParams.toString();
    }

    private static Headers buildHeaderParams(Map<String, Object> headerParams) {
        Headers.Builder headersBuilder = new Headers.Builder();
        if (CollUtil.isNotEmpty(headerParams)) {
            for (String key : headerParams.keySet()) {
                if (headerParams.get(key) != null) {
                    headersBuilder.add(key, ObjectUtil.toString(headerParams.get(key)));
                }
            }
        }
        return headersBuilder.build();
    }

    private static X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    private static SSLSocketFactory sslSocketFactory() {
        try {
            // 信任任何链接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse doPost(String url, String jsonBody, Map<String, Object> headerParams) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, jsonBody))
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .headers(buildHeaderParams(headerParams))
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return new HttpResponse(response.code(), new String(response.body().bytes()));
        }
    }

    public static HttpResponse doPut(String url, String jsonBody, Map<String, Object> headerParams) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(mediaType, jsonBody))
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .headers(buildHeaderParams(headerParams))
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return new HttpResponse(response.code(), new String(response.body().bytes()));
        }
    }

    public static HttpResponse doGet(String url, Map<String, Object> urlParams, Map<String, Object> headerParams) throws IOException {
        Request request = new Request.Builder()
                .url(url + "?" + buildUrlParams(urlParams))
                .get()
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .headers(buildHeaderParams(headerParams))
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return new HttpResponse(response.code(), new String(response.body().bytes()));
        }
    }

    public HttpResponse doDelete(String url, Map<String, Object> urlParams, Map<String, Object> headerParams) throws IOException {
        Request request = new Request.Builder()
                .url(url + "?" + buildUrlParams(urlParams))
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .headers(buildHeaderParams(headerParams))
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            return new HttpResponse(response.code(), new String(response.body().bytes()));
        }
    }

}
