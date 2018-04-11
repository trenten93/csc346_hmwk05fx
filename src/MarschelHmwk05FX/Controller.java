package MarschelHmwk05FX;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
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


public class Controller{
    public static String key = WeatherDriver.getKey();

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
        String state = stateField.getText();
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







}
