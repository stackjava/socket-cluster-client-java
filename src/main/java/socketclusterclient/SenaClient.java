package socketclusterclient;

import java.text.SimpleDateFormat;
import java.util.*;
import com.neovisionaries.ws.client.*;
import io.github.sac.*;

public class SenaClient {
	public static void main(String[] args) throws Exception {
		// Create a socket instance
		String url = "ws://localhost:8000/socketcluster/";

		Socket socket = new Socket(url);
		socket.setListener(new BasicListener() {

			public void onConnected(Socket socket, Map<String, List<String>> headers) {
				System.out.println("Connected to endpoint");
			}

			public void onDisconnected(Socket socket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
					boolean closedByServer) {
				System.out.println("Disconnected from end-point");
			}

			public void onConnectError(Socket socket, WebSocketException exception) {
				System.out.println("Got connect error " + exception);
			}

			public void onSetAuthToken(String token, Socket socket) {
				System.out.println("Token is " + token);
			}

			@Override
			public void onAuthentication(Socket socket, Boolean status) {
			}
		});

		// By default logging of messages is enabled ,to disable
		socket.disableLogging();
		// This will set automatic-reconnection to server with delay of 2
		// seconds and repeating it for 30 times
		socket.setReconnection(new ReconnectStrategy().setDelay(2000).setMaxAttempts(30));
		// This will send websocket handshake request to socketcluster-server
		socket.connect();

		Socket.Channel channel = socket.createChannel("sena");
		channel.subscribe(new Ack() {
			public void call(String channelName, Object error, Object data) {
				if (error == null) {
					System.out.println("Listen channel sena: " + data);
				}
			}
		});

		channel.onMessage(new Emitter.Listener() {
			public void call(String channelName, Object object) {
				System.out.println("Message from channel " + channelName + ": " + object.toString());
			}
		});
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
					socket.publish("kai", "sena - " + new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date()));
				}
			}
		});
		t.start();
	}
}
