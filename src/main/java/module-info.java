module com.michaelmckibbin.viennaubhan {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.michaelmckibbin.viennaubahn to javafx.fxml;
    exports com.michaelmckibbin.viennaubahn;
}