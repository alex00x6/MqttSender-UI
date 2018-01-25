package mqtt;

import com.amazonaws.services.iot.client.*;

import java.util.concurrent.ThreadLocalRandom;

public class MQTTConnector {
    public String clientEndpoint;
    public String topic;
    public String payload;
    public String awsAccessKeyId;
    public String awsSecretAccessKey;
    public String sessionToken;
    public int openConnectionTimeMs;
    public int connectionRetries;

    public void mqttSubscribe() {
        printClientInfo();
        AWSIotMqttClient awsIotClient = createClient();
        awsIotClient.setConnectionTimeout(openConnectionTimeMs);
        awsIotClient.setKeepAliveInterval(openConnectionTimeMs);
        awsIotClient.setMaxConnectionRetries(connectionRetries);
        try {
            awsIotClient.connect();
            AWSIotTopic topicIoT = new TestTopicListener(topic, AWSIotQos.QOS0);
            awsIotClient.subscribe(topicIoT);
            System.out.println("Subscriber: " + awsIotClient.getConnectionStatus().toString());
            if (awsIotClient.getConnectionStatus().equals(AWSIotConnectionStatus.CONNECTED)) {
                sleep(openConnectionTimeMs);
                awsIotClient.unsubscribe(topic);
                awsIotClient.disconnect();
            }
        } catch (AWSIotException e) {
            e.printStackTrace();
        }
    }

    public void mqttPublish() {
        printClientInfo();
        AWSIotMqttClient awsIotClient = createClient();
        awsIotClient.setMaxConnectionRetries(connectionRetries);
        try {
            if (awsIotClient.getConnectionStatus().equals(AWSIotConnectionStatus.DISCONNECTED)) {
                awsIotClient.connect();
            }
            awsIotClient.publish(topic, payload);
            if (awsIotClient.getConnectionStatus().equals(AWSIotConnectionStatus.CONNECTED)) {
                awsIotClient.disconnect();
            }
        } catch (AWSIotException e) {
            e.printStackTrace();
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private AWSIotMqttClient createClient() {
        String clientId = ThreadLocalRandom.current().nextInt(900, 900000 + 1) + "xe";
        if (sessionToken == null || sessionToken.equals("") || sessionToken.isEmpty()) {
            System.out.println("Launching IoT client without session token.");
            System.out.println("=========================");
            return new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey);
        } else {
            System.out.println("Launching IoT client using session token.");
            System.out.println("=========================");
            return new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey, sessionToken);
        }
    }

    private void printClientInfo() {
        System.out.println("=========================");
        System.out.println(clientEndpoint);
        System.out.println(topic);
        System.out.println(payload);
        System.out.println(awsAccessKeyId);
        System.out.println(awsSecretAccessKey);
        System.out.println(sessionToken);
        System.out.println("=========================");
    }
}
