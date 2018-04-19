package MarschelHmwk05FX;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import org.jsoup.nodes.Document;
import org.jsoup.parser.*;
import java.net.*;


public class WeatherDriver {
    public static Weather cityWeather = new Weather();
    public static String key = getKey();
    public WeatherDriver(){
        //
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
            System.err.println("DATABASE DID NOT OPEN!!!");
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
                zipInfo[0] = rs.getString("city");
                zipInfo[1] = rs.getString("state");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
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
            String queryString = String.format("Select * from zips where state like '%s' and city like '%s%%' " +
                    "and decommissioned like 'false' order by estimatedpopulation ",state,cityName);
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                cityState[0] = rs.getString("city");
                cityState[1] = rs.getString("state");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            cityState[0] = "error";
            cityState[1] = "error";
        }
        closeDB(conn);
        if(cityState[0] == null || cityState[1] == null){
            cityState[0] = "error";
            cityState[1] = "error";
        }

        return cityState;
    }

    public static String readFromURL(String urlString){ // returns string of source code of link
        String data = "";
        String line;
        try {
            URL  url = new URL(urlString);
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader( url.openStream()));
            } catch (FileNotFoundException e) {
                data = "error";
                return data;
            }
            while((line = input.readLine()) != null){
                data += line +"\n";
            }
            input.close();
        } catch (Exception e) {
            data = "error";
            return data;
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

            byte[] response = getImageSource(imageLink);
            if(num ==1){
                createImageFromSource(response,"radarImage1.jpg");
            }else if(num ==2){
                createImageFromSource(response,"radarImage2.jpg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static byte[] getImageSource(String imageLink){
        byte[] response = null;
        try {
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
            response = out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void createImageFromSource(byte[] source,String fileName){
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(source);
            fos.close();
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
                    stationValue = e.attr("value");
                }
            }
            stationValue = stationValue.replaceAll("/auto/virtuallythere_jan3/radar/station.asp\\?ID=","");
            String stationId = stationValue.substring(0,3);
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
                    "order by estimatedpopulation",state,city);//and locationtype like 'primary'
            ResultSet rs = stmt.executeQuery(queryString);
            while (rs.next()) {
                result = rs.getString("zipcode");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            result = "error";
        }
        closeDB(conn);
        return result;
    }

    public static ArrayList<String> createStatesArray(){
        ArrayList<String> result = new ArrayList<String>();
        Connection conn = connectToDB("zipDatabase.db");

        try {
            Statement stmt = conn.createStatement();
            String queryString = String.format("Select * from states order by stateFull");
            ResultSet rs = stmt.executeQuery(queryString);
            while(rs.next()){
                result.add(rs.getString("stateFull"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeDB(conn);
        return result;
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
