package ru.yetanothercoder.android.googleapi.spreadsheets;

import com.google.api.client.util.Key;

public class Link {
	@Key("@rel")
	private String rel;
	@Key("@type")
	private String type;
	@Key("@href")
	private String href;
	
	public Link() {
		super();
	}
	public Link(String rel, String type, String href) {
		this.rel = rel;
		this.type = type;
		this.href = href;
	}
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Link [rel=");
		builder.append(rel);
		builder.append(", type=");
		builder.append(type);
		builder.append(", href=");
		builder.append(href);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
