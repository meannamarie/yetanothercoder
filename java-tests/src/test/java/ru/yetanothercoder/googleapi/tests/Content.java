package ru.yetanothercoder.googleapi.tests;

import com.google.api.client.util.Key;

public class Content {
	
	@Key("@type")
	private String type;
	
	@Key("@src")
	private String src;
	
	@Key("text()")
	private String value;
	
	public Content(String value) {
		this.value = value;
	}
	
	public Content() {
		super();
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Content [type=");
		builder.append(type);
		builder.append(", src=");
		builder.append(src);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
}
