package org.ccfng.global;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ConnectionClass {

	private String source_jdbcUrl = null;

	private String sourceHost;

	private String sourcePort;

	private String sourceUsername;

	private String sourcePassword;

	private String sourceDb;

	DateTimeFormatter formatter;

	public ConnectionClass(){
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//logToConsole("\n Fetching Database Config!!");
		try (Stream<String> stream = Files.lines(Paths.get("source-db-config.txt"))) {
			List<String> db_files = FXCollections.observableArrayList();
			stream.forEach(db_files::add);
			if (!db_files.isEmpty()) {
				//FilePath.xsdDir = db_files.get(0);
				sourceHost = db_files.get(0);
				sourcePort = db_files.get(1);
				sourceUsername = db_files.get(2);
				sourcePassword = db_files.get(3);
				sourceDb = db_files.get(4);
				source_jdbcUrl = "jdbc:mysql://" +this.sourceHost + ":" + this.sourcePort + "/" + sourceDb +
						"?useServerPrepStmts=false&useSSL=false&rewriteBatchedStatements=true";
			}
			//logToConsole("\n Database Config Fetched!!");
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, e.getMessage(), ButtonType.OK);
			alert.showAndWait();
			e.printStackTrace();
		}
	}

	public String getSource_jdbcUrl() {
		return source_jdbcUrl;
	}

	public String getSourceHost() {
		return sourceHost;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public String getSourceUsername() {
		return sourceUsername;
	}

	public String getSourcePassword() {
		return sourcePassword;
	}

	public String getSourceDb() {
		return sourceDb;
	}

	public DateTimeFormatter getFormatter() {
		return formatter;
	}
}
