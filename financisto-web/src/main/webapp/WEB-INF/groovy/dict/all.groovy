import ru.yetanothercoder.financisto.demo.DictController;
import groovy.json.JsonBuilder;


log.info "retreiving all dictionary data"

// get data
def dictController = new DictController()
def data = dictController.getAll(params)

// return as json
def json = new JsonBuilder()
json data

log.info "returning a json"
response.contentType = 'application/json'
sout << json.toString()