package ec.gob.ambiente.suia.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DynamicImageServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5882389832191346665L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			// Get image file.
			String file = request.getParameter("file");
			System.out.println("********************------------------********************");
			System.out.println(file);
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(file));

			// Get image contents.
			byte[] bytes = new byte[in.available()];

			in.read(bytes);
			in.close();

			// Write image contents to response.
			response.getOutputStream().write(bytes);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}