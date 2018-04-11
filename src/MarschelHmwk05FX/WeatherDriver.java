package MarschelHmwk05FX;

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


@SuppressWarnings("ALL")
public class WeatherDriver {
    public static Weather cityWeather = new Weather();
    //public static Connection conn = connectToDB("zipDatabase.db");
    public static String key = getKey();


    public WeatherDriver(){

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

    public static void closeDB(Connection conn){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readFromDB(String query){
        Connection conn = connectToDB("zipDatabase.db");
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
        closeDB(conn);
    }

    public static String[] getCityStateFromZip(String zip){
        String[] zipInfo = new String[2];
        Connection conn = connectToDB("zipDatabase.db");
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
        closeDB(conn);
        return zipInfo;
    }

    public static String[] getCityStateFromCityName(String cityName, String state){
        String[] cityState = new String[2];
        Connection conn = connectToDB("zipDatabase.db");
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
        closeDB(conn);
        return cityState;
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
