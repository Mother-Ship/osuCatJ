package App;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class Console extends OutputStream {

    private TextArea output;

    Console(TextArea ta) {
        this.output = ta;
    }

    @Override
    public void write(int i) throws IOException {
        Platform.runLater(()->output.appendText(String.valueOf((char) i)));
        output.setScrollTop(Double.MAX_VALUE);
    }
}
