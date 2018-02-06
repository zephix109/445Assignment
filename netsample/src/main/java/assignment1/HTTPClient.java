package assignment1;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class HTTPClient {
	
	private Socket TCPSocket;
	private DataOutputStream os;
	private DataInputStream is;
	
	public HTTPClient(String ip, int port) {
		try {
			TCPSocket = new Socket(ip, port);
			os = new DataOutputStream(TCPSocket.getOutputStream());
			is = new DataInputStream(TCPSocket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + ip);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: Echoserver " + ip);
		}
		
	}

	public static void main(String[] args) {
		
		HTTPClient client = new HTTPClient("localhost", 8007);

//		client.getRequest("localhost", "/");		
		client.postRequest("localhost", "/", "Hello, server");
		
	}
	
	public void getRequest(String domain, String location) {
		if (TCPSocket != null && os != null && is != null) {
			try {
				os.writeBytes("GET " + location + " HTTP/1.1\n");
				os.writeBytes("Host: " + domain + "\n");
				os.writeBytes("\n.\n");
				BufferedReader br = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));
				String output;				
				String t;
				
				while ((t = br.readLine()) != null) {
					System.out.println(t);
				}
				
				output = "hello";
				os.writeBytes(output);
				
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
	
	public void postRequest(String domain, String location, String data) {
		if (TCPSocket != null && os != null && is != null) {
			try {
				os.writeBytes("POST " + location + " HTTP/1.1\n");
				os.writeBytes("Host: " + domain + "\n");
				os.writeBytes("\n" + data +"\n");
				BufferedReader br = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));
				String output;				
				String t;
				
				while ((t = br.readLine()) != null) {
					System.out.println(t);
				}
				
				output = "hello";
				os.writeBytes(output);
				
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
