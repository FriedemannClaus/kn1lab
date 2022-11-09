import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Die "Klasse" Sender liest einen String von der Konsole und zerlegt ihn in einzelne Worte. Jedes Wort wird in ein
 * einzelnes {@link Packet} verpackt und an das Medium verschickt. Erst nach dem Erhalt eines entsprechenden
 * ACKs wird das nächste {@link Packet} verschickt. Erhält der Sender nach einem Timeout von einer Sekunde kein ACK,
 * überträgt er das {@link Packet} erneut.
 */
public class Sender {
    /**
     * Hauptmethode, erzeugt Instanz des {@link Sender} und führt {@link #send()} aus.
     *
     * @param args Argumente, werden nicht verwendet.
     */
    public static void main(String[] args) {
        Sender sender = new Sender();
        try {
            sender.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Erzeugt neuen Socket. Liest Text von Konsole ein und zerlegt diesen. Packt einzelne Worte in {@link Packet}
     * und schickt diese an Medium. Nutzt {@link SocketTimeoutException}, um eine Sekunde auf ACK zu
     * warten und das {@link Packet} ggf. nochmals zu versenden.
     *
     * @throws IOException Wird geworfen falls Sockets nicht erzeugt werden können.
     */
    private void send() throws IOException {
        //Text einlesen und in Worte zerlegen
        Scanner scanner = new Scanner(System.in);
        String eingabe = scanner.nextLine();
        eingabe = eingabe + " EOT";
        String[] worte = eingabe.split("(?= )");
        worte[worte.length - 1] = worte[worte.length - 1].trim();


        // Socket erzeugen auf Port 9998 und Timeout auf eine Sekunde setzen
        DatagramSocket clientSocket = new DatagramSocket(9998);
        clientSocket.setSoTimeout(1000);
        //clientSocket.connect(InetAddress.getLocalHost(), 9997);

        // Iteration über den Konsolentext
        int i = 0;
        int sequenz = 1;
        while (i < worte.length) {
            // Paket an Port 9997 senden
            byte[] payload = worte[i].getBytes(Charset.defaultCharset());
            Packet packet = new Packet(sequenz, sequenz + payload.length, false, payload);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(packet);
            byte[] sendData = b.toByteArray();
            DatagramPacket datagramPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(), 9997);
            clientSocket.send(datagramPacket);

            try {
                // Auf ACK warten und erst dann Schleifenzähler inkrementieren
                byte[] buf = new byte[256];
                DatagramPacket receivePacketRaw = new DatagramPacket(buf, buf.length);
                clientSocket.receive(receivePacketRaw);
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(receivePacketRaw.getData()));
                Packet packetIn = (Packet) is.readObject();
                if (packetIn.isAckFlag()) {
                    i++;
                    sequenz = sequenz + payload.length;
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                System.out.println("Receive timed out, retrying...");
            }
        }

        // Wenn alle Packete versendet und von der Gegenseite bestätigt sind, Programm beenden
        clientSocket.close();

        if (System.getProperty("os.name").equals("Linux")) {
            clientSocket.disconnect();
        }

        System.exit(0);
    }
}
