package MarschelHmwk05FX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application {
    public static Weather cityWeather = new Weather();

    @Override
    public void start(Stage primaryStage) throws Exception{
        // download current logo image.
        byte[] logoImageSource = WeatherDriver.getImageSource("https://icons.wxug.com/logos/PNG/wundergroundLogo_4c.png");
        WeatherDriver.createImageFromSource(logoImageSource,"src/MarschelHmwk05FX/wuLogo1.png");
        WeatherDriver.createImageFromSource(logoImageSource,"src/MarschelHmwk05FX/wuLogo2.png");

        Parent root = FXMLLoader.load(getClass().getResource("weatherFXApp2.fxml"));
        primaryStage.setTitle("Weather Search Ultimate");
        primaryStage.setScene(new Scene(root, 600, 545));
        primaryStage.setHeight(530);
        primaryStage.setResizable(false);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);


    }

}
