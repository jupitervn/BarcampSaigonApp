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

import com.google.api.client.http.HttpResponse;

/**
 * Interface for Google Doc Service.
 * 
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public interface GoogleDocService {
    /**
     * Method post to Google Form at <b>googleFormUrl</b>.
     * 
     * @param googleFormUrl
     * @throws IOException
     */
    HttpResponse postToGoogleForm(String googleFormUrl, Map<String, String> paramMap) throws IOException;

    /**
     * Method to retrieve a google doc by feed list.
     * 
     * @param googleDocsUrl
     * @return
     * @throws IOException
     */
    Feed getGoogleDocsByList(String googleDocsUrl) throws IOException;

    /**
     * Method to retrieve a google doc by cell.
     * 
     * @param googleDocsUrl
     * @param minRow
     * @param maxRow
     * @return
     */
    Feed getGoogleDocsByCells(String googleDocsUrl, int minRow, int maxRow, boolean isEmptyCellReturned);

}
