package org.phw.hbaser.ui;

import org.phw.core.lang.Strings;

public class QueryOption {
    private boolean showTimeStamp;
    private boolean showLineNo;
    private boolean keyOnlyFilter;
    private boolean randomRowFilter;
    private int rowLimit = Integer.MAX_VALUE;

    public boolean isShowTimeStamp() {
        return showTimeStamp;
    }

    public void setShowTimeStamp(boolean showTimeStamp) {
        this.showTimeStamp = showTimeStamp;
    }

    public boolean isShowLineNo() {
        return showLineNo;
    }

    public void setShowLineNo(boolean showLineNo) {
        this.showLineNo = showLineNo;
    }

    public boolean isKeyOnlyFilter() {
        return keyOnlyFilter;
    }

    public void setKeyOnlyFilter(boolean keyOnlyFilter) {
        this.keyOnlyFilter = keyOnlyFilter;
    }

    public int getRowLimit() {
        return rowLimit;
    }

    public void setRowLimit(String strRowLimit) {
        int limit = Strings.isEmpty(strRowLimit) ? 0 : Integer.valueOf(strRowLimit);
        if (limit <= 0) {
            limit = Integer.MAX_VALUE;
        }

        rowLimit = limit;
    }

    public boolean isRandomRowFilter() {
        return randomRowFilter;
    }

    public void setRandomRowFilter(boolean randomRowFilter) {
        this.randomRowFilter = randomRowFilter;
    }

}
