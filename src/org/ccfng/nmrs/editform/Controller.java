package org.ccfng.nmrs.editform;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.ccfng.global.DBMiddleMan;
import org.ccfng.global.DestinationConnectionClass;
import org.ccfng.global.KeyValueClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {

    @FXML
    private ComboBox<KeyValueClass> cboForm;

    @FXML
    private TextArea txtFind;

    @FXML
    private TextArea txtReplace;

    @FXML
    private Button btnCancel;

    DestinationConnectionClass dd;

    public void initialize(){
        dd = new DestinationConnectionClass();
        cboForm.setItems(DBMiddleMan.allForms);
    }

    @FXML
    private void replace(){
        if(txtFind.getText().equals("") || txtFind.getText() == null){
            return;
        }
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName(DBMiddleMan.DRIVER);

            //STEP 3: Open a connection

            conn = DriverManager.getConnection(dd.getDestination_jdbcUrl(), dd.getDestinationUsername(), dd.getDestinationPassword());

            //STEP 4: Execute a query

            stmt = conn.createStatement();
            String sql = "UPDATE htmlformentry_html_form SET xml_data = REPLACE(xml_data, '"+txtFind.getText()+"','"+txtReplace.getText()+"') WHERE form_id="+ cboForm.getSelectionModel().getSelectedItem().getKey();

            stmt.execute(sql);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Update Successful", ButtonType.OK);
            alert.showAndWait();
            txtFind.setText("");
            txtReplace.setText("");
            cboForm.getSelectionModel().select(null);
        } catch (SQLException se) {
            //Handle errors for JDBC
            Alert alert = new Alert(Alert.AlertType.ERROR, se.getMessage(), ButtonType.OK);
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }// do nothing
        }
    }

    @FXML
    private void close(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        // do what you have to do
        stage.close();
    }


}
