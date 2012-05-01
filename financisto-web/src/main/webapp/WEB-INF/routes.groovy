
get "/", forward: "/WEB-INF/pages/index.gsp"
get "/smartclient-demo", forward: "/WEB-INF/pages/smartclient-xml.gsp"
get "/financisto-demo", forward: "/WEB-INF/pages/financisto-demo.gsp"
get "/favicon.ico", redirect: "/images/gaelyk-small-favicon.png"
get "/datetime", forward: "/datetime.groovy"
get "/demo/rest-api/dict/all.json", forward: "/dict/all.groovy"

get "/demo/rest-api/ds/@action.json", forward: "/ds/@action.groovy",
	validate: { ['fetch', 'add', 'update', 'remove'].contains(action) }
