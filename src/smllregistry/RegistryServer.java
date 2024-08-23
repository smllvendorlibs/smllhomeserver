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
				String name = rs.getString("package_name");
				String author = rs.getString("author");
				String url = rs.getString("repourl");
				String description = rs.getString("description");

				String json
					= """
  {
		"pkgname"  : "%s", 
  	"pkgauthor" : "%s", 
  	"pkgurl" : "%s", 
  	"pkgdesc" : "%s",
    "status": "ok"
  }
    """;

				response.sendResponse(String.format(json, name, author, url, description));
			} catch (SQLException ex) {
				BlazingLog.severe(ex.getMessage());
			}
		} else {
			String json
				= """
	{
    "status": "err"
	}
        """.trim();
			response.sendResponse(json);
		}
	}
}
