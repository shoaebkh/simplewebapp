package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Sample servlet class for login related methods
 */
public class LoginServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		request.getRequestDispatcher("link.html").include(request, response);

		String userid = request.getParameter("userid");
		String password = request.getParameter("password");

		if (password.equals("admin")) {
			out.print("Welcome, " + userid);
			HttpSession session = request.getSession();
			session.setAttribute("name", userid);
		} else {
			out.print("Sorry, username or password error!");
			request.getRequestDispatcher("login.html").include(request,
					response);
		}
		out.close();
	}

}
