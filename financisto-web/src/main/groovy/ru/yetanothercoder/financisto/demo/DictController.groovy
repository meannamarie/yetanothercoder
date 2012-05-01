package ru.yetanothercoder.financisto.demo

import groovy.util.logging.Log;

@Log
class DictController {
	List categories = ['Food', 'Comp', 'Auto', 'Holiday']
	List accounts = ['RUB', 'USD']
	
	def getAll(params) {
		log.info "getting all dictionaries, params: $params" 
		
		def result = ['accounts':accounts, 'categories':categories]
		def totals = [:]
		for (def acc : accounts) {
			totals[acc] = calcTotal(acc)
		}
		result['totals'] = totals
		return result
	}
	
	def calcTotal(acc) {
		return 100
	}
}
