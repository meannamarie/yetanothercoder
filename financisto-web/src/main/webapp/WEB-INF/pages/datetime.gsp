<% include '/WEB-INF/includes/header.gsp' %>
<% import my.gaelyk.test2.GroovyTest %>
<h1>Date / time</h1>

<p>
    <%
        log.info "outputing the datetime attribute"
    %>
    The current date and time: <%= request.getAttribute('datetime') %>
</p>

filtered <%=new GroovyTest().filter(['ant', 'buffalo', 'cat', 'dinosaur']) %><br/>
filtered <%=request.getAttribute('filter').filter(['123', 'ant', 'my', '12345', 'dinosaur']) %><br/>


<% include '/WEB-INF/includes/footer.gsp' %>

