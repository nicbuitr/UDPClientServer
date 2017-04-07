package fileEvent;

import java.io.Serializable;

public class FileEvent implements Serializable {

	public FileEvent() {
	}

	private static final long serialVersionUID = 1L;

	private int totalFileSegments;
	private int fileSegmentNumber;
	private long totalFileSize;
	private String destinationDirectory;
	private String sourceDirectory;
	private String filename;
	private long fileSegmentSize;
	private int segmentStartIndex;
	private int segmentEndIndex;
	private byte[] fileSegmentData;
	private byte[] md5Hash;

	private String status;
	
	public int getTotalFileSegments() {
		return totalFileSegments;
	}

	public void setTotalFileSegments(int totalFileSegments) {
		this.totalFileSegments = totalFileSegments;
	}

	public int getFileSegmentNumber() {
		return fileSegmentNumber;
	}

	public void setFileSegmentNumber(int fileSegmentNumber) {
		this.fileSegmentNumber = fileSegmentNumber;
	}
	
	public long getTotalFileSize() {
		return totalFileSize;
	}

	public void setTotalFileSize(long totalFileSize) {
		this.totalFileSize = totalFileSize;
	}

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public String getSourceDirectory() {
		return sourceDirectory;
	}

	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getFileSegmentSize() {
		return fileSegmentSize;
	}

	public void setFileSegmentSize(long fileSegmentSize) {
		this.fileSegmentSize = fileSegmentSize;
	}

	
	public int getSegmentStartIndex() {
		return segmentStartIndex;
	}

	public void setSegmentStartIndex(int segmentStartIndex) {
		this.segmentStartIndex = segmentStartIndex;
	}

	public int getSegmentEndIndex() {
		return segmentEndIndex;
	}

	public void setSegmentEndIndex(int segmentEndIndex) {
		this.segmentEndIndex = segmentEndIndex;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public byte[] getFileSegmentData() {
		return fileSegmentData;
	}

	public void setFileSegmentData(byte[] fileSegmentData) {
		this.fileSegmentData = fileSegmentData;
	}
	
	public byte[] getMd5Hash() {
		return md5Hash;
	}

	public void setMd5Hash(byte[] md5Hash) {
		this.md5Hash = md5Hash;
	}
}