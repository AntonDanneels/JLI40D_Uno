package be.kuleuven.cs.jli40d.client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Anton D.
 */
public class Utils
{
    public static void createPopup( String text )
    {
        final Stage myDialog = new Stage();
        myDialog.initModality( Modality.WINDOW_MODAL);
        myDialog.setTitle( "Info" );

        Button okButton = new Button("Close");
        okButton.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent arg0) {
                myDialog.close();
            }

        });

        Scene myDialogScene = new Scene( VBoxBuilder.create()
                .children(new Text(text), okButton)
                .alignment( Pos.CENTER)
                .padding(new Insets(10))
                .build());

        myDialog.setScene(myDialogScene);
        myDialog.show();
    }
}
