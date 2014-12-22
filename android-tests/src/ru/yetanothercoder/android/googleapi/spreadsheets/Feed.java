package ru.yetanothercoder.android.googleapi.spreadsheets;

import com.google.api.client.util.Key;

import java.util.LinkedList;
import java.util.List;

public class Feed {
	
	private static final String CELLS_REL_MARK = "#cellsfeed";
	private static final String POST_REL_MARK = "#post";
	private static final String BATCH_REL_MARK = "#batch";
	
	
	@Key
	private String id;
	
	@Key("@gd:etag")
	private String etag;
	
	@Key
	private String title;
	
	@Key("entry")
	private List<Entry> entries;
	
	@Key("link")
	private List<Link> links;

	@Key("gs:rowCount")
	private Integer rowCount;

	@Key("gs:colCount")
	private Integer colCount;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
	public void addEntry(Entry e) {
		if (entries == null) {
			entries = new LinkedList<Entry>();
		}
		entries.add(e);
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public Link findCellsFeedUrl() {
		for (Link link : links) {
			if (link.getRel().endsWith(CELLS_REL_MARK)) {
				return link;
			}
		}
		return null;
	}

	public Link findPostUrl() {
		for (Link link : links) {
			if (link.getRel().contains(POST_REL_MARK)) {
				return link;
			}
		}
		return null;
	}
	
	public Link findBatchUrl() {
		for (Link link : links) {
			if (link.getRel().contains(BATCH_REL_MARK)) {
				return link;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("Feed [id=");
		builder.append(id);
		builder.append(", etag=");
		builder.append(etag);
		builder.append(", title=");
		builder.append(title);
		builder.append(", entries=");
		builder.append(entries != null ? entries.subList(0,
				Math.min(entries.size(), maxLen)) : null);
		builder.append("]");
		return builder.toString();
	}

	public Integer getRowCount() {
		return rowCount;
	}

	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}

	public Integer getColCount() {
		return colCount;
	}

	public void setColCount(Integer colCount) {
		this.colCount = colCount;
	}
}
