# UDPClientServer
# Setup:
1. Clone the repository
2. Set it up with a Java IDE (I Use Eclipse)
3. Set the server **port** and **bufferSize** located at "server/Server.java" to your preference
4. Run the server
5. Run the ClientUI located at "client/ClientUI.java"
6. Set the configuration values or leave them to default
    * **Make sure that the Client bufferSize <= Server bufferSize**
    * And of course that the ports match as well
7. Save setup.
8. Select the file you wish to send to server
9. Click on "Upload File" and the selector will open at "./data/" folder
10. Wait for the file to be sent, you can check the console to watch the progress
11. Once done the file is stored at "./udpUploads/" folder by default
