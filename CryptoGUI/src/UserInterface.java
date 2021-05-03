import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UserInterface extends Application {

	private static int width = 400;
	private static int height = 600;
	
	private DESSimple crypt;
	private String defaultKeyFileName = "session-key";
	
	private Button loadKeyButton;
	private Button createKeyButton;
	private Button saveKeyButton;
	private Button encryptFileButton;
	private Button decryptFileButton;
	private VBox pane;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		crypt = new DESSimple(defaultKeyFileName);
		
		// create javafx nodes
		loadKeyButton = new Button("Load Key");
		createKeyButton = new Button("Create Key");
		saveKeyButton = new Button("Save Key");
		encryptFileButton = new Button("Encrypt File");
		decryptFileButton = new Button("Decrypt File");
		pane = new VBox();
		pane.getChildren().addAll(loadKeyButton, createKeyButton, saveKeyButton, encryptFileButton, decryptFileButton);
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(20, 20, 20, 20));
		
		// set button event listeners
		loadKeyButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// need to um open a dialog box to browse for the file.
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Select Encryption Key File");
				File keyFile = fileChooser.showOpenDialog(primaryStage);
				if (keyFile != null) {
					try {
						crypt.loadKey(keyFile.getAbsolutePath());
					} catch (ClassNotFoundException e) {
						System.out.println("Class Not Found Exception: " + e);
						e.printStackTrace();
					} catch (IOException e) {
						System.out.println("File Exception: " + e);
						e.printStackTrace();
					}
				} else {
					System.out.println("File not found");
				}
			}
		});
		
		createKeyButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// generate a new key.
				try {
					crypt.generateKey();
				} catch (NoSuchAlgorithmException e) {
					System.out.println("No Such Algorithm Exception: " + e);
					e.printStackTrace();
				}
			}
		});
		
		saveKeyButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// save key to file
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Pick or Create Key File");
				File keyFile = fileChooser.showSaveDialog(primaryStage);
				if (keyFile != null) {
					try {
						crypt.saveKey(keyFile.getAbsolutePath());
					} catch (IOException e) {
						System.out.println("IO Exception: " + e);
						e.printStackTrace();
					}
				}
			}
		});
		
		encryptFileButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					
					// open file chooser to pick text file
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Choose Text File to Encrypt");
					File textFile = fileChooser.showOpenDialog(primaryStage);
					
					// open file chooser to pick file to save encrypted file as
					fileChooser.setTitle("Pick or Create Key File");
					File encryptedFile = fileChooser.showSaveDialog(primaryStage);
					// read file string
					Scanner scan = new Scanner(textFile);
					String fileText = "";
					while (scan.hasNext()) {
						fileText += scan.next();
					}
					
					// run encryption 
					byte[] encText = crypt.encrypt(fileText);            
					System.out.println("The DES encrypted message 64: "+ (Base64.getEncoder().encodeToString(encText)));
					
					// run save
					if (encryptedFile != null) {
						try {
							crypt.saveEncrypted(encryptedFile.getAbsolutePath(), encText);
							
						} catch (IOException e) {
							System.out.println("IO Exception: " + e);
							e.printStackTrace();
						}
					}
				} catch(Exception e) {
					System.out.println("Error: " + e);
				}
			}
		});
		
		decryptFileButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// choose file to decrypt
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Choose Encrypted File to Decrypt");
				File encryptedFile = fileChooser.showOpenDialog(primaryStage);
				
				// choose file to save decrypted text in
				fileChooser.setTitle("Pick or Create Decrypted Text File");
				File decryptedFile = fileChooser.showSaveDialog(primaryStage);
				
				// run decryption
				String decryptedText = crypt.decrypt(encryptedFile);
				
				// save file
			}
		});
		
		Scene scene = new Scene(pane, width, height);
		primaryStage.setTitle("My Cryptor App");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
