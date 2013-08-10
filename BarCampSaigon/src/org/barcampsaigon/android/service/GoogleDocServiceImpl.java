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
import java.util.Map;

import org.barcampsaigon.android.model.Feed;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.xml.XmlObjectParser;

/**
 * Google doc service implementation.
 * 
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class GoogleDocServiceImpl implements GoogleDocService {

    private void addGoogleHeaders(HttpRequest httpRequest) {
        //        HttpHeaders headers = httpRequest.getHeaders();
        HttpHeaders headers = new HttpHeaders();
        // headers.setAuthorization("GoogleLogin auth=" + authToken);
        // headers.setUserAgent("docker android junit test (user-agent)");
        headers.set(HttpUtils.GDATA_VERSION_HTTP_HEADER, "3.0");
        httpRequest.setHeaders(headers);
    }

    @Override
    public HttpResponse postToGoogleForm(String googleFormUrl, Map<String, String> paramMap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        UrlEncodedContent httpContent = new UrlEncodedContent(paramMap);
        HttpRequest httpRequest = httpTransport.createRequestFactory().buildPostRequest(new GenericUrl(googleFormUrl),
                httpContent);
        HttpUtils.addGoogleHeader(httpRequest);
        HttpResponse httpResponse = httpRequest.execute();
        return httpResponse;
    }

    @Override
    public Feed getGoogleDocsByList(String googleDocsUrl) {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        GenericUrl sUrl = new GenericUrl(googleDocsUrl);

        HttpRequest httpRequest;
        HttpResponse httpResponse;
        Feed googleFeed = null;
        try {
            httpRequest = httpTransport.createRequestFactory().buildGetRequest(
                    sUrl);
            httpRequest.setConnectTimeout(HttpUtils.HTTP_TIMEOUT); // 3 minutes connect timeout
            httpRequest.setReadTimeout(HttpUtils.HTTP_READ_TIMEOUT);

            addGoogleHeaders(httpRequest);

            httpRequest.setParser(new XmlObjectParser(HttpUtils.SPREADSHEET_NAMESPACE));
            httpResponse = httpRequest.execute();
            if (httpResponse.getStatusCode() == 200) {
                googleFeed = httpResponse.parseAs(Feed.class);
            }
            httpResponse.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            googleFeed = null;
        }
        return googleFeed;
    }

    @Override
    public Feed getGoogleDocsByCells(String googleDocsUrl, int minRow, int maxRow, boolean isEmptyCellReturned) {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        if (isEmptyCellReturned) {
            googleDocsUrl += "?return-empty=true";
        }
        GenericUrl sUrl = new GenericUrl(googleDocsUrl);

        HttpRequest httpRequest;
        HttpResponse httpResponse;
        Feed googleFeed = null;
        try {
            httpRequest = httpTransport.createRequestFactory().buildGetRequest(
                    sUrl);
            httpRequest.setConnectTimeout(HttpUtils.HTTP_TIMEOUT); // 3 minutes connect timeout
            httpRequest.setReadTimeout(HttpUtils.HTTP_READ_TIMEOUT);
            httpRequest.setRetryOnExecuteIOException(true);
            httpRequest.setNumberOfRetries(5);
            addGoogleHeaders(httpRequest);

            httpRequest.setParser(new XmlObjectParser(HttpUtils.SPREADSHEET_NAMESPACE));
            httpResponse = httpRequest.execute();
            if (httpResponse.getStatusCode() == 200) {
                googleFeed = httpResponse.parseAs(Feed.class);
            }
            Log.d("D.Vu", "GET " + googleFeed.getTotalResults());
            httpResponse.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            googleFeed = null;
        }
        return googleFeed;
    }
}
