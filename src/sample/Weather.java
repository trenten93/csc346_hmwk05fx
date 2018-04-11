package sample;

public class Weather {
    public String city;
    public String state;
    public String stateName;
    public String zip;
    public String observationTime;
    public String weather;
    public String tempString;
    public String tempF;
    public String tempC;
    public String relativeHumidity;
    public String windString;
    public String windDirection;
    public String windDegrees;
    public String windMph;
    public String windGustMph;
    public String feelsLikeF;
    public String visibilityM;
    public String uvIndex;
    public String percipitation1Hr;
    public String percipitation24Hr;


    public Weather(){
        //nothing to see here set variables directly
    }


    @Override
    public String toString(){
        String result = String.format("%s %s zip: %s at time: %s weather: %s temp of: %s humidity: %s Wind: %s percipitation 24hr: %s inches"
                ,city,stateName,zip,observationTime,weather,tempString,relativeHumidity,windString,percipitation24Hr);
        return result;
    }
}
