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


    public void zipSearchButtonRun() { // when the search button is clicked
        if(!isZipFieldValid()){
            validateZipField(); // checks if the contents in the zip field are valid and if not it validates them and
            if(!isZipFieldValid()){// checks again.
                return;// if it can't validate it returns to the caller.
            }
        }

        String zip = zipField.getText();
        double zipDouble = Double.parseDouble(zip)/100000; // used to incriment the zip code if it gets an error the first time
        int zipInt = Integer.parseInt(zip);// same as above only for zips without leading 00068

        if(zipInt>99950){ // max zip code
            zip = "99950";
        }
        if(zipDouble < 0.00501){ // min zip code 00501
            zip = "00501";
        }

        String[] searchResults = WeatherDriver.getCityStateFromZip(zip);

        while(searchResults[0].equals("error") && searchResults[1].equals("error")){ // if it got an error fix it
            zipDouble = zipDouble+0.00001; // increment zip code
            zip = Double.toString(zipDouble); // turn it back to a string
            if(zip.length()>7){// strip the 0. from it and any trailing numbers after 5
                zip = zip.substring(2,7);
            }else{
                zip = zip.substring(2);
            }
            searchResults = WeatherDriver.getCityStateFromZip(zip);
            errorMessage = "YOUR INITIAL ZIP WAS INVALID. \nShowing results for zip "+zip+ " instead\n\n";
        }
        String cityFixed = searchResults[0].replaceAll(" ", "_");// replace spaces with _

        String urlString = String.format("http://api.wunderground.com/api/%s/conditions/q/%s/%s.xml",key,searchResults[1],cityFixed);
        String urlStringT = "http://www.pcrepairforums.com/misc/school/Maitland.xml";// just generating data from static xml file.

        String data = WeatherDriver.readFromURL(urlString);

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
        cityOutput.setText("");

        String state = WeatherDriver.fullStateToAbb(stateFull);
        String[] searchResults = WeatherDriver.getCityStateFromCityName(city,state);

        if(searchResults[0].equals("error") || searchResults[1].equals("error")){
            String fixedCity = attemptFixOnCitySearch(city);
            searchResults = WeatherDriver.getCityStateFromCityName(fixedCity,state);

            // error management it will attempt to search for cities that contained the letters you typed
            if(!searchResults[0].equals("error") || !searchResults[1].equals("error")){
                String tempOutput = String.format("Showing results for city that contained the letters '%s'\n",city);
                cityOutput.setText(tempOutput);
            }else{
                cityOutput.setText("That was not a valid city & state search!");
                return;
            }

        }
        String cityFixed = searchResults[0].replaceAll(" ", "_");
        String urlString = String.format("http://api.wunderground.com/api/%s/conditions/q/%s/%s.xml",key,searchResults[1],cityFixed);

        String urlStringT = "http://www.pcrepairforums.com/misc/school/Maitland.xml";

        String data = WeatherDriver.readFromURL(urlString);// gets the xml source code
        WeatherDriver.parseXmlString(data);// parses that data and fills class

        String output = WeatherDriver.cityWeather.toStringFormat();

        cityOutput.appendText(output);
        String zip = WeatherDriver.getZipFromCityState(state,searchResults[0]);

        generateRadarImage(zip,2);
        imageClickValid2 = true;


    }

    public static String attemptFixOnCitySearch(String city){
        // this will fix the city search by adding % in between every letter so it will match letter instead of words
        String result = "";
        ArrayList<String> stringList = new ArrayList<String>();

        if(city.length() == 0){
            result = "%";
        }else{
            String[] temp = city.split("");
            stringList.addAll(Arrays.asList(temp));

            for(int i=0;i<stringList.size();i++){
                if(i==0){
                    stringList.add(0,"%");
                }else if(i%2 == 0){
                    stringList.add(i,"%");
                }
            }
            for(String s:stringList){
                result += s;
            }
        }
        return result;
    }


    public void cityTabOpened(){ // runs when city tab is opened
        Image logo2 = new Image("file:src/MarschelHmwk05FX/wuLogo2.png");
        image2.setImage(logo2);

        citySearchButton.setDisable(true);
        cityField.setDisable(true);

        stateComboBox.setTooltip(new Tooltip());

        stateComboBox.getItems().clear();
        stateComboBox.getItems().addAll(states);

        stateComboBox.setVisibleRowCount(13);

        autoCombo = new ComboBoxAutoComplete<String>(stateComboBox); // creates and manages the combo box
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

    public void radarImageClick(){ // allows you to open radar image
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
        // finds the radar image for a zip code and option sets what tab to show the image in.
        if(zip.equals("")){
            return;
        }
        double zipDouble = 0;
        try {// increment zip if it isn't found the first time
            zipDouble = Double.parseDouble(zip)/100000;
        } catch (NumberFormatException e) {
            System.err.println("Error in generate radar image!");
            return;
        }

        // get the link to the correct weather station
        String radarStationPage = formatStationString(zip);
        String radarStationPageData = WeatherDriver.readFromURL(radarStationPage);

        // increment zip code if it got an error
        while(radarStationPageData.equalsIgnoreCase("error")){
            zipDouble = zipDouble +0.00001;
            zip = Double.toString(zipDouble);
            zip = zip.substring(2,7);
            radarStationPage = formatStationString(zip);
            radarStationPageData = WeatherDriver.readFromURL(radarStationPage);
        }

        String stationId = WeatherDriver.findWeatherStationId(radarStationPageData);
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
        // fixes zip field to ONLY allow numbers and limit length to 5
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
        // checks if the zip field is valid
        if(zipField.getText().matches("[0-9]*") && zipField.getText().length() == 5){
            return true;
        }else{
            return false;
        }
    }
//
    public void zipFieldChanged(){//key released
        // runs on event
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
        // runs when any value is changed in the city tab
        String stateComboBoxValue = stateComboBox.getEditor().getText();
        if(stateComboBoxValue != null){
            boolean flag = false;
            String compareState = stateComboBoxValue.toLowerCase();
            for(String s:states){
                if(s.toLowerCase().equals(compareState)){
                    flag = true;
                }
            }
            if(flag){ // if state is valid it enables the city field
                cityFieldValid = true;
            }else{// once city is valid it enables the search button
                cityFieldValid = false;
                citySearchButtonValid = false;
            }
        }
        if(cityFieldValid){
            cityField.setDisable(false);
        }else{
            cityField.setDisable(true);
        }

        // I know I could simplify this, but I didn't have the time.
        if(stateComboBoxValue !=null && cityField.getText() != null){ // checks for null fields
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
