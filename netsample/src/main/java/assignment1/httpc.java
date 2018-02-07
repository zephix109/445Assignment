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

public class httpc {

	private Socket TCPSocket;
	private DataOutputStream os;
	private DataInputStream is;

	public httpc(String ip, int port) {
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
		
		OptionParser parser = new OptionParser();
		String host = "httpbin.org";
		Integer port = 80;

//		parser.acceptsAll(asList("host", "h"), "host").withOptionalArg().defaultsTo("httpbin.org");
		parser.acceptsAll(asList("port", "p"), "port").withOptionalArg().defaultsTo(port.toString());
		
		parser.acceptsAll(asList("get", "GET", "Get"), "get:").withRequiredArg();
		parser.acceptsAll(asList("post", "POST", "Post"), "post:").withRequiredArg();
		parser.accepts("v", "verbose");
//		parser.accepts("h", "header").withOptionalArg(); 

		OptionSet opts = parser.parse(args);

		

		if(opts.has("get")) {
//			if(opts.valuesOf("get").contains("'http://")) //httpbin.org/get?course=networking&assignment=1'"))
//				System.out.println("WOO");
//			else
//				System.out.println("nooooo");

//			if(opts.valuesOf("get").contains("'http://")) {
//				int index = opts.valuesOf("get").indexOf("'http://");
//				
//			}

			//Fetch string from list that contains http://
			String URL = (String) opts.valuesOf("get").get(opts.valuesOf("get").indexOf(new Object() {
			    @Override
			    public boolean equals(Object obj) {
			        return obj.toString().contains("'http://");
			    }
			}));
			System.out.println(URL);
			
//			String host = (String) opts.valueOf("host");
//			int port = Integer.parseInt((String) opts.valueOf("port"));
			
			httpc client = new httpc(host, port);
			
			client.getRequest(host, "/get?course=networking&assignment=1");
		} else if(opts.has("post")) {
//			client.postRequest(host, "get?course=networking&assignment=1", "Hello, server");
		} else {
			System.out.println("No get or post request");
		}
	}

	public void getRequest(String domain, String location) {
		if (TCPSocket != null && os != null && is != null) {
			try {
				os.writeBytes("GET " + location + " HTTP/1.1\r\n");
				os.writeBytes("Host: " + domain + "\r\n\r\n");
				BufferedReader br = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));
				String t;

				while ((t = br.readLine()) != null) {
					System.out.println(t);
				}

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
				os.writeBytes("POST " + location + " HTTP/1.0\n");
				os.writeBytes("Host: " + domain + "\n");
				os.writeBytes("\n" + data + "\n");
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
