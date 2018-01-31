package assignment1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class HTTPClient {

	public static void main(String[] args) {
		Socket TCPSocket = null;
		DataOutputStream os = null;
		DataInputStream is = null;

		try {
			TCPSocket = new Socket("hostname", 8007);
			os = new DataOutputStream(TCPSocket.getOutputStream());
			is = new DataInputStream(TCPSocket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: hostname");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: Echoserver hostname");
		}

		if (TCPSocket != null && os != null && is != null) {
			try {

				os.writeBytes("GET / HTTP/1.1\n");
				os.writeBytes("Host: hostname\n");
				os.writeBytes("\n.\n");
				BufferedReader br = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));
				String t;
				while ((t = br.readLine()) != null)
					System.out.println(t);
				
				br.close();
				os.close();
				is.close();
				TCPSocket.close();
				
			} catch (UnknownHostException e) {
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}
}
