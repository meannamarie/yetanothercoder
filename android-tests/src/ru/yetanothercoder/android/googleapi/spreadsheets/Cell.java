package ru.yetanothercoder.android.googleapi.spreadsheets;

import com.google.api.client.util.Key;

public class Cell {
	
	
	@Key("@row")
	private int row;
	
	@Key("@col")
	private int col;
	
	@Key("@inputValue")
	private String inputValue;

	@Key("text()")
	private String value;

	
	public Cell() {
		super();
	}

	public Cell(int row, int col, String inputValue) {
		this.row = row;
		this.col = col;
		this.inputValue = inputValue;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public String getInputValue() {
		return inputValue;
	}

	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
