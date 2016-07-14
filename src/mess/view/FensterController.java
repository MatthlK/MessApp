package mess.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import mess.Main;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class FensterController implements Initializable {

    private Main main;
    private Rectangle2D screen = Screen.getPrimary().getBounds();
    private Stage stage;
    private double menueHoehe;
    private double scale = 0.5;
    private double kalibrierFaktor;
    private String einheit;
    private int klicks;
    private Point2D[] p;
    private ArrayList<Line> l;
    private Circle[] circle;
    private Text[] t;
    private ObservableList<String> liste;
    private DecimalFormat df3;
    private DecimalFormat df2;

    //fxid Elemente
    @FXML
    private Pane hauptPane;
    @FXML
    private Pane paneUnten;
    @FXML
    private ImageView bildView;
    @FXML
    private Label labelAusgabe;
    @FXML
    private Label labelKalibrierung;
    @FXML
    private TextField textFeldLaenge;
    @FXML
    private TextField textFeldEinheit;
    @FXML
    private ListView<String> listView;

    public FensterController() {
        klicks = 0;
        p = new Point2D[10];
        l = new ArrayList<>();
        circle = new Circle[10];
        t = new Text[10];
        liste = FXCollections.observableArrayList();
        df3 = new DecimalFormat("####0.000");
        df2 = new DecimalFormat("####0.00");
    }

    //Initialize Block - Override, da Initializable implementiert
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bildView.setImage(new Image("mess/view/drop.png", 600, 300, true, true));
        setContextMenu();
        hauptPane.heightProperty().addListener(changed -> {
            setClear();
        });
        hauptPane.widthProperty().addListener(changed -> {
            setClear();
        });
    }


    @FXML
    public void setKalibrierung() {
        if (p[1] != null) {
            try {
                einheit = textFeldEinheit.getText();
                kalibrierFaktor = Double.parseDouble(textFeldLaenge.getText()) / p[0].distance(p[1]);
                setLabelAusgabe("Bild ist kalibriert. Messen ist möglich durch Anklicken von zwei Punkten im Bild.");
            } catch (NumberFormatException e) {
                setLabelAusgabe("Bitte Zahl eingeben. Dezimaltrenner ist Punkt!");
            }
            if (klicks >= 3 && kalibrierFaktor != 0.0) {
                setLabelKalibrierung("" + df3.format(kalibrierFaktor) + " " + einheit + "/Pixel");
                liste.clear();
                if (klicks >= 3)
                    liste.add("a1 -> a2:  " + df2.format(p[3].distance(p[2]) * kalibrierFaktor) + " " + einheit);
                if (klicks >= 5)
                    liste.add("b1 -> b2:  " + df2.format(p[5].distance(p[4]) * kalibrierFaktor) + " " + einheit);
                if (klicks >= 7)
                    liste.add("c1 -> c2:  " + df2.format(p[7].distance(p[6]) * kalibrierFaktor) + " " + einheit);
                if (klicks >= 9)
                    liste.add("d1 -> d2:  " + df2.format(p[9].distance(p[6]) * kalibrierFaktor) + " " + einheit);
            }
        } else {
            setLabelAusgabe("Erst bekannte Länge durch zwei Punkte definieren.");
        }

    }

    private void setLabelAusgabe(String ausgabeText) {
        labelAusgabe.setText(ausgabeText);
    }

    private void setLabelKalibrierung(String kalibrierText) {
        labelKalibrierung.setText(kalibrierText);
    }

    private void setListView() {
        listView.setItems(liste);
    }

    @FXML
    public void setClear() {
        liste.clear();
        p = new Point2D[10];
        hauptPane.getChildren().removeAll(circle);
        hauptPane.getChildren().removeAll(l);
        hauptPane.getChildren().removeAll(t);
        setLabelKalibrierung("nicht kalibriert");
        textFeldLaenge.setText("Länge");
        textFeldEinheit.setText("mm");
        kalibrierFaktor = 0;
        klicks = 0;
        setLabelAusgabe("Bild öffnen/einfügen und bekannte Länge durch zwei Punkte C1/C2 definieren.");
    }

    //Punkte - Linien erzeugen - TODO evtl. auslagern in Modell??
    @FXML
    public void setMousePressed(MouseEvent m) {
        if (m.getButton() == MouseButton.PRIMARY) {
            if (klicks <= 9) {
                p[klicks] = new Point2D(m.getX(), m.getY());
                if (klicks < 2) {
                    t[klicks] = new Text(p[klicks].getX(), p[klicks].getY() - 7, "C" + (klicks + 1));
                    setLabelAusgabe("Bekannte Länge über die Punkte C1 -> C2 festlegen.");
                    if (klicks == 1) {
                        l.add(0, new Line(p[klicks].getX(), p[klicks].getY(), p[klicks - 1].getX(), p[klicks - 1].getY()));
                        hauptPane.getChildren().add(l.get(0));
                    }
                } else if (klicks <= 3) {
                    t[klicks] = new Text(p[klicks].getX(), p[klicks].getY() - 7, "a" + (klicks - 1));
                    if (klicks == 3) {
                        liste.add("a1 -> a2:  " + df2.format(p[klicks].distance(p[klicks - 1]) * kalibrierFaktor) + " " + einheit);
                        l.add(1, new Line(p[klicks].getX(), p[klicks].getY(), p[klicks - 1].getX(), p[klicks - 1].getY()));
                        hauptPane.getChildren().add(l.get(1));
                        setLabelAusgabe("Länge a1 -> a2 definiert.");
                    }
                } else if (klicks <= 5) {
                    t[klicks] = new Text(p[klicks].getX(), p[klicks].getY() - 7, "b" + (klicks - 3));
                    if (klicks == 5) {
                        liste.add("b1 -> b2:  " + df2.format(p[klicks].distance(p[klicks - 1]) * kalibrierFaktor) + " " + einheit);
                        l.add(2, new Line(p[klicks].getX(), p[klicks].getY(), p[klicks - 1].getX(), p[klicks - 1].getY()));
                        hauptPane.getChildren().add(l.get(2));
                        setLabelAusgabe("Länge b1 -> b2 definiert.");
                    }
                } else if (klicks <= 7) {
                    t[klicks] = new Text(p[klicks].getX(), p[klicks].getY() - 7, "c" + (klicks - 5));
                    if (klicks == 7) {
                        liste.add("c1 -> c2:  " + df2.format(p[klicks].distance(p[klicks - 1]) * kalibrierFaktor) + " " + einheit);
                        l.add(3, new Line(p[klicks].getX(), p[klicks].getY(), p[klicks - 1].getX(), p[klicks - 1].getY()));
                        hauptPane.getChildren().add(l.get(3));
                        setLabelAusgabe("Länge c1 -> c2 definiert.");
                    }
                } else {
                    t[klicks] = new Text(p[klicks].getX(), p[klicks].getY() - 7, "d" + (klicks - 7));
                    if (klicks == 9) {
                        liste.add("d1 -> d2:  " + df2.format(p[klicks].distance(p[klicks - 1]) * kalibrierFaktor) + " " + einheit);
                        l.add(4, new Line(p[klicks].getX(), p[klicks].getY(), p[klicks - 1].getX(), p[klicks - 1].getY()));
                        hauptPane.getChildren().add(l.get(4));
                        setLabelAusgabe("Länge d1 -> d2 definiert.");
                    }
                }
                l.forEach(linie -> {
                    linie.setStroke(Color.RED);
                    linie.setStrokeWidth(2);
                });
                circle[klicks] = new Circle(p[klicks].getX(), p[klicks].getY(), 2.0, Color.RED);
                hauptPane.getChildren().add(circle[klicks]);
                t[klicks].setFill(Color.YELLOW);
                t[klicks].setFont(Font.font("Verdana", FontWeight.BOLD, 13));
                hauptPane.getChildren().add(t[klicks]);
            }
            klicks++;
            setListView();
        }
    }

    @FXML
    public void setOeffnen() {
        menueHoehe = paneUnten.getHeight() + 50;
        bildView.fitWidthProperty().bind(stage.widthProperty());
        bildView.fitHeightProperty().bind(stage.heightProperty().subtract(menueHoehe));
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Bilddatei öffnen");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg", "*.bmp", "*.tif"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                Image bild = new Image(selectedFile.toURI().toURL().toExternalForm(), true);
                bildView.setImage(bild);
                setScaleFile(selectedFile);
                setLabelAusgabe("Bild geladen. Bereit zum Kalibrieren. Oder neues Bild einfügen.");
            } catch (Exception exc) {
                setLabelAusgabe("Bild " + selectedFile + " konnte nicht geöffnet werden.");
            }
        }
    }

    @FXML
    public void setCopyPasteKey(KeyEvent key) {
        final KeyCombination INSCOMB = new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN);
        final KeyCombination COPCOMB = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
        if (INSCOMB.match(key)) {
            setPaste();
        } else if (COPCOMB.match(key)) {
            setCopy();
        }
    }

    @FXML
    public void setPaste() {
        menueHoehe = paneUnten.getHeight() + 50;
        bildView.fitWidthProperty().bind(stage.widthProperty());
        bildView.fitHeightProperty().bind(stage.heightProperty().subtract(menueHoehe));
        Clipboard cb = Clipboard.getSystemClipboard();
        if (cb.hasImage()) {
            Image bild = cb.getImage();
            bildView.setImage(bild);
            setScale(bild);
            setLabelAusgabe("Bild eingefügt. Bereit zum Kalibrieren. Oder neues Bild einfügen.");
        }
    }

    @FXML
    public void setCopy() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putImage(bildView.getImage());
        clipboard.setContent(content);
    }

    //Drag und Drop Methode - nötig!
    @FXML
    public void setDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasImage() || db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        }
    }

    //Drag und Drop Methode
    @FXML
    public void setDropped(DragEvent event) {
        menueHoehe = paneUnten.getHeight() + 50;
        Dragboard db = event.getDragboard();
        bildView.fitWidthProperty().bind(stage.widthProperty());
        bildView.fitHeightProperty().bind(stage.heightProperty().subtract(menueHoehe));
        if (db.hasImage()) {
            Image bild = db.getImage();
            bildView.setImage(bild);
            setScale(bild);
            event.setDropCompleted(true);
            event.consume();
        } else if (db.hasFiles()) {
            db.getFiles().forEach(file -> {
                String mimetype = new MimetypesFileTypeMap().getContentType(file);
                String type = mimetype.split("/")[0];
                if (type.equals("image")) {
                    try {
                        Image bild = new Image(file.toURI().toURL().toExternalForm(), true);
                        bildView.setImage(bild);
                        setScaleFile(file);
                        setLabelAusgabe("Bild geladen. Bereit zum Kalibrieren. Oder neues Bild einfügen.");
                    } catch (Exception exc) {
                        setLabelAusgabe("Bild konnte nicht geöffnet werden.");
                    }
                } else {
                    setLabelAusgabe("Eingefügte Datei ist kein Bild.");
                }
            });
            event.setDropCompleted(true);
            event.consume();
        }
    }

    //Kontextmenü für Kopieren und Einfügen
    private void setContextMenu() {
        final ContextMenu cm = new ContextMenu();
        MenuItem cmItem1 = new MenuItem("Kopieren");
        MenuItem cmItem2 = new MenuItem("Einfügen");
        cmItem1.setOnAction(
                (ActionEvent e) -> setCopy());
        cmItem2.setOnAction(
                (ActionEvent e) -> setPaste());
        cm.getItems().add(cmItem1);
        cm.getItems().add(cmItem2);
        bildView.addEventHandler(MouseEvent.MOUSE_CLICKED,
                (MouseEvent e) -> {
                    if (e.getButton() == MouseButton.SECONDARY)
                        cm.show(bildView, e.getScreenX(), e.getScreenY());
                });
    }

    //Skalierung Setzen wenn Quelle Bild
    private void setScale(Image bild) {
        if ((bild.getHeight() >= screen.getHeight() - menueHoehe))
            scale = (screen.getHeight() - menueHoehe - 100) / bild.getHeight();
        if (bild.getHeight() < screen.getHeight() - menueHoehe)
            scale = 1.0;
        if (bild.getHeight() < screen.getHeight() / 2 - menueHoehe)
            scale = 1.5;
        if (bild.getHeight() < 400)
            scale = 2.0;
        stage.setWidth(bild.getWidth() * scale);
        stage.setHeight(bild.getHeight() * scale + menueHoehe);
    }

    //Skalierung setzen wenn Quelle file (TODO nur mit ImageIcon hinbekommen)
    private void setScaleFile(File file) {
        ImageIcon sizeBild = new ImageIcon(file.getAbsolutePath());
        if ((sizeBild.getIconHeight() >= screen.getHeight() - menueHoehe))
            scale = (screen.getHeight() - menueHoehe - 100) / sizeBild.getIconHeight();
        if (sizeBild.getIconHeight() < screen.getHeight() - menueHoehe)
            scale = 1.0;
        if (sizeBild.getIconHeight() < screen.getHeight() / 2 - menueHoehe)
            scale = 1.5;
        if (sizeBild.getIconHeight() < 400)
            scale = 2.0;
        stage.setWidth(sizeBild.getIconWidth() * scale);
        stage.setHeight(sizeBild.getIconHeight() * scale + menueHoehe);
    }

    //Beenden
    @FXML
    public void setExit() {
        System.exit(0);
    }

    //Info - Copyright
    @FXML
    public void setAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add("mess/view/DarkTheme.css");
        alert.setTitle("MessApp");
        alert.setHeaderText("MessApp - Version 0.11");
        alert.setContentText("Zum Betrachten von Bildern per Drag and Drop aus Dateisystem, " +
                "Internet oder Anwendungen.\n" + "Ermöglicht die Durchführung von Messungen bei enthaltenem Vergleichsmaßsstab." +
                "\n\nCopyright © 1995-2016 Matthias Kemmel - Alle Rechte vorbehalten.");
        alert.showAndWait();
    }


    //Setze Verknüpfung zu Main und zur primaryStage
    public void setMain(Main main) {
        this.main = main;
        this.stage = main.getStage();
    }
}