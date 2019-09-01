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

public class DestinationConnectionClass {

	private String destination_jdbcUrl = null;

	private String destinationHost;

	private String destinationPort;

	private String destinationUsername;

	private String destinationPassword;

	private String destinationDb;

	DateTimeFormatter formatter;

	public DestinationConnectionClass(){
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//logToConsole("\n Fetching Database Config!!");
		try (Stream<String> stream = Files.lines(Paths.get("db-config.txt"))) {
			List<String> db_files = FXCollections.observableArrayList();
			stream.forEach(db_files::add);
			if (!db_files.isEmpty()) {
				//FilePath.xsdDir = db_files.get(0);
				destinationHost = db_files.get(1);
				destinationPort = db_files.get(2);
				destinationUsername = db_files.get(3);
				destinationPassword = db_files.get(4);
				destinationDb = db_files.get(5);
				destination_jdbcUrl = "jdbc:mysql://" +this.destinationHost + ":" + this.destinationPort + "/" + destinationDb +
						"?useServerPrepStmts=false&rewriteBatchedStatements=true";
			}
			//logToConsole("\n Database Config Fetched!!");
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, e.getMessage(), ButtonType.OK);
			alert.showAndWait();
			e.printStackTrace();
		}
	}

	public String getDestination_jdbcUrl() {
		return destination_jdbcUrl;
	}

	public String getDestinationHost() {
		return destinationHost;
	}

	public String getDestinationPort() {
		return destinationPort;
	}

	public String getDestinationUsername() {
		return destinationUsername;
	}

	public String getDestinationPassword() {
		return destinationPassword;
	}

	public String getDestinationDb() {
		return destinationDb;
	}

	public DateTimeFormatter getFormatter() {
		return formatter;
	}
}
