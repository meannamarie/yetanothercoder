package ds

import ru.yetanothercoder.financisto.demo.DataSourceController;
import groovy.json.JsonBuilder;


log.info "datasource fetch"

// get data
def data = new DataSourceController().fetch(params);

// return as json
def json = new JsonBuilder()
json data

log.info "returning as json"
response.contentType = 'application/json'
sout << json.toString()