package sample;

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
import java.net.*;
import java.net.MalformedURLException;

public class Main extends Application {
    public static Weather cityWeather = new Weather();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("weatherFXApp2.fxml"));
        primaryStage.setTitle("Weather Search Ultimate");
        primaryStage.setScene(new Scene(root, 600, 545));
        primaryStage.setHeight(530);
        primaryStage.setResizable(false);
        primaryStage.show();
        // got button command working
        // lets see if i can do a commit without getting errors and wanting me to merge
    }


    public static void main(String[] args) throws SQLException,Exception {
        launch(args);
        System.out.println("hello");

        Scanner input = new Scanner(System.in);
        Connection conn = connectToDB("zipDatabase.db");
        String key = getKey();
        //http://api.wunderground.com/api/Key/conditions/q/MO/Maitland.xml


        System.out.println("Would you like to search by zip or city and state? Enter 1 for zip or 2 for city and state: ");
        String option = "1";//input.nextLine();


        if(option.equals("1")){
            System.out.println("Enter a zipcode to search for: ");
            String zipcodeSearch = "64506";//input.next();
            System.out.println("\n");

            String[] searchResults = getCityStateFromZip(conn,zipcodeSearch);
            String cityFixed = searchResults[0].replaceAll(" ", "_");

            String urlString = String.format("http://api.wunderground.com/api/%s/conditions/q/%s/%s.xml",key,searchResults[1],cityFixed);
            System.out.println(urlString);

            String urlStringT = "http://www.pcrepairforums.com/misc/school/Maitland.xml";// just generating data from static xml file.

            String data = readFromURL(urlStringT);

            // parse data for weather data.
            parseXmlString(data);
            System.out.println(cityWeather.toString());


        }else if(option.equals("2")){
            System.out.println("Enter a state to search in: ");
            String stateSearch = input.nextLine();
            System.out.println("Enter a city to search: ");
            String citySearch = input.nextLine();

            String[] searchResults = getCityStateFromCityName(conn,citySearch,stateSearch);
            String cityFixed = searchResults[0].replaceAll(" ", "_");

            String urlString = String.format("http://api.wunderground.com/api/%s/conditions/q/%s/%s.xml",key,searchResults[1],cityFixed);
            System.out.println(urlString);

            String urlStringT = "http://www.pcrepairforums.com/misc/school/Maitland.xml";

            String data = readFromURL(urlStringT);
            parseXmlString(data);

            System.out.println(cityWeather.toString());



        }else{
            System.out.println("You did not enter a valid input. Run program again. ");
        }

        // once I populate entire class data I can do the gui part



        closeDB(conn);
    }

    public static Connection connectToDB(String databaseName){
        try {
            String connectString = "jdbc:sqlite:" + databaseName;
            Connection conn = DriverManager.getConnection(connectString);
            if(conn == null) {
                conn = null;

            }
            return conn;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("It did not open");
            return null;
        }
    }

    public static void closeDB(Connection conn){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readFromDB(Connection conn,String query){
        try {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String zipcode = rs.getString("zipcode");
                String ziptype = rs.getString("zipcodetype");
                String city = rs.getString("city");
                String state = rs.getString("state");
                String locationType = rs.getString("locationtype");
                String worldRegion = rs.getString("worldregion");
                String country = rs.getString("country");

                System.out.printf("City: %-20s state: %-5s zipcode: %-7s country: %-4s locationType: %-15s\n",city,state,zipcode,country,locationType);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String[] getCityStateFromZip(Connection conn,String zip){
        String[] zipInfo = new String[2];
        try {
            Statement stmt = conn.createStatement();
            String queryString = String.format("Select * from zips where zipcode like '%s' and locationtype like 'PRIMARY' ",zip);
            ResultSet rs = stmt.executeQuery(queryString);

            while (rs.next()) {
                String zipcode = rs.getString("zipcode");
                zipInfo[0] = rs.getString("city");
                zipInfo[1] = rs.getString("state");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return zipInfo;
    }

    public static String[] getCityStateFromCityName(Connection conn, String cityName, String state){
        String[] cityState = new String[2];
        try {
            Statement stmt = conn.createStatement();
            String queryString = String.format("Select city,state from zips where state like '%s' and city like '%s%%' ",state,cityName);
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                cityState[0] = rs.getString("city");
                cityState[1] = rs.getString("state");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cityState;
    }

    public static String getKey(){
        String key = "";
        try {
            Scanner input = new Scanner(new File("creds.txt"));
            key = input.nextLine();
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return key;
    }

    public static String readFromURL(String urlString){ // delete file creation when finished
        String data = "";
        String line;
        try {
            File weatherData = new File("WeatherData.xml");
            PrintWriter output = new PrintWriter(weatherData);
            URL  url = new URL(urlString);
            BufferedReader input = new BufferedReader(new InputStreamReader( url.openStream()));

            while((line = input.readLine()) != null){
                data += line +"\n";
                output.println(line);
            }
            input.close();
            output.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(3);
            e.printStackTrace();
        }


        return data;
    }

    public static void parseXmlString(String data){
        Document doc = Jsoup.parse(data,"", Parser.xmlParser()); // this one reads directly from the string
        for(Element e:doc.select("current_observation")){
            cityWeather.city=e.selectFirst("city").text();
            cityWeather.state=e.selectFirst("state").text();
            cityWeather.stateName=e.select("state_name").text();
            cityWeather.zip= e.select("zip").text();
            cityWeather.observationTime = e.select("observation_time").text();
            cityWeather.weather = e.select("weather").text();
            cityWeather.tempString = e.select("temperature_string").text();
            cityWeather.relativeHumidity = e.select("relative_humidity").text();
            cityWeather.windString = e.select("wind_string").text();
            cityWeather.percipitation24Hr = e.select("precip_today_in").text();
        }
    }


}
