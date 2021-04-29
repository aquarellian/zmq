import org.zeromq.*;
import zmq.util.Z85;

public class ZMQCommunicate {
    private static ZMQ.Socket createPublisher(String url) {
        ZContext ctx = new ZContext();

        ZAuth auth = new ZAuth(ctx);
        auth.setVerbose(true);
        auth.configureCurve(ZAuth.CURVE_ALLOW_ANY);

        ZMQ.Socket client = ctx.createSocket(SocketType.PUB);
        ZCert clientCert = new ZCert();
        client.setCurvePublicKey(clientCert.getPublicKey());
        client.setCurveSecretKey(clientCert.getSecretKey());
        client.setCurveServerKey(Z85.decode(".HgpMsGxpb?n!Yub))n#+{YzLL{&)7D$icCIx6#?"));
        client.connect(url);
        return client;
    }

    private static ZMQ.Socket createSubscriber(String url) {
        ZContext ctx = new ZContext();

        ZAuth auth = new ZAuth(ctx);
        auth.setVerbose(true);
        auth.configureCurve(ZAuth.CURVE_ALLOW_ANY);

        ZMQ.Socket client = ctx.createSocket(SocketType.SUB);
        client.setZAPDomain("global".getBytes());
        client.setCurveServer(true);
        client.setCurvePublicKey(Z85.decode(".HgpMsGxpb?n!Yub))n#+{YzLL{&)7D$icCIx6#?"));
        client.setCurveSecretKey(Z85.decode("3*Kg{9Wy@Pvcy2TCLODMK74b2Df!H<xFx%aeU3^a"));
        client.bind(url);
        return client;
    }

    public static void main(String[] args) {
        ZMQ.Socket publisher = createPublisher("tcp://localhost:6005");
        ZMQ.Socket subscriber = createSubscriber("tcp://localhost:6005");
        subscriber.subscribe(new byte[0]);
        new Thread(() -> {
            while (true) {
                String s = new String(subscriber.recv(0));
                System.out.println("Received: " + s);
            }
        }).start();

        int i = 0;
        while (true) {
            String data = "Iteration " + i;
            publisher.send(data.getBytes());
            System.out.println("Sent: " + data);
            i++;
        }
    }
}
