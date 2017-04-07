package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClientUI {

	//GUI
	//----
	private JFrame f = new JFrame("Client");
	private JButton saveSetupButton = new JButton("Save Setup");
	private JButton uploadFileButton = new JButton("Upload File");
	private JPanel mainPanel = new JPanel();
	private Client client;
	private final JLabel lblServerIp = new JLabel("Server IP: ");
	private final JLabel lblServerPort = new JLabel("Server PORT: ");
	private final JLabel lblBufferSize = new JLabel("Buffer Size:");
	private final JLabel lblFileToUpload = new JLabel("File To Upload:");
	private final JTextField txtFieldIP = new JTextField();
	private final JTextField txtFieldPort = new JTextField();
	private final JTextField txtFieldBufferSize = new JTextField();
	private final JTextField txtFieldFile = new JTextField();
	private File fileToUpload;

	public ClientUI() {

		//Frame
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		//frame layout
		mainPanel.setLayout(null);

		f.getContentPane().add(mainPanel, BorderLayout.CENTER);
		

		lblServerIp.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblServerIp.setBounds(24, 27, 143, 56);
		mainPanel.add(lblServerIp);
		
		lblServerPort.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblServerPort.setBounds(24, 69, 190, 56);
		mainPanel.add(lblServerPort);
		
		lblBufferSize.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblBufferSize.setBounds(24, 111, 255, 56);
		mainPanel.add(lblBufferSize);
		
		txtFieldIP.setBounds(280, 46, 228, 22);
		txtFieldIP.setColumns(10);
		mainPanel.add(txtFieldIP);
		
		txtFieldPort.setColumns(10);
		txtFieldPort.setBounds(413, 88, 95, 22);
		mainPanel.add(txtFieldPort);
		
		txtFieldBufferSize.setColumns(10);
		txtFieldBufferSize.setBounds(413, 124, 95, 22);
		mainPanel.add(txtFieldBufferSize);
		
		saveSetupButton.setBounds(134, 180, 261, 50);
		mainPanel.add(saveSetupButton);
		saveSetupButton.addActionListener(new saveConfigButtonListener());
		

		lblFileToUpload.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblFileToUpload.setBounds(24, 238, 241, 56);		
		mainPanel.add(lblFileToUpload);
		
		txtFieldFile.setColumns(10);
		txtFieldFile.setBounds(24, 302, 471, 32);
		mainPanel.add(txtFieldFile);
		txtFieldFile.addMouseListener(new fileTxtFieldListener());
		
		uploadFileButton.setBounds(134, 361, 261, 50);
		mainPanel.add(uploadFileButton);
		uploadFileButton.addActionListener(new uploadFileButtonListener());
		f.setSize(new Dimension(540, 484));
		f.setVisible(true);
		

		client = new Client();
		txtFieldIP.setText(client.DEFAULT_SERVER_IP);
		txtFieldPort.setText(Integer.toString(client.DEFAULT_PORT));
		txtFieldBufferSize.setText(Integer.toString(client.DEFAULT_BUFFER_SIZE));

	}

	class saveConfigButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String pIP = txtFieldIP.getText();
			String pPort = txtFieldPort.getText();
			String pBufferSize = txtFieldBufferSize.getText();
			
			if (!pIP.equals("") && !pPort.equals("") && !pBufferSize.equals("")){
				
				if (client.setConfig(pIP, Integer.parseInt(pPort), Integer.parseInt(pBufferSize))){
					JOptionPane.showMessageDialog(f, "Settings Updated Successfully" + client.CRLF +						
						"IP: " + pIP + client.CRLF +
						"Port: " + pPort + client.CRLF +
						"Buffer Size: " + pBufferSize);
				}
			}
			else{
				JOptionPane.showMessageDialog(f, "Fill all the fields!" + client.CRLF + 
						"Using default Settings: " + client.CRLF + 
						"Default IP: " + client.DEFAULT_SERVER_IP + client.CRLF +
						"Default Port: " + client.DEFAULT_PORT + client.CRLF +
						"Default Buffer Size: " + client.DEFAULT_BUFFER_SIZE);
			}
		}
	}

	class fileTxtFieldListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			//Create a file chooser
			final JFileChooser fc = new JFileChooser(new File("./data"));
			
			int returnVal = fc.showOpenDialog(f);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            fileToUpload = fc.getSelectedFile();
	            //This is where a real application would open the file.
	            System.out.println("Opening: " + fileToUpload.getName() + "." + "\n");
	            txtFieldFile.setText(fileToUpload.getAbsolutePath());
				System.out.println(fileToUpload.getAbsolutePath());
	        }
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	class uploadFileButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			if(fileToUpload != null){						
				//Try to send file
				boolean success = client.createConnection(fileToUpload.getAbsolutePath());
				
				if (success){
					JOptionPane.showMessageDialog(f, "Upload to server successfull !!!");
				}
				else{
					JOptionPane.showMessageDialog(f, "Something went wrong, check console...", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}			
			else{
				JOptionPane.showMessageDialog(f, "No file selected.");
			}
		}
	}
	

	public static void main(String argv[]) throws Exception
	{
		//Create a Client object
		new ClientUI();
	}
}
