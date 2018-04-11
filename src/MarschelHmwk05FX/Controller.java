package MarschelHmwk05FX;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;


public class Controller{




    @FXML
    public Button zipSearchButton;

    @FXML
    public TextArea zipOutput;

    @FXML
    public TextField zipField;

    @FXML
    public TextField stateField;

    @FXML
    public TextField cityField;

    @FXML
    public TextArea cityOutput;

    @FXML
    public Button citySearchButton;



    public void zipSearchButtonRun() {
        System.out.println("button clicked");
        System.out.println(zipField.getText());

        String output = zipField.getText();

        zipOutput.setText(output);

    }

    public void citySearchButtonRun(){
        System.out.println("Button clicked");
        System.out.println(stateField.getText());
        System.out.println(cityField.getText());

        String output = cityField.getText() + " " + stateField.getText();

        cityOutput.setText(output);
    }


    public void handle(ActionEvent event){
        //String zip = userField.getText();

    }




}
