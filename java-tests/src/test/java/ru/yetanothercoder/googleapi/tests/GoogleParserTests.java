package ru.yetanothercoder.googleapi.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.google.api.client.http.xml.atom.AtomContent;

public class GoogleParserTests {
	
	@Test
	public void testContentValue() throws IOException {
		Entry entry = new Entry();
		entry.setContent(new Content("content value"));
		entry.getContent().setType("type");
		
		AtomContent requestContent = AtomContent.forEntry(SpreadsheetTests.CELL_NAMESPACE, entry);
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		requestContent.writeTo(bao);
		
		System.out.println(bao.toString());
	}
	
	@Test
	public void testEmptyContent() throws IOException {
		Entry entry = new Entry();
		entry.setContent(new Content());
		
		AtomContent requestContent = AtomContent.forEntry(SpreadsheetTests.CELL_NAMESPACE, entry);
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		requestContent.writeTo(bao);
		System.out.println(bao.toString());
	}
}
