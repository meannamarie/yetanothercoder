package ru.yetanothercoder.financisto.demo.dto;

import groovy.transform.ToString;

import java.util.Date;

/**
 * Information about a transaction.
 */
@ToString(includeNames=true,includeFields=true)
class Transaction {

	long id;
	String status;
	long date;
	String account;
	String receiver;
	String category;
	long amount;
	String comment;
	String project;

}