package server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Arrays;

import fileEvent.FileEvent;

public class Server {
	private DatagramSocket socket = null;
	private FileEvent fileEvent = null;

	private int port = 9876;
	private int bufferSize = 32000; // Min = 576 bytes, theoretical Max 64000 bytes 
	
	//File where the segments will be appended to
	private byte[] incomingFile;
	private int numberOfPacketsExpected;
	private int numberOfPacketsReceived;
	
	public Server(int pPort, int pBufferSize) {
		
		if (pPort != 0) {
			port = pPort;
		}
		
		if (pBufferSize != 0) {
			bufferSize = pBufferSize;
		}

	}

	public void createAndListenSocket() {
		try {
			socket = new DatagramSocket(port);
			byte[] incomingData = new byte[(int) (bufferSize+550) ]; // 550 Additional buffer for the header of the message
			System.out.println("Server started and listening at port: " + port);
			System.out.println("Buffer Size: "+ incomingData.length);
			while (true) {
				DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				socket.receive(incomingPacket);
				
				byte[] data = incomingPacket.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				fileEvent = (FileEvent) is.readObject();
				
				if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
					System.out.println("Some issue happened while packing the data @ client side");
					System.exit(0);
				}
				
				/**
				 * Create a new byte array to start appending the file segments
				 *  @warning this only works for receiving 1 file at the time, not for
				 *  		 multiple simultaneous files sent, for the latter, thread handling
				 *  		 must be implemented.
				 */
				if (incomingFile == null) {
					incomingFile = new byte[(int) fileEvent.getTotalFileSize()];
					numberOfPacketsExpected = fileEvent.getTotalFileSegments();
					numberOfPacketsReceived = 0;
				}
				
				System.out.println("SERVER: Received file segment # " + fileEvent.getFileSegmentNumber()+"/"+fileEvent.getTotalFileSegments() + " Incoming Data Size " + incomingPacket.getLength() + " # of Packets Left: " + (numberOfPacketsExpected-numberOfPacketsReceived));
				
				createAndWriteFile(); // writing the file to hard disk
				numberOfPacketsReceived++;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createAndWriteFile() {
		try {
			String directory  = "./udpUploads/";
			String outputFile = new File(directory + fileEvent.getFilename()).getCanonicalPath();
			if (!new File(directory).exists()) {
				new File(directory).mkdirs();
			}
			
			boolean foundIncompleteFile = false;
			while (new File(outputFile).exists() && !foundIncompleteFile) {
				int fileCopyNumber = 1;
				//If the file is already complete then add next sequential to name
				if (new File(outputFile).length() == fileEvent.getTotalFileSize()){
					String fileName = outputFile;
					String nameLeft = fileName.split("\\.")[0];
					try {
						int previousNumber = Integer.parseInt(nameLeft.substring(nameLeft.length()-1));
						fileCopyNumber += previousNumber;
					}
					catch (Exception e){
						// If its not a number then proceed
					}
					
					if (fileCopyNumber == 1){
						nameLeft += "_" + fileCopyNumber;
					}
					else{
						nameLeft = nameLeft.split("_")[0] + "_" + fileCopyNumber;
					}
					
					String nameRight = fileName.split("\\.")[1];
					
					outputFile = nameLeft + "." + nameRight;
				}
				else {
					foundIncompleteFile = true;
				}
			}
			
			// Appends the byte segment to the file byte array
			for (int i = fileEvent.getSegmentStartIndex(), j = 0; i < fileEvent.getSegmentEndIndex(); i++, j++) {
				incomingFile[i] = fileEvent.getFileSegmentData()[j];		
			}
			
			// If all the segments are received then save the file
			if (fileEvent.getFileSegmentNumber() == fileEvent.getTotalFileSegments()){
				
				File dstFile = new File(outputFile);
				FileOutputStream fileOutputStream = null;
				fileOutputStream = new FileOutputStream(dstFile, true);
				fileOutputStream.write(incomingFile);
				fileOutputStream.flush();
				fileOutputStream.close();
								
				byte[] originFileHash = fileEvent.getMd5Hash();
				
				MessageDigest md = MessageDigest.getInstance("MD5");
				InputStream is = Files.newInputStream(Paths.get(dstFile.getAbsolutePath()));
				new DigestInputStream(is, md); 
				
				byte[] serverFileHash = md.digest();
				
				System.out.println("Hash Client == Hash Server? " + Arrays.equals(originFileHash, serverFileHash));				
			
				System.out.println("Output file : " + outputFile + " is successfully saved ");
				incomingFile = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int pPort = 0;
		int pBufferSize = 0;
		if (args.length > 0){
			pPort = Integer.parseInt(args[0]);
			if (args[1] != null) {
				pBufferSize = Integer.parseInt(args[1]);
			}
		}
		Server server = new Server(pPort, pBufferSize);
		server.createAndListenSocket();
	}
}
