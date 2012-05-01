
import my.gaelyk.test2.GroovyTest;

log.info "Setting attribute datetime"

def gt = new GroovyTest()

def words = ['ant', 'buffalo', 'cat', 'dinosaur', '21']

log.info "before: " + words
log.info "filtered: " + gt.filter(words)

request.setAttribute 'datetime', new Date().toString()
request.setAttribute 'filter', gt

log.info "Forwarding to the template"

forward '/WEB-INF/pages/datetime.gsp'