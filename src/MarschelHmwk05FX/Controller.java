package MarschelHmwk05FX;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;


public class Controller{
    public static String key = WeatherDriver.getKey();

    public ArrayList<String> states = WeatherDriver.createStatesArray();

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
    public TabPane mainWindow;

    @FXML
    public Tab cityTab;

    @FXML
    public ImageView image1; // logo image

    @FXML
    public ImageView image2; // logo image

    @FXML
    public ImageView radarTab1Image; // tab1 image

    @FXML
    public ImageView radarTab2Image;

    @FXML
    public ComboBox<String> stateComboBox;

    public ComboBoxAutoComplete<String> autoCombo;



    public boolean imageClickValid1 = false;
    public boolean imageClickValid2 = false;

    public boolean cityFieldValid = false;
    public boolean citySearchButtonValid = false;
    public boolean zipTabOpen = false;
    public String errorMessage="";




    public void zipSearchButtonRun() {
        if(!isZipFieldValid()){
            validateZipField();
            if(!isZipFieldValid()){
                return;
            }
        }

        String zip = zipField.getText();

        double zipDouble = Double.parseDouble(zip)/100000;
        int zipInt = Integer.parseInt(zip);

        if(zipInt>99950){
            zip = "99950";
        }
        if(zipDouble < 0.00501){
            zip = "00501";
        }


        String[] searchResults = WeatherDriver.getCityStateFromZip(zip);

        while(searchResults[0].equals("error") && searchResults[1].equals("error")){
            zipDouble = zipDouble+0.00001;
            zip = Double.toString(zipDouble);
            if(zip.length()>7){
                zip = zip.substring(2,7);
            }else{
                zip = zip.substring(2);
            }
            searchResults = WeatherDriver.getCityStateFromZip(zip);
            errorMessage = "YOUR INITIAL ZIP WAS INVALID. \nShowing results for zip "+zip+ " instead\n\n";
        }
        String cityFixed = searchResults[0].replaceAll(" ", "_");


        String urlString = String.format("http://api.wunderground.com/api/%s/conditions/q/%s/%s.xml",key,searchResults[1],cityFixed);
        System.out.println(urlString);

        String urlStringT = "http://www.pcrepairforums.com/misc/school/Maitland.xml";// just generating data from static xml file.

        String data = WeatherDriver.readFromURL(urlStringT);

        // parse data for weather data.
        WeatherDriver.parseXmlString(data);

        String output = WeatherDriver.cityWeather.toStringFormat();

        zipOutput.setText(errorMessage);
        zipOutput.appendText(output);


        errorMessage = "";
        generateRadarImage(zip,1);
        imageClickValid1 = true;


    }

    public void citySearchButtonRun(){
        String stateFull = stateComboBox.getValue().toString();
        String city = cityField.getText();

        String state = WeatherDriver.fullStateToAbb(stateFull);

        String[] searchResults = WeatherDriver.getCityStateFromCityName(city,state);
        String cityFixed = searchResults[0].replaceAll(" ", "_");

        // add while block here to catch errors and either automagically try to fix them or let the user know that
        // they entered invalid information. or maybe try a combo of both.

        if(searchResults[0].equals("error") || searchResults[1].equals("error")){
            cityOutput.setText("That was not a valid city & state!");
            return;
        }


        String urlString = String.format("http://api.wunderground.com/api/%s/conditions/q/%s/%s.xml",key,searchResults[1],cityFixed);
        System.out.println(urlString);

        String urlStringT = "http://www.pcrepairforums.com/misc/school/Maitland.xml";

        String data = WeatherDriver.readFromURL(urlStringT);
        WeatherDriver.parseXmlString(data);

        String output = WeatherDriver.cityWeather.toStringFormat();

        cityOutput.setText(output);
        String zip = WeatherDriver.getZipFromCityState(state,city);

        System.out.println(zip);
        generateRadarImage(zip,2);
        imageClickValid2 = true;


    }


    public void cityTabOpened(){
        Image logo2 = new Image("file:src/MarschelHmwk05FX/wuLogo2.png");
        image2.setImage(logo2);

        citySearchButton.setDisable(true);
        cityField.setDisable(true);

        stateComboBox.setTooltip(new Tooltip());

        stateComboBox.getItems().clear();
        stateComboBox.getItems().addAll(states);

        stateComboBox.setVisibleRowCount(13);

        autoCombo = new ComboBoxAutoComplete<String>(stateComboBox);
        zipTabOpen = false;


    }


    public void zipTabOpened(){
        Image logo1 = new Image("file:src/MarschelHmwk05FX/wuLogo1.png");
        image1.setImage(logo1);


        zipSearchButton.setDisable(true);
        zipTabOpen = true;

    }


    public void zipEnter(){
        if(zipField.getText().length() == 5 && isZipFieldValid()){
            zipSearchButtonRun();
        }
    }


    public void cityEnter(){
        if(cityField.getText().length()>0){
            citySearchButtonRun();
        }
    }

    public void imageClick(){ // for logo
        try {
            Desktop desktop = Desktop.getDesktop();
            URI oURL = new URI("http://www.wunderground.com");
            desktop.browse(oURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void radarImageClick(){
        if(zipTabOpen == true){
            if(imageClickValid1){
                try {
                    File radarImage = new File("radarImage1.jpg");
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(radarImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(zipTabOpen == false){
            if(imageClickValid2){
                try {
                    File radarImage = new File("radarImage2.jpg");
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(radarImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String formatStationString(String zip){
        String result = String.format("https://www.wunderground.com/cgi-bin/findweather/"+
                "getForecast?query=%s&btnWx=Go&vthost=&vt_language=&adtags=&brand=virtuallythere_jan3&select2=Select+mode",zip);
        return result;
    }

    public void generateRadarImage(String zip, int option){
        System.out.println("ZIP IS: "+zip);

        int zipInt = 0;
        try {
            zipInt = Integer.parseInt(zip);
        } catch (NumberFormatException e) {
            System.err.println("error in zip format");
            //e.printStackTrace();
        }

        double zipDouble = Double.parseDouble(zip)/100000;

        // get the link to the correct weather station
        String radarStationPage = formatStationString(zip);

        System.out.println(radarStationPage+"\n");

        String radarStationPageData = WeatherDriver.readFromURL(radarStationPage);

        while(radarStationPageData.equalsIgnoreCase("error")){
            zipInt ++;
            zipDouble = zipDouble +0.00001;
            System.out.println(zipInt);
            //zip = Integer.toString(zipInt);

            zip = Double.toString(zipDouble);

            zip = zip.substring(2,7);

            radarStationPage = formatStationString(zip);
            radarStationPageData = WeatherDriver.readFromURL(radarStationPage);
        }

        String stationId = WeatherDriver.findWeatherStationId(radarStationPageData);
        System.out.println(stationId);
        String radarLink = String.format("https://www.wunderground.com/radar/radblast.asp?ID=%s",stationId);

        String radarLinkData = WeatherDriver.readFromURL(radarLink); // get the sourcecode of page with radar image

        if(option == 1){
            WeatherDriver.downloadRadarImage(radarLinkData,1);
            Image radar1 = new Image("file:radarImage1.jpg");
            radarTab1Image.setImage(radar1);
        }else if(option == 2){
            WeatherDriver.downloadRadarImage(radarLinkData,2);
            Image radar2 = new Image("file:radarImage2.jpg");
            radarTab2Image.setImage(radar2);
        }

    }


    public void validateZipField(){
        if(!zipField.getText().matches("[0-9]*")){
            String s = zipField.getText();
            String temp = s.replaceAll("[^\\d]", "");
            zipField.setText(temp);
            zipField.positionCaret(zipField.getText().length());
        }

        if(zipField.getText().length() >5){
            String s = zipField.getText().substring(0,5);
            zipField.setText(s);
            zipField.positionCaret(5);
        }

    }

    public boolean isZipFieldValid(){
        if(zipField.getText().matches("[0-9]*") && zipField.getText().length() == 5){
            return true;
        }else{
            return false;
        }
    }

    public void zipFieldChanged(){//key released
        if(!isZipFieldValid()){
            zipSearchButton.setDisable(true);
            validateZipField();
        }else{
            zipSearchButton.setDisable(false);
        }

        if(zipField.getText().length() == 5){
            zipSearchButton.setDisable(false);
        }else{
            zipSearchButton.setDisable(true);
        }


    }

    public void zipFieldPressed(){
        if(!isZipFieldValid()){
            zipSearchButton.setDisable(true);
            validateZipField();
        }else{
            zipSearchButton.setDisable(false);
        }

    }


    public void cityDataEdit(){
        String stateComboBoxValue = stateComboBox.getEditor().getText();
        if(stateComboBoxValue != null){
            boolean flag = false;
            String compareState = stateComboBoxValue.toLowerCase();
            for(String s:states){
                if(s.toLowerCase().equals(compareState)){
                    flag = true;
                }
            }
            if(flag){
                cityFieldValid = true;
            }else{
                cityFieldValid = false;
                citySearchButtonValid = false;
            }
        }
        if(cityFieldValid){
            cityField.setDisable(false);
        }else{
            cityField.setDisable(true);
        }

        if(stateComboBoxValue !=null && cityField.getText() != null){
            if(cityFieldValid && cityField.getText().length()>1){
                citySearchButtonValid = true;
            }else{
                citySearchButtonValid = false;
            }
        }else{
            citySearchButtonValid = false;
        }

        if(citySearchButtonValid){
            citySearchButton.setDisable(false);
        }else{
            citySearchButton.setDisable(true);
        }

    }

    @FXML
    public void cityDataPressed(KeyEvent e){
        cityDataEdit();
    }

    @FXML
    public void cityDataComboBoxKeyPressed(KeyEvent e){
        autoCombo.keyReleased(e);
        cityDataEdit();
    }

    @FXML
    public void stateComboBoxHiding(Event e){
        autoCombo.onHiding(e);
    }




}
