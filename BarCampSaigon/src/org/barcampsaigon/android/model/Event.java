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
package org.barcampsaigon.android.model;

import java.util.Date;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class Event {
    private String name;
    private Date time;
    private int colNum;

    public Event(String name, int colNum) {
        setName(name);
        setColNum(colNum);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time
     *            the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return the colNum
     */
    public int getColNum() {
        return colNum;
    }

    /**
     * @param colNum
     *            the colNum to set
     */
    public void setColNum(int colNum) {
        this.colNum = colNum;
    }
}
