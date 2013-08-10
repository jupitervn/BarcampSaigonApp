package org.barcampsaigon.android.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.api.client.util.Key;

public class Entry {

    private final static String REL_EDIT = "edit";
    private final static String REL_SELF = "self";
    private static final String CELL_REL_MARK = "#cellsfeed";

    @Key private String id;

    @Key private String title;

    @Key("@gd:etag") private String etag;

    @Key("content") private Content content;

    @Key("updated") private Date updatedDate;

    @Key("gs:rowCount") private Integer rowCount;

    @Key("gs:colCount") private Integer colCount;

    @Key("link") private List<Link> links;

    @Key("gs:cell") private Cell cell;

    @Key("batch:id") private String batchId;

    @Key("batch:operation") private BatchOperation batchOp;

    public Entry() {

    }

    public Entry(String title, int rowCount, int colCount) {
        this.title = title;
        this.rowCount = rowCount;
        this.colCount = colCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content workSheetUrl) {
        this.content = workSheetUrl;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public Integer getColCount() {
        return colCount;
    }

    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void addLink(String rel, String type, String href) {
        if (links == null) {
            links = new LinkedList<Link>();
        }
        links.add(new Link(rel, type, href));
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public BatchOperation getBatchOp() {
        return batchOp;
    }

    public void setBatchOp(BatchOperation batchOp) {
        this.batchOp = batchOp;
    }

    public Link findEditLink() {
        for (Link link : links) {
            if (REL_EDIT.equals(link.getRel())) {
                return link;
            }
        }
        return null;
    }

    public Link findSelfLink() {
        for (Link link : links) {
            if (REL_SELF.equals(link.getRel())) {
                return link;
            }
        }
        return null;
    }

    public Link findCellFeedUrl() {
        for (Link link : links) {
            if (link.getRel().contains(CELL_REL_MARK)) {
                return link;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        StringBuilder builder = new StringBuilder();
        builder.append("Entry [id=");
        builder.append(id);
        builder.append(", title=");
        builder.append(title);
        builder.append(", etag=");
        builder.append(etag);
        builder.append(", content=");
        builder.append(content);
        builder.append(", rowCount=");
        builder.append(rowCount);
        builder.append(", colCount=");
        builder.append(colCount);
        builder.append(", links=");
        builder.append(links != null ? links.subList(0,
                Math.min(links.size(), maxLen)) : null);
        builder.append("]");
        return builder.toString();
    }

    /**
     * @return the updatedDate
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate
     *            the updatedDate to set
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

}
