package MarschelHmwk05FX;

import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

public class ComboBoxAutoComplete<T> {
    private ComboBox<T> cmb;
    public String filter = "";
    private ObservableList<T> originalItems;

    public ComboBoxAutoComplete(ComboBox<T> cmb) {
        this.cmb = cmb;
        originalItems = FXCollections.observableArrayList(cmb.getItems());
        cmb.setTooltip(new Tooltip());
    }

    public void keyReleased(KeyEvent e){
        System.out.println("ComboBoxAuto Class Key event code is: "+e.getCode());
        handleOnKeyPressed(e);
    }

    public void onHiding(Event e){
        System.out.println("Event in hiding in AutoCombo is: "+e.getEventType().toString());
        handleOnHiding(e);
    }

    public void handleOnKeyPressed(KeyEvent e) {
        ObservableList<T> filteredList = FXCollections.observableArrayList();
        KeyCode code = e.getCode();
        System.out.println("Keycode is: "+code);
        System.out.println("Filter is: "+filter);

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
            filter = cmb.getEditor().getText();
            cmb.getItems().setAll(originalItems);
        }
        if(code == KeyCode.ESCAPE) {
            filter = "";
        }
        if(code == KeyCode.DOWN || code == KeyCode.UP){
            return;
        }
        if(code == KeyCode.ENTER){
            filter = cmb.getValue().toString();
            return;
        }
        if(code == KeyCode.TAB){
            //filter = cmb.getEditor().getText();
            filter = cmb.getValue().toString();
            return;
        }

        if(filter.length() == 0) {
            filteredList = originalItems;
            cmb.getTooltip().hide();
        }else {
            Stream<T> items = cmb.getItems().stream();
            String txtUsr = unaccent(filter.toString().toLowerCase());
            items.filter(el -> unaccent(el.toString().toLowerCase()).contains(txtUsr)).forEach(filteredList::add);
            cmb.getTooltip().setText(txtUsr);
            Window stage = cmb.getScene().getWindow();
            double posX = stage.getX() + cmb.getBoundsInParent().getMinX();
            double posY = stage.getY() + cmb.getBoundsInParent().getMinY();
            cmb.getTooltip().show(stage, posX, posY);
            cmb.show();
        }
        cmb.getItems().setAll(filteredList);
        return;
    }

    public void handleOnHiding(Event e) {
        filter = "";
        cmb.getTooltip().hide();
        T s = cmb.getSelectionModel().getSelectedItem();
        cmb.getItems().setAll(originalItems);
        cmb.getSelectionModel().select(s);
    }

    private String unaccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}
