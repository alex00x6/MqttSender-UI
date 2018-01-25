package app.view;

import app.MainApp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mqtt.MQTTConnector;

import java.io.OutputStream;
import java.io.PrintStream;

public class MainViewController {
    private MainApp mainApp;

    @FXML
    private ToggleGroup pubSub;
    @FXML
    private RadioButton subscribe;
    @FXML
    private RadioButton publish;
    @FXML
    private TextArea output;
    @FXML
    private Button doit;
    @FXML
    private TextField iotEndpoint;
    @FXML
    private TextField topic;
    @FXML
    private TextField payload;
    @FXML
    private TextField accessKeyId;
    @FXML
    private TextField secretAccessKey;
    @FXML
    private TextField sessionToken;
    @FXML
    private TextField subscriptionTimeout;
    @FXML
    private TextField maxConnectionRetries;

    public MainViewController() {

    }

    @FXML
    private void goLaunch() {
        //по нажатию кнопки запускаем main.java.main.java.mqtt клиент в отдельном потоке
        //делаем это для того, чтоб приложение не зависало, пока main.java.main.java.mqtt клиент не закончит
       new Thread(() -> {
            MQTTConnector mqttConnector = new MQTTConnector();
            mqttConnector.clientEndpoint = iotEndpoint.getText();
            mqttConnector.topic = topic.getText();
            mqttConnector.payload = payload.getText();
            mqttConnector.awsAccessKeyId = accessKeyId.getText();
            mqttConnector.awsSecretAccessKey = secretAccessKey.getText();
            mqttConnector.sessionToken = sessionToken.getText();
            mqttConnector.openConnectionTimeMs = Integer.parseInt(subscriptionTimeout.getText());
            mqttConnector.connectionRetries = Integer.parseInt(maxConnectionRetries.getText());
            if (publish.isSelected()) {
                mqttConnector.mqttPublish();
            }
            if (subscribe.isSelected()) {
                mqttConnector.mqttSubscribe();
            }
        }).start();
    }

    @FXML
    private void initialize() {
        //выводим консольные out и err в output field приложения
        OutputStream out = new Console(output);
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public class Console extends OutputStream {
        @Override
        public void write(int b) {
            appendText(String.valueOf((char) b));
        }

        private TextArea output;

        Console(TextArea ta) {
            this.output = ta;
        }

        void appendText(String str) {
            Platform.runLater(() -> output.appendText(str));
        }
    }
}
