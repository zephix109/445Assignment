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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class httpc {

	private Socket TCPSocket;
	private DataOutputStream os;
	private DataInputStream is;

	public httpc(String host, int port) {
		try {
			TCPSocket = new Socket(host, port);
			os = new DataOutputStream(TCPSocket.getOutputStream());
			is = new DataInputStream(TCPSocket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + host);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to server: " + host);
		}

	}

	public static void main(String[] args) {

		OptionParser parser = new OptionParser();
		String host = "httpbin.org";
		Integer port = 80;
		String url;
		String location;
		Boolean verbose = false;
		HashMap<String, String> headers = new HashMap<String, String>();

		args = formatHttpRequest(args);

		parser.acceptsAll(asList("host"), "host").withOptionalArg().defaultsTo("httpbin.org");
		parser.acceptsAll(asList("port", "p"), "port").withOptionalArg().defaultsTo(port.toString());
		parser.acceptsAll(asList("get", "GET", "Get"), "get");
		parser.acceptsAll(asList("post", "POST", "Post"), "post");
		parser.acceptsAll(asList("v", "verbose"), "verbose");
		parser.acceptsAll(asList("h", "header"), "header").withRequiredArg();
		//parser.acceptsAll(asList("http", "https"), "http").withOptionalArg();

		OptionSet opts = parser.parse(args);

		if (opts.has("get")) {
			if (opts.has("verbose")) {
				verbose = true;
				//System.out.println("verbose: " +verbose);
			}

			if (opts.has("header")) {
				for(Object obj : opts.valuesOf("header")) {
					String[] temp = obj.toString().split("=");
					headers.put(temp[0], temp[1]);
					//System.out.println(obj);
				}
			}

			url = fetchUrl(args);
			host = getHost(url);
			location = getUrlDetails(host, url);
			
			//System.out.println("url: " + url + "\nhost: " + host + "\nlocation: " + location);

			httpc client = new httpc(host, port);

			client.getRequest(host, location, verbose, headers);
		} else if (opts.has("post")) {
			 //client.postRequest(host, "get?course=networking&assignment=1", "Hello, server");
		} else {
			System.out.println("No get or post request");
		}
	}

	/**
	 * HTTP Get request through custom TCP socket
	 * 
	 * Note: Each name=value header needs to be preceded by a -h
	 * 
	 * @param domain
	 * @param location
	 * @param verbose
	 * @param headers
	 */
	public void getRequest(String domain, String location, Boolean verbose, HashMap<String,String> headers) {
		if (TCPSocket != null && os != null && is != null) {
			try {
				os.writeBytes("GET " + location + " HTTP/1.1\r\n");
				if(headers.isEmpty()) {
					os.writeBytes("Host: " + domain + "\r\n\r\n");
				} else {
					//Add headers from HashMap to GET request
					os.writeBytes("Host: " + domain + "\r\n");
					Iterator<Entry<String, String>> it = headers.entrySet().iterator();
				    while (it.hasNext()) {
				        @SuppressWarnings("rawtypes")
						Map.Entry pair = (Map.Entry)it.next();
				        os.writeBytes(pair.getKey() + ": " + pair.getValue() + "\r\n");
				        it.remove();
				    }
				    os.writeBytes("\r\n");
				}
					
				BufferedReader br = new BufferedReader(new InputStreamReader(TCPSocket.getInputStream()));
				String t;
				while ((t = br.readLine()) != null) {
					//Do not print headers if not verbose
					if(!verbose && !t.startsWith("{")) {
						continue;
					}
					verbose = true;
					System.out.println(t);
				}

				br.close();
				
			} catch (UnknownHostException e) {
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			} finally {
				if (is != null) try { is.close(); } catch (IOException e) {} 
	            if (os != null) try {os.close(); } catch (IOException e) {}
	            if (TCPSocket != null) try { TCPSocket.close(); } catch (IOException e) {}
			}
		}
	}

	public void postRequest(String domain, String location, String data) {
		if (TCPSocket != null && os != null && is != null) {
			try {
				os.writeBytes("POST " + location + " HTTP/1.1\n");
				os.writeBytes("Host: " + domain + "\n");
				os.writeBytes("\n" + data + "\r\n\r\n");
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

	/**
	 * Formats the base http request to function with OptionParser
	 * 
	 * @param args
	 * @return
	 */
	public static String[] formatHttpRequest(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("'"))
				arg = arg.replaceAll("'", "");
			if (!arg.startsWith("-"))
				arg = "-" + arg;
			//System.out.println(arg);
		}

		return args;
	}

	/**
	 * Ingests and parses the url for the host and returns it
	 * 
	 * @param url
	 * @return
	 */
	public static String getHost(String url) {
		String formattedUrl = "";
		try {
			formattedUrl = url.substring(url.indexOf("http://") + 7);
			formattedUrl = formattedUrl.substring(0, formattedUrl.indexOf("/"));
		} catch (NullPointerException e) {
			System.out.println("Malformed url");
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println("Malformed url index");
		}
		return formattedUrl;
	}
	
	/**
	 * Finds the argument that holds the url and returns it
	 * 
	 * @param args
	 * @return
	 */
	public static String fetchUrl(String[] args) {
		for(String arg : args) {
			if(arg.contains("http://")) {
				return arg;
			}
		}
		return "";
	}
	
	/**
	 * Parses the raw url and returns just the location
	 * 
	 * @param host
	 * @param url
	 * @return
	 */
	public static String getUrlDetails(String host, String url) {
		String urlDetails = "";
		try {
			urlDetails = url.substring(url.indexOf(host) + host.length(), url.length() - 1);
		}catch (Exception e) {
			System.out.println("Url format exception");
		}
		return urlDetails;
	}
}