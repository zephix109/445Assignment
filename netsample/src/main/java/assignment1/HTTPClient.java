package assignment1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class HTTPClient {
	

	public static void main(String[] args) {
        Socket smtpSocket = null;  
        DataOutputStream os = null;
        DataInputStream is = null;

        try {
            smtpSocket = new Socket("EchoServer hostname", 8007);
            os = new DataOutputStream(smtpSocket.getOutputStream());
            is = new DataInputStream(smtpSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: Echoserver hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }
        
		    if (smtpSocket != null && os != null && is != null) {
		            try {
		// The capital string before each colon has a special meaning to SMTP
		// you may want to read the SMTP specification, RFC1822/3
		        os.writeBytes("HELO\n");    
		                os.writeBytes("MAIL From: k3is@fundy.csd.unbsj.ca\n");
		                os.writeBytes("RCPT To: k3is@fundy.csd.unbsj.ca\n");
		                os.writeBytes("DATA\n");
		                os.writeBytes("From: k3is@fundy.csd.unbsj.ca\n");
		                os.writeBytes("Subject: testing\n");
		                os.writeBytes("Hi there\n"); // message body
		                os.writeBytes("\n.\n");
		        os.writeBytes("QUIT");
		// keep on reading from/to the socket till we receive the "Ok" from SMTP,
		// once we received that then we want to break.
		                String responseLine;
		                while ((responseLine = is.readLine()) != null) {
		                    System.out.println("Server: " + responseLine);
		                    if (responseLine.indexOf("Ok") != -1) {
		                      break;
		                    }
		                }
		// clean up:
		// close the output stream
		// close the input stream
		// close the socket
		        os.close();
		                is.close();
		                smtpSocket.close();   
		            } catch (UnknownHostException e) {
		                System.err.println("Trying to connect to unknown host: " + e);
		            } catch (IOException e) {
		                System.err.println("IOException:  " + e);
		            }
		        }
		    }           
	}

