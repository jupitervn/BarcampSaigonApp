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
package org.barcampsaigon.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * @author Jupiter (vu.cao.duy@gmail.com)
 * 
 */
public class ExtendedHorizontalScrollView extends HorizontalScrollView {
    private OnScrollStoppedListener onScrollStoppedListener;
    private Runnable scrollerTask;
    private int initialPosition;

    private final int newCheck = 100;

    /**
     * Default constructor.
     * 
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ExtendedHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addScrollerTask();
    }

    /**
     * Default constructor.
     * 
     * @param context
     * @param attrs
     */
    public ExtendedHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        addScrollerTask();
    }

    /**
     * Default constructor.
     * 
     * @param context
     */
    public ExtendedHorizontalScrollView(Context context) {
        super(context);
        addScrollerTask();
    }

    /**
     * Start the scrollerTaks.
     */
    public void startScrollerTask() {
        initialPosition = getScrollX();
        this.postDelayed(scrollerTask, newCheck);
    }

    private void addScrollerTask() {
        scrollerTask = new Runnable() {

            @Override
            public void run() {

                int newPosition = getScrollX();
                if (initialPosition - newPosition == 0) {//has stopped

                    if (getOnScrollStoppedListener() != null) {

                        getOnScrollStoppedListener().onScrollStopped();
                    }
                } else {
                    initialPosition = getScrollX();
                    ExtendedHorizontalScrollView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    /**
     * @return the onScrollStoppedListener
     */
    public OnScrollStoppedListener getOnScrollStoppedListener() {
        return onScrollStoppedListener;
    }

    /**
     * @param onScrollStoppedListener
     *            the onScrollStoppedListener to set
     */
    public void setOnScrollStoppedListener(OnScrollStoppedListener onScrollStoppedListener) {
        this.onScrollStoppedListener = onScrollStoppedListener;
    }

    /**
     * Interface to listen for scroll ends.
     * 
     * @author Jupiter (vu.cao.duy@gmail.com)
     * 
     */
    public interface OnScrollStoppedListener {
        /**
         * On scroll ends.
         */
        void onScrollStopped();
    }

}
