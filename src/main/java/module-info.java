module org.example.poprojectgalaxyv7 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.poprojectgalaxyv7 to javafx.fxml;
    exports org.example.poprojectgalaxyv7;
}