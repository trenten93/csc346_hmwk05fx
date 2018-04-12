package MarschelHmwk05FX;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.parser.*;
import sun.security.validator.ValidatorException;
import javax.sound.midi.ControllerEventListener;
import java.net.*;
import java.net.MalformedURLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import javafx.scene.image.*;
import javafx.scene.image.Image;




public class Controller{
    public static String key = WeatherDriver.getKey();

    public String tempState= "";

    public String[] statesArray = {"AK", "AL", "AR", "AZ", "CA", "CO", "CT",
            "DC", "DE", "FL", "GA", "HI", "IA", "ID", "IL", "IN", "KS", "KY",
            "LA", "MA", "MD", "ME", "MI", "MN", "MO", "MS", "MT", "NC", "ND",
            "NE", "NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "RI",
            "SC", "SD", "TN", "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY"};



    @FXML
    public Button zipSearchButton;

    @FXML
    public TextArea zipOutput;

    @FXML
    public TextField zipField;

    @FXML
    public TextField cityField;

    @FXML
    public TextArea cityOutput;

    @FXML
    public Button citySearchButton;

    @FXML
    public ComboBox<String> stateComboBox;

    @FXML
    public TabPane mainWindow;

    @FXML
    public Tab cityTab;

    @FXML
    public ImageView image1;

    @FXML
    public ImageView image2;

    @FXML
    public Button testButton;

    @FXML
    public ImageView radarTab1Image;






    public void zipSearchButtonRun() {
        String zip = zipField.getText();

        String[] searchResults = WeatherDriver.getCityStateFromZip(zip);
        String cityFixed = searchResults[0].replaceAll(" ", "_");

        String urlString = String.format("http://api.wunderground.com/api/%s/conditions/q/%s/%s.xml",key,searchResults[1],cityFixed);
        System.out.println(urlString);

        String urlStringT = "http://www.pcrepairforums.com/misc/school/Maitland.xml";// just generating data from static xml file.

        String data = WeatherDriver.readFromURL(urlStringT);

        // parse data for weather data.
        WeatherDriver.parseXmlString(data);

        String output = WeatherDriver.cityWeather.toStringFormat();

        zipOutput.setText(output);


    }

    public void citySearchButtonRun(){
        String state = stateComboBox.getValue().toString();
        String city = cityField.getText();

        String[] searchResults = WeatherDriver.getCityStateFromCityName(city,state);
        String cityFixed = searchResults[0].replaceAll(" ", "_");

        String urlString = String.format("http://api.wunderground.com/api/%s/conditions/q/%s/%s.xml",key,searchResults[1],cityFixed);
        System.out.println(urlString);

        String urlStringT = "http://www.pcrepairforums.com/misc/school/Maitland.xml";

        String data = WeatherDriver.readFromURL(urlStringT);
        WeatherDriver.parseXmlString(data);

        String output = WeatherDriver.cityWeather.toStringFormat();

        cityOutput.setText(output);
    }


    public void cityTabOpened(){
        stateComboBox.getItems().addAll(statesArray);
        stateComboBox.setVisibleRowCount(13);


    }

    public void zipEnter(){
        if(zipField.getText().length() == 5){
            zipSearchButtonRun();
        }
    }

    public void cityEnter(){
        if(cityField.getText().length()>0){
            citySearchButtonRun();
        }
    }

    public void imageClick(){
        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI oURL = new URI("http://www.wunderground.com");
            desktop.browse(oURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testButtonRun(){
        String zip = zipField.getText();
        String[] cityState = WeatherDriver.getCityStateFromZip(zip);
        String cityFixed = cityState[0].replaceAll(" ", "%20");

        String radarLink = String.format("https://www.wunderground.com/radar/radblast.asp?ID=eax&label=%s",cityFixed);


        String radarLinkData = WeatherDriver.readFromURL(radarLink);

        WeatherDriver.getLinkToRadarPage(radarLinkData);

        Image radar1 = new Image("file:radarImage.jpg");

        radarTab1Image.setImage(radar1);









    }



}
