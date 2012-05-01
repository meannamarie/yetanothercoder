package ru.yetanothercoder.financisto.demo

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ru.yetanothercoder.financisto.demo.dto.Transaction;
import groovy.util.logging.Log;

@Log
class DataSourceController {

	int idCounter = 0
	Map<String, List<Transaction>> data = [:]
	DictController dict = new DictController()

	DataSourceController() {
		data.put("RUB", generateStubRubTransactions(dict.getAccounts().get(0)));
		data.put("USD", generateStubUsdTransactions(dict.getAccounts().get(1)));
	}

	def Map fetch(Map params) {
		log.info "fetch, params: $params"

		def ds = params._dataSource?:"RUB"

		def total = getTotalRows(ds)

		def sortBy = params._sortBy?:"default"
		int start = Integer.valueOf(params._startRow?:0)
		int end = Integer.valueOf(params._endRow?:total)

		if (end > total) {
			end = total
		}

		log.info "ds: $ds, total: $total, sort: $sortBy, start: $start, end: $end"
		
		List<Transaction> transactions = getData(ds, start, end - 1, sortBy)
		
		def result = ['response'
			:['status':0, 'startRow':start, 'endRow':end, 'totalRows': total, 'data':transactions]]
		return result
	}
	
	def getData(id, start, end, sortBy) {
		return data.get(id)[start..end]
	}
	
	def getTotalRows(id) {
		if (!data.containsKey(id)) {
			data.put(id, new LinkedList<Transaction>());
			dict.accounts.add(id);
		}
		return data.get(id).size();
	}


	private List<Transaction> generateStubRubTransactions(String acc) {
		long now = new Date().getTime();
		int day = 24 * 60 * 60 * 1000;
		
		List<Transaction> result = [
			new Transaction(id:idCounter++, account:acc, amount:177, category:"f", 
					comment: "dinner", date: new Date(now - day).getTime(), project:"moscow", receiver:"Auchan", 
					status: "v"),
			new Transaction(id:idCounter++, account:acc, amount:578, category:"c",
					comment: "europe charger", date: new Date(now - 2 * day).getTime(), project:"mac", receiver:"nix",
					status: "w")
		]

		return result
	}
	
	private List<Transaction> generateStubUsdTransactions(String acc) {
		long now = new Date().getTime();
		int day = 24 * 60 * 60 * 1000;
		
		List<Transaction> result = [
			new Transaction(id:idCounter++, account:acc, amount:100, category:"f",
					comment: "breakfast", date: new Date(now - day).getTime(), project:"work", receiver:"MDon",
					status: "v"),
		]

		return result
	}
}
