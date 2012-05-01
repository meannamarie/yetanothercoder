package ru.yetanothercoder.googleapi.tests;

import com.google.api.client.util.Key;

public class BatchOperation {
	
	public static final BatchOperation UPDATE = new BatchOperation("update");
	
	@Key("@type")
	private String type;
	
	public BatchOperation(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName() + " [type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
