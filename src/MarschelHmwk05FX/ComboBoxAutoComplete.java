package MarschelHmwk05FX;

import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

public class ComboBoxAutoComplete<T> {
    // class that controlls the comboBox and allows me to filter the items with each action in the controller
    private ComboBox<T> cmb;
    public String filter = "";
    private ObservableList<T> originalItems;

    public ComboBoxAutoComplete(ComboBox<T> cmb) {// constructor
        this.cmb = cmb;
        originalItems = FXCollections.observableArrayList(cmb.getItems());
        cmb.setTooltip(new Tooltip());
    }

    public void keyReleased(KeyEvent e){ //calls the keypress method
        handleOnKeyPressed(e);
    }

    public void onHiding(Event e){
        handleOnHiding(e);
    }

    public void handleOnKeyPressed(KeyEvent e) {
        ObservableList<T> filteredList = FXCollections.observableArrayList();
        KeyCode code = e.getCode();
        cmb.getEditor().end();

        // these set the filter list for each keycode
        if(cmb.getEditor().getText().length() ==0){
            filter = "";
        }
        if(code.isLetterKey() && code != KeyCode.SPACE) {
            filter += e.getText();
        }
        if(code == KeyCode.SPACE){
            filter = cmb.getEditor().getText();
        }
        if(code == KeyCode.BACK_SPACE && filter.length() > 0) {
            cmb.getItems().setAll(originalItems);
            filter = cmb.getEditor().getText();
            cmb.getEditor().end();
        }
        if(code == KeyCode.ESCAPE) {
            filter = "";
        }
        if(code == KeyCode.DOWN || code == KeyCode.UP){
            return;
        }
        if(code == KeyCode.ENTER){
            filter = cmb.getEditor().getText();
            return;
        }
        if(code == KeyCode.TAB ){
            filter = cmb.getEditor().getText();
            return;
        }

        if(filter.length() == 0) {
            filteredList = originalItems;
            cmb.getTooltip().hide();
        }else {
            Stream<T> items = cmb.getItems().stream();
            String txtUsr = unaccent(filter.toString().toLowerCase());

            // applies the filter to each item in comboBox list.
            items.filter(el -> unaccent(el.toString().toLowerCase()).contains(txtUsr)).forEach(filteredList::add);

//            items.filter(el -> {
//                return unaccent(el.toString().toLowerCase()).contains(txtUsr);
//            }).forEach(filteredList::add);

            cmb.getTooltip().setText(txtUsr);
            Window stage = cmb.getScene().getWindow();
            double posX = stage.getX() + cmb.getBoundsInParent().getMinX();
            double posY = stage.getY() + cmb.getBoundsInParent().getMinY();
            cmb.getTooltip().show(stage, posX, posY);
            cmb.show();
        }
        cmb.getItems().setAll(filteredList);
    }

    public void handleOnHiding(Event e) {
        filter = "";
        cmb.getTooltip().hide();
        T s = cmb.getSelectionModel().getSelectedItem();
        cmb.getItems().setAll(originalItems);
        cmb.getSelectionModel().select(s);
    }

    private String unaccent(String s) { // returns a pattern of the matcher
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}
