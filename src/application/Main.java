package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			//Main.fxml 파일을 불러와서 화면에 붙여넣는다
			BorderPane root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			
			Scene scene = new Scene(root,700,500);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			scene.getStylesheets().add(getClass().getResource("DarkTheme.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
