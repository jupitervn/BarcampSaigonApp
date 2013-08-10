/**
 * Copyright (C) 2013 BARCAMP SG. All rights reserved.
 * 
 * 
 * BARCAMP SG MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. BARCAMP SG SHALL NOT BE LIABLE FOR ANY
 * LOSSES OR DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 */
package org.barcampsaigon.android.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.barcampsaigon.android.model.TwitterBearerToken;
import org.barcampsaigon.android.util.Config;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.xml.XmlNamespaceDictionary;

/**
 * Http utils and constants.
 * 
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public final class HttpUtils {
    /**
     * Constant for Authorization Header.
     */
    public static final String AUTHORIZATION_HTTP_HEADER = "Authorization";
    /**
     * Constant for GData-Version Header.
     */
    public static final String GDATA_VERSION_HTTP_HEADER = "GData-Version";
    /**
     * Constant for Content-Length Header.
     */
    public static final String CONTENT_LENGTH_HTTP_HEADER = "Content-Length";
    /**
     * Constant for Content-type Header.
     */
    public static final String CONTENT_TYPE_HTTP_HEADER = "Content-Type";
    /**
     * Constant for XmlNamespace Dictionary.
     */
    public static final XmlNamespaceDictionary SPREADSHEET_NAMESPACE = new XmlNamespaceDictionary()
            .set("", "http://www.w3.org/2005/Atom")
            .set("gd", "http://schemas.google.com/g/2005")
            .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/");

    private HttpUtils() {

    }

    public static final int HTTP_TIMEOUT = 60000;
    public static final int HTTP_READ_TIMEOUT = 60000;

    /**
     * Add Google Header for http request.
     * 
     * @param httpRequest
     */
    public static void addGoogleHeader(HttpRequest httpRequest) {
        //        HttpHeaders headers = httpRequest.getHeaders();
        HttpHeaders headers = new HttpHeaders();
        // headers.setAuthorization("GoogleLogin auth=" + authToken);
        // headers.setUserAgent("docker android junit test (user-agent)");
        headers.set(GDATA_VERSION_HTTP_HEADER, "3.0");
        httpRequest.setHeaders(headers);
    }

    public static HttpResponse postToGoogleForm(String googleFormUrl, Map paramMap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        UrlEncodedContent httpContent = new UrlEncodedContent(paramMap);
        HttpRequest httpRequest = httpTransport.createRequestFactory().buildPostRequest(new GenericUrl(googleFormUrl),
                httpContent);
        HttpUtils.addGoogleHeader(httpRequest);
        HttpResponse httpResponse = httpRequest.execute();
        return httpResponse;
    }

    /**
     * Get twitter page.
     * 
     * @param url
     * @return
     * @throws IOException
     */
    public static HttpResponse getTwitterPage(String url, Map<String, String> args) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        String convertUrl = url;
        if (args != null) {
            int count = 0;
            for (String key : args.keySet()) {
                String value = args.get(key);
                if (value != null) {
                    if (count == 0) {
                        convertUrl += "?" + URLEncoder.encode(key, "utf-8") + "="
                                + URLEncoder.encode(value, "utf-8");
                    } else {
                        convertUrl += "&" + URLEncoder.encode(key, "utf-8") + "="
                                + URLEncoder.encode(value, "utf-8");
                    }
                    count++;
                }
            }
        }
        HttpRequest httpRequest = httpTransport.createRequestFactory().buildGetRequest(new GenericUrl(convertUrl));
        String bearerToken = getBearerToken();
        HttpHeaders httpHeaders = httpRequest.getHeaders();
        Log.d("D.Vu", "BEARER TOKEN " + bearerToken);
        if (httpHeaders == null) {
            httpHeaders = new HttpHeaders();
        }
        if (bearerToken != null) {
            httpHeaders.setAuthorization("Bearer " + bearerToken);
        } else {
            throw new IOException("Can't connect to Twitter");
        }
        httpRequest.setRetryOnExecuteIOException(true);
        httpRequest.setNumberOfRetries(5);
        JsonObjectParser jsonObjectParser = new JsonObjectParser(new GsonFactory());
        httpRequest.setParser(jsonObjectParser);
        HttpResponse httpResponse = httpRequest.execute();
        return httpResponse;
    }

    public static String getBearerToken() throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        String convertUrl = Config.TWITTER_OAUTH_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "client_credentials");
        UrlEncodedContent httpContent = new UrlEncodedContent(params);
        HttpRequest httpRequest = httpTransport.createRequestFactory().buildPostRequest(new GenericUrl(convertUrl),
                httpContent);
        HttpHeaders httpHeaders = httpRequest.getHeaders();
        if (httpHeaders == null) {
            httpHeaders = new HttpHeaders();
        }
        httpHeaders.setAuthorization("Basic " + Config.TWITTER_AUTH_KEY);
        httpRequest.setRetryOnExecuteIOException(true);
        httpRequest.setNumberOfRetries(5);
        JsonObjectParser jsonObjectParser = new JsonObjectParser(new GsonFactory());
        httpRequest.setParser(jsonObjectParser);
        HttpResponse httpResponse = httpRequest.execute();
        TwitterBearerToken tokenInfo = httpResponse.parseAs(TwitterBearerToken.class);
        String accessToken = null;
        if (tokenInfo != null && tokenInfo.getTokenType() != null
                && tokenInfo.getTokenType().toLowerCase().equals("bearer")) {
            accessToken = tokenInfo.getAccessToken();
        }
        return accessToken;
    }
}
