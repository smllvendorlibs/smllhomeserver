package smllregistry;

import blazing.BlazingLog;
import blazing.BlazingResponse;
import blazing.Destructor;
import blazing.Get;
import blazing.Initializer;
import blazing.Post;
import blazing.Route;
import blazing.Static;
import blazing.WebServer;
import blazing.json.JSon;
import components.HomeView;
import components.PackageGroup;
import components.ResultsHeader;
import java.util.Map;
import webx.Div;
import webx.Html;
import webx.Main;
import webx.WebXElement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import webx.H1;

@WebServer("8080")
@Static("/styles")
@Static("/images")
public class RegistryServer {
	private RegistryServer() {}
	
	private static Connection conn;

	@Initializer
	public static void init() {
		String url = "jdbc:sqlite:db/packages.db";
		try {
			Connection connection = DriverManager.getConnection(url);
			conn = connection;
			BlazingLog.info("Connection to SQLite has been established.");
		} catch (SQLException ex) {
			BlazingLog.severe(ex.getMessage());
		}
	}

	@Route
	public static void home(BlazingResponse response) {
		WebXElement page = new Html()
			.addHeaderScript("https://cdn.tailwindcss.com")
			.addHeaderStyleLink("https://unpkg.com/nes.css@latest/css/nes.min.css")
			.addHeaderStyleLink("/styles/main.css")
			.title("SMLL | Packages")
			.addChildren(
				new Main()
					.addChildren(new HomeView())
					.id("page")
			);

		response.sendUiRespose(page);
	}

	@Post("/v1/search")
	public static void search(BlazingResponse response) {
		Map<String, String> params = response.params();
		String term = params.get("term");
		ResultSet rs = null;
		try {
			String sql = String.format(
				"""
SELECT * FROM Packages 
where 
package_name like '%%%s%%' 
or description like '%%%s%%'
or repourl like '%%%s%%';
""".trim(),
				term,
				term,
				term
			);
			Statement stmt = conn.createStatement();

			rs = stmt.executeQuery(sql);
		} catch (SQLException ex) {
			BlazingLog.severe(ex.getMessage());
			return;
		}

		if (term.isBlank()) {
			response.sendUiRespose(new HomeView());
			return;
		}

		if (rs != null) {
			try {
				var resultPage = new Div()
					.addChildren(new ResultsHeader(term))
					.className("flex flex-col p-5 w-full");

				int count = 0;
				while (rs.next()) {
					String name = rs.getString("package_name");
					String author = rs.getString("author");
					String authorurl = rs.getString("authorgiturl");
					String url = rs.getString("repourl");
					String description = rs.getString("description");
					resultPage.addChild(new PackageGroup(name, author, authorurl, url, description));
					count++;
				}

				if (count == 0) {
					resultPage
						.addChild(
						new Div()
							.className("p-5 my-10 w-full justify-center text-center border")
							.addChild(
								new H1("404 results not found")
							)
					);
					response.sendUiRespose(resultPage);
				} else {
					response.sendUiRespose(resultPage);
				}
			} catch (SQLException ex) {
				BlazingLog.severe(ex.getMessage());
			}
		}
	}

	@Post("/v1/reload")
	public static void reloadHome(BlazingResponse response) {
		response.sendUiRespose(
			new Main()
				.addChildren(new HomeView())
				.id("page")
		);
	}

	@Post("/v1/public/pkgman/pkginfo")
	public static void packageInfo(BlazingResponse response) {

		Map<String, String> params = response.params();
		String term = params.get("term");

		HashMap<String, String> obj = new HashMap<>();
		obj.put("pkgname", null);
		obj.put("pkgauthor", null);
		obj.put("pkgurl", null);
		obj.put("pkgdesc", null);
		String status = "status";
		if (term == null) { // We did not find the pkg
			obj.put(status, "err");
			response.sendResponse(JSon.from(obj));
			return;
		}

		ResultSet rs = null;
		try {
			String sql = String.format(
				"""
SELECT * FROM Packages 
where 
lower(package_name) = lower('%s');
""".trim(),
				term
			);
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException ex) {
			BlazingLog.severe(ex.getMessage());
			return;
		}

		if (term.isBlank()) {
			response.sendUiRespose(new HomeView());
			return;
		}

		if (rs != null) {
			try {
				String name = rs.getString("package_name");
				String author = rs.getString("author");
				String url = rs.getString("repourl");
				String description = rs.getString("description");

				String json = JSon.from(obj);
				if (name == null) { // We did not find the pkg
					obj.put(status, "pkg-err");
					response.sendResponse(json);
					return;
				}

				// Package is found. Prepare the json response
				obj.put("pkgname", name);
				obj.put("pkgauthor", author);
				obj.put("pkgurl", url);
				obj.put("pkgdesc", description);
				obj.put(status, "ok");

				json = JSon.from(obj);
				response.sendResponse(json);

			} catch (SQLException ex) {
				BlazingLog.severe(ex.getMessage());
				obj.put(status, "db-err");
				response.sendResponse(JSon.from(obj));
			}
		} else {
			obj.put(status, "sys-err");
			response.sendResponse(JSon.from(obj));
		}
	}

	@Destructor
	public static void close() {
		try {
			conn.close();
			BlazingLog.info("DB Connection dropped");
		} catch (SQLException ex) {
			BlazingLog.severe(ex.getMessage());
		}
	}
}
