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
        } catch (Exception e) {
            System.out.println("error in getCityStateFromZip");
            zipInfo[0]="error";
            zipInfo[1] = "error";
        }
        closeDB(conn);
        if(zipInfo[0] == null || zipInfo[1] == null){
            zipInfo[0] = "error";
            zipInfo[1] = "error";
        }
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

    public static String readFromURL(String urlString){ // returns string of source code of link
        String data = "";
        String line;
        System.out.println(urlString);
        try {
            URL  url = new URL(urlString);
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader( url.openStream()));
            } catch (FileNotFoundException e) {
                data = "error";
                System.out.println(data);
                return data;
            }
            while((line = input.readLine()) != null){
                data += line +"\n";
            }
            input.close();
        } catch (Exception e) {
            System.out.println(data);
            data = "error";
            return data;
        }
        return data;
    }

    public static String readFromUrlTest(String urlString){
        String result = "";

        return result;
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

    public static void downloadRadarImage(String inputLink, int num){ // connects to radar page and creates image file in root
        String imageLink = "";

        try {
            Document doc = Jsoup.parse(inputLink,"",Parser.htmlParser());
            Elements imageLinks = doc.select("img");
            for(Element e:imageLinks){
                String linkText = e.toString();
                if(linkText.contains("radblast")){
                    imageLink = "https:"+e.attr("src")+".jpg";
                }
            }
            URL  url = new URL(imageLink);
            InputStream input = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1!=(n=input.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            input.close();
            byte[] response = out.toByteArray();
            if(num ==1){
                FileOutputStream fos = new FileOutputStream("radarImage1.jpg");
                fos.write(response);
                fos.close();
            }else if(num ==2){
                FileOutputStream fos = new FileOutputStream("radarImage2.jpg");
                fos.write(response);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String findWeatherStationId(String data){
        String result = "";
        String stationValue = "";
        Document doc = Jsoup.parse(data,"",Parser.htmlParser());
        String title = doc.title();

        try {
            Elements optionTags = doc.select("option");

            for(Element e:optionTags){
                if(e.attr("value").contains("/auto/virtuallythere_jan3/radar/station.asp")){
                    System.out.println(e.attr("value"));
                    stationValue = e.attr("value");
                }
            }
            stationValue = stationValue.replaceAll("/auto/virtuallythere_jan3/radar/station.asp\\?ID=","");
            System.out.println(stationValue);

            String stationId = stationValue.substring(0,3);
            System.out.println(stationId);

            result = stationId;

        } catch (Exception e) {
            System.err.println("ERROR");
        }


        return result;
    }


    public static String getZipFromCityState(String state,String city){
        String result = "";
        Connection conn = connectToDB("zipDatabase.db");
        try {
            Statement stmt = conn.createStatement();
            String queryString = String.format("Select * from zips where state like '%s' and city like '%s' and decommissioned like 'false' " +
                    "and locationtype like 'primary' order by estimatedpopulation",state,city);
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                result = rs.getString("zipcode");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDB(conn);
        return result;
    }

    public static void populateStatesArray(ArrayList states){
        Connection conn = connectToDB("zipDatabase.db");

        try {
            Statement stmt = conn.createStatement();

            String queryString = String.format("Select * from states order by stateFull");
            ResultSet rs = stmt.executeQuery(queryString);

            while(rs.next()){
                states.add(rs.getString("stateFull"));
            }
            rs.close();
            stmt.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDB(conn);
    }


    public static String fullStateToAbb(String fullState){
        String result = "";

        Connection conn = connectToDB("zipDatabase.db");

        try {
            Statement stmt = conn.createStatement();
            String queryString = String.format("Select * from states where stateFull like '%s' ",fullState);
            ResultSet rs = stmt.executeQuery(queryString);

            while(rs.next()){
                result = rs.getString("stateAbb");
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDB(conn);

        return result;

    }




}
