package MarschelHmwk05FX;

public class Weather {
    public String city;
    public String state;
    public String stateName;
    public String zip;
    public String observationTime;
    public String weather;
    public String tempString;
    public String relativeHumidity;
    public String windString;
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

    public String toStringFormat(){
        String result = String.format("City: %s \nState: %s \nzip: %s \ntime: %s \nweather: %s \ntemp: %s \nhumidity: %s \nWind: %s \npercipitation 24hr: %s inches"
                ,city,stateName,zip,observationTime,weather,tempString,relativeHumidity,windString,percipitation24Hr);


        return result;
    }

}
