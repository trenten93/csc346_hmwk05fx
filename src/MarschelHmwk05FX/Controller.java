package MarschelHmwk05FX;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;


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
    public ImageView image1; // logo image

    @FXML
    public ImageView image2; // logo image

    @FXML
    public ImageView radarTab1Image; // tab1 image

    @FXML
    public ImageView radarTab2Image;


    public boolean imageClickValid1 = false;
    public boolean imageClickValid2 = false;

    public boolean cityFieldValid = false;
    public boolean citySearchButtonValid = false;
    public boolean zipTabOpen = false;
    public String errorMessage="";




    public void zipSearchButtonRun() {
        String zip = zipField.getText();
        double zipDouble = Double.parseDouble(zip)/100000;
        int zipInt = Integer.parseInt(zip);
        if(zipInt>99950){
            zip = "99950";
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
        String state = stateComboBox.getValue().toString();
        String city = cityField.getText();

        String[] searchResults = WeatherDriver.getCityStateFromCityName(city,state);
        String cityFixed = searchResults[0].replaceAll(" ", "_");

        // add while block here to catch errors and either automagically try to fix them or let the user know that
        // they entered invalid information. or maybe try a combo of both.

        String urlString = String.format("http://api.wunderground.com/api/%s/conditions/q/%s/%s.xml",key,searchResults[1],cityFixed);
        System.out.println(urlString);

        String urlStringT = "http://www.pcrepairforums.com/misc/school/Maitland.xml";

        String data = WeatherDriver.readFromURL(urlStringT);
        WeatherDriver.parseXmlString(data);

        String output = WeatherDriver.cityWeather.toStringFormat();

        cityOutput.setText(output);
        String zip = WeatherDriver.getZipFromCityState(state,city);
        System.out.println("HELLOASDFLKAJSLDFKAJSLDFKJA;SLKDFJ");

        System.out.println(zip);
        generateRadarImage(zip,2);
        imageClickValid2 = true;


    }


    public void cityTabOpened(){
        citySearchButton.setDisable(true);
        cityField.setDisable(true);
        stateComboBox.getItems().addAll(statesArray);
        stateComboBox.setVisibleRowCount(13);
        zipTabOpen = false;
    }

    public void zipTabOpened(){
        zipSearchButton.setDisable(true);
        zipTabOpen = true;
    }

    public void zipEnter(){
        if(zipField.getText().length() == 5){
            zipSearchButtonRun();
        }
    }

    public void cityEnter(){
        cityDataPressed();
        if(cityField.getText().length()>0 && stateComboBox.getValue().length()==2){
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
        int zipInt = Integer.parseInt(zip);

        // get the link to the correct weather station
        String radarStationPage = formatStationString(zip);

        System.out.println(radarStationPage+"\n");

        String radarStationPageData = WeatherDriver.readFromURL(radarStationPage);

        while(radarStationPageData.equalsIgnoreCase("error")){
            zipInt ++;
            System.out.println(zipInt);
            zip = Integer.toString(zipInt);
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



    public void zipFieldChanged(){
        if(zipField.getText().length()==5){
            zipSearchButton.setDisable(false);
        }else{
            zipSearchButton.setDisable(true);
        }
    }

    public void zipFieldPressed(){
        zipFieldChanged();
    }

    public void cityDataEdit(){

        String stateComboBoxValue = stateComboBox.getEditor().getText();

        if(stateComboBoxValue != null){
            if(Arrays.asList(statesArray).contains(stateComboBoxValue.toUpperCase())){
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

    public void cityDataPressed(){
        cityDataEdit();
    }



}
