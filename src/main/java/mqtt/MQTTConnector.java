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
    public int openConnectionTimeMs ;
    public int connectionRetries;

    public void mqttSubscribe(){
        String clientId = ThreadLocalRandom.current().nextInt(900, 900000 + 1)+"xe";

        System.out.println("=========================");
        System.out.println(clientEndpoint);
        System.out.println(topic);
        System.out.println(payload);
        System.out.println(awsAccessKeyId);
        System.out.println(awsSecretAccessKey);
        System.out.println(sessionToken);
        System.out.println("=========================");

        AWSIotMqttClient awsIotClient;
        if (sessionToken==null || sessionToken.equals("") || sessionToken.isEmpty()){
            System.out.println("Launching IoT client without session token.");
            System.out.println("=========================");
            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey);
        }
        else{
            System.out.println("Launching IoT client using session token.");
            System.out.println("=========================");
            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey, sessionToken);
        }

        awsIotClient.setConnectionTimeout(openConnectionTimeMs);
        awsIotClient.setKeepAliveInterval(openConnectionTimeMs);
        awsIotClient.setMaxConnectionRetries(connectionRetries);

        try {
            awsIotClient.connect();
        } catch (AWSIotException e) {
            e.printStackTrace();
        }

        AWSIotTopic topicIoT = new TestTopicListener(topic, AWSIotQos.QOS0);

        try {
            awsIotClient.subscribe(topicIoT);
            System.out.println("Subscriber: "+awsIotClient.getConnectionStatus().toString());
        } catch (AWSIotException e) {
            e.printStackTrace();
        }

        if (awsIotClient.getConnectionStatus().equals(AWSIotConnectionStatus.CONNECTED)){
            sleep(openConnectionTimeMs);
            try {
                awsIotClient.unsubscribe(topic);
                awsIotClient.disconnect();
            } catch (AWSIotException e) {
                e.printStackTrace();
            }
        }
    }

    public void mqttPublish(){
        String clientId = ThreadLocalRandom.current().nextInt(900, 900000 + 1)+"Pe";

        System.out.println("=========================");
        System.out.println(clientEndpoint);
        System.out.println(topic);
        System.out.println(payload);
        System.out.println(awsAccessKeyId);
        System.out.println(awsSecretAccessKey);
        System.out.println(sessionToken);
        System.out.println("=========================");

        AWSIotMqttClient awsIotClient;
        if (sessionToken==null || sessionToken.equals("") || sessionToken.isEmpty()){
            System.out.println("Launching IoT client without session token.");
            System.out.println("=========================");
            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey);
        }
        else{
            System.out.println("Launching IoT client using session token.");
            System.out.println("=========================");
            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey, sessionToken);
        }
        awsIotClient.setMaxConnectionRetries(connectionRetries);

        try {
            if(awsIotClient.getConnectionStatus().equals(AWSIotConnectionStatus.DISCONNECTED)){
                awsIotClient.connect();
            }
            awsIotClient.publish(topic, payload);
            if (awsIotClient.getConnectionStatus().equals(AWSIotConnectionStatus.CONNECTED)){
                awsIotClient.disconnect();
            }
        } catch (AWSIotException e) {
            e.printStackTrace();
        }
    }

    public void sleep(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
