package my.gaelyk.test2;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Gaelyk_test2Servlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world77");
	}
}
