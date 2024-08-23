package smllregistry;

import blazing.BlazingLog;
import blazing.BlazingResponse;
import blazing.Get;
import blazing.Initializer;
import blazing.Post;
import blazing.Route;
import blazing.Static;
import blazing.WebServer;
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

@WebServer("8080")
@Static("/styles")
@Static("/images")
public class RegistryServer {

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
				var result_page = new Div()
					.addChildren(new ResultsHeader(term))
					.className("flex flex-col p-5 w-full");

				while (rs.next()) {
					String name = rs.getString("package_name");
					String author = rs.getString("author");
					String authorurl = rs.getString("authorgiturl");
					String url = rs.getString("repourl");
					String description = rs.getString("description");
					result_page.addChild(new PackageGroup(name, author, authorurl, url, description));
				}

				response.sendUiRespose(result_page);
				return;
			} catch (SQLException ex) {
				BlazingLog.severe(ex.getMessage());
			}
		}

		System.out.println("" + conn);
		response.sendResponse("Showing results for: " + term);
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

		if (term == null)  { // We did not find the pkg
			Map obj = new HashMap();
			obj.put("status", "err"); 
			response.sendResponse(map2Json(obj));
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
				String name = rs.getString("package_name");
				String author = rs.getString("author");
				String url = rs.getString("repourl");
				String description = rs.getString("description");

				if (name == null)  { // We did not find the pkg
					Map obj = new HashMap();
					obj.put("status", "err"); 
					response.sendResponse(map2Json(obj));
					return;
				}

				Map obj = new HashMap();
				obj.put("pkgname", name);
				obj.put("pkgauthor", author);
				obj.put("pkgurl", url);
				obj.put("pkgdesc", description); 
				obj.put("status", "ok"); 

				response.sendResponse(map2Json(obj));

			} catch (SQLException ex) {
				BlazingLog.severe(ex.getMessage());
			}
		} else {
				Map obj = new HashMap();
				obj.put("status", "err"); 
				response.sendResponse(map2Json(obj));
		}
	}

	private static String map2Json(Map<String, String> map) {
		var entries = map.entrySet();

		StringBuilder sb = new StringBuilder(); 
		sb.append("{".indent(0));
		for (var it = entries.iterator(); it.hasNext();) {
			var entry = it.next();
			sb.append(
				String.format("\"%s\":\"%s\"", entry.getKey(), entry.getValue())
					.concat(it.hasNext() ? "," : "").indent(2)
			);

		}
		sb.append("}");
		return sb.toString(); 
	}
}
