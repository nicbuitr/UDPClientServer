package client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import fileEvent.FileEvent;

public class Client {
	
	public final String DEFAULT_SERVER_IP = "localHost";
	public final int DEFAULT_PORT = 9876;
	public final int DEFAULT_BUFFER_SIZE= 1024;
	public final String CRLF = "\r\n";
	
	private DatagramSocket socket = null;
	private FileEvent event = null;
	private String sourceFilePath = "";
	private String destinationPath = "./udpUploads/";
	
	private String serverIP = "localHost";
	private int port = 9876;
	private int bufferSize = 1024;
	
	public Client() {
		
		serverIP = DEFAULT_SERVER_IP;
		port = DEFAULT_PORT;
		bufferSize = DEFAULT_BUFFER_SIZE;		

	}
	
	public boolean setConfig(String pIP, int pPort, int pBufferSize) {
		serverIP = pIP;
		port = pPort;
		bufferSize = pBufferSize;
		
		return true;
	}

	public boolean createConnection(String pSourceFilePath) {
		
		sourceFilePath = pSourceFilePath;
		
		try {

			socket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(serverIP);
			
			File file = new File(sourceFilePath);
			
			int numberOfSegments = (int) file.length()/bufferSize;
			System.out.println("File Size: " + file.length() );
			System.out.println("Number of Segments: " + numberOfSegments);
			
			// Create the MD5 hash of the file on Client
			MessageDigest md = MessageDigest.getInstance("MD5");
			InputStream is = Files.newInputStream(Paths.get(file.getAbsolutePath()));
			new DigestInputStream(is, md); 
			byte[] digest = md.digest();
			
			
			int fileSegmentNumber = 0;
			
			while (fileSegmentNumber <= numberOfSegments){
			
				event = getFileEvent(file, numberOfSegments, fileSegmentNumber, bufferSize);
				event.setMd5Hash(digest);
				
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ObjectOutputStream os = new ObjectOutputStream(outputStream);
				os.writeObject(event);
				os.close();
				outputStream.close();
	
				DatagramPacket sendPacket = new DatagramPacket(outputStream.toByteArray(), outputStream.toByteArray().length, IPAddress, port);
				socket.send(sendPacket);
				
				System.out.println("CLIENT: Sent Packet # " + fileSegmentNumber + "/" + numberOfSegments + " Packet Data Size " + event.getFileSegmentSize());
				fileSegmentNumber++;
				//TimeUnit.MILLISECONDS.sleep(20);
			}
						
			System.out.println("File sent to Server");
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public FileEvent getFileEvent(File file, int pTotalFileSegments, int pFileSegmentNumber, int pBufferSize) {
		
		
		FileEvent fileEvent = new FileEvent();
		fileEvent.setTotalFileSegments(pTotalFileSegments);
		fileEvent.setFileSegmentNumber(pFileSegmentNumber);
		fileEvent.setTotalFileSize(file.length());
		fileEvent.setFilename(file.getName());
		fileEvent.setSourceDirectory(sourceFilePath);
		
		if (file.isFile()) {
			try {
				
				fileEvent.setDestinationDirectory(new File(destinationPath).getCanonicalPath()+"\\");
				
				DataInputStream diStream = new DataInputStream(new FileInputStream(file));
				
				byte[] fileBytes = new byte[(int) file.length()];
				
				int read = 0;
				int numRead = 0;
				while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
					read = read + numRead;
				}				
				
				int readFrom = pFileSegmentNumber * (pBufferSize);
				int readTo = ((pFileSegmentNumber + 1) * pBufferSize) <= (int) file.length()?((pFileSegmentNumber + 1) * pBufferSize):(int) file.length();
				int segmentLength = readTo - readFrom;
				fileEvent.setFileSegmentSize(segmentLength);
				fileEvent.setSegmentStartIndex(readFrom);
				fileEvent.setSegmentEndIndex(readTo);
				fileEvent.setFileSegmentData(Arrays.copyOfRange(fileBytes, readFrom, readTo));
				fileEvent.setStatus("Success");
				diStream.close();
				
			} catch (Exception e) {
				e.printStackTrace();
				fileEvent.setStatus("Error");
			}
		} else {
			System.out.println("path specified is not pointing to a file");
			fileEvent.setStatus("Error");
		}
		return fileEvent;
	}
}
