import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UserInterface extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Sniffer Application");
        Group group = new Group(new Label("Welcome to Sniffer App"));
        Scene scene = new Scene(group, 300, 500);
        stage.setScene(scene);
        stage.show();
    }
}
