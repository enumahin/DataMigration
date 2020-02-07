package org.ccfng.nmrs.query;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.ccfng.global.DBMiddleMan;
import org.ccfng.global.DestinationConnectionClass;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class Controller {

    @FXML
    private TextArea query;

    DestinationConnectionClass dd = new DestinationConnectionClass();

    public void initialize(){

    }

    @FXML
    private void executeQuery(){
        Integer loc = 0;
        if(DBMiddleMan.presentLocation == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR, "PLEASE SELECT LOCATION AND TRY AGAIN...", ButtonType.OK);
            alert.showAndWait();
            return;
        }else{
            loc = DBMiddleMan.presentLocation;
        }
        // DELETE DUPLICATE FORM ENTRIES WITH NULL VISIT_ID

        // VOID DUPLICATE FORM ENTRY WHERE 1 HAS NO VISITID
        String sql = query.getText();

        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception exc) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error Registering DB Driver " + exc.getMessage() + "..", ButtonType.OK);
            alert.showAndWait();
        }
        try (Connection conn = DriverManager.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword())) {

            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                //execute batch
                ResultSet rs = stmt.executeQuery(sql);
                ResultSetMetaData rsmd = rs.getMetaData();
                Integer colCount = rsmd.getColumnCount();
                try{

                    Workbook workbook = new HSSFWorkbook();
                    Sheet spreadsheet = workbook.createSheet("Export");

                    Row row = spreadsheet.createRow(0);

                    for (int i = 1; i <= rsmd.getColumnCount(); i++){

                        row.createCell(i - 1).setCellValue(rsmd.getColumnLabel(i));
                    }

                    int cc = 1;
                    while (rs.next()){
                        row = spreadsheet.createRow(cc);
                        for (int j = 0; j < colCount; j++) {
                            if (rs.getString(j+1) != null) {
                                row.createCell(j).setCellValue(rs.getString(j+1));
                            } else {
                                row.createCell(j).setCellValue("");
                            }
                        }
                        cc++;
                    }

                    FileOutputStream fileOut = new FileOutputStream("Query_" + LocalDateTime.now().toString() + ".xls");
                    workbook.write(fileOut);
                    fileOut.close();
                }catch(IOException ex){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error: "+ex.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                    ex.printStackTrace();
                }


                conn.commit();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Result Exported !", ButtonType.OK);
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR, "PLEASE SELECT LOCATION AND TRY AGAIN...", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
