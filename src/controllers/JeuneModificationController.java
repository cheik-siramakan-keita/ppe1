package controllers;

import java.io.IOException;
import java.sql.SQLException;
import application.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import models.base.User;
import models.dao.ConfigurationConnexionBaseDeDonneesDAO;
import models.dao.UserDAO;

public class JeuneModificationController {
	@FXML
	private AnchorPane mainPane;
	@FXML
	private Button deconnexionButton;
	@FXML
	private Button fermetureButton;
	@FXML
	private Button retourButton;
	@FXML
	private Button modificationButton;
	@FXML
	private Label idLabel;
	@FXML
	private TextField nomInput;
	@FXML
	private TextField prenomInput;
	@FXML
	private TextField identifiantInput;
	@FXML
	private TextField emailInput;
	@FXML
	private TextField telephoneInput;
	@FXML
	private TextField adresseInput;
	@FXML
	private TextField villeInput;
	@FXML
	private TextField codePostalInput;
	
	String nomDeLaPersonneConnecte;
	int idJeuneModification;
	String roles;
	
	private double xOffset;
	private double yOffset;
	
	public void recuperer_le_nom_de_la_personne_connecte(String nomDeLaPersonneConnecte) 
	{
		this.nomDeLaPersonneConnecte = nomDeLaPersonneConnecte;
	}
	
	public void recuperer_le_status_super_administrateur_de_la_personne_connecte(String roles) 
	{
		this.roles = roles;
	}
	
	public void obtenir_les_informations_du_jeune_a_modifier(int idJeuneModification, String nom, String prenom, String email,
			String telephone, String adresse, String ville, String code_postal) 
	{
		this.idJeuneModification = idJeuneModification;
		idLabel.setText("ID: " + Integer.toString(idJeuneModification));
		nomInput.setText(nom);
		prenomInput.setText(prenom);
		emailInput.setText(email);
		telephoneInput.setText(telephone);
		adresseInput.setText(adresse);
		villeInput.setText(ville);
		codePostalInput.setText(code_postal);
	}
	
	@FXML
	private void deconnecter_l_utilisateur_connecte(ActionEvent actionEvent) 
	{	
		try 
		{
	    	mainPane.getChildren().clear();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getClassLoader().getResource("views/fxml/Connexion.fxml"));
			AnchorPane userFrame = (AnchorPane) loader.load();
			Scene sc = mainPane.getScene();
			sc.setRoot(userFrame);
		}catch(IOException e) 
		{
	        e.printStackTrace();
	    }
	}
	
	@FXML
	private void fermer_l_application(ActionEvent actionEvent) 
	{
		Platform.exit();
        System.exit(0);
	}
	
	@FXML
	private void retourner_dans_la_page_precedente(ActionEvent actionEvent) 
	{
		try 
		{
			mainPane.getChildren().clear();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getClassLoader().getResource("views/fxml/Jeune.fxml"));
			AnchorPane userFrame = (AnchorPane) loader.load();
			Scene sc = mainPane.getScene();
			sc.setRoot(userFrame);
			JeuneController jeuneController = loader.<JeuneController>getController();
			jeuneController.recuperer_le_nom_de_la_personne_connecte(this.nomDeLaPersonneConnecte);
			jeuneController.recuperer_le_status_super_administrateur_de_la_personne_connecte(this.roles);
		}catch (IOException e) 
		{
		   e.printStackTrace();
		}
	}
	
	@FXML
	private void modifier_les_informations_du_jeune(ActionEvent actionEvent) throws NumberFormatException, ClassNotFoundException, SQLException 
	{
		boolean verificationDeLaSyntaxeEmail = User.verifier_la_syntaxe_de_l_email(emailInput.getText());
		
		if(verificationDeLaSyntaxeEmail == true) 
		{
			if(telephoneInput.getText().length() == 10) 
			{
				if(adresseInput.getText().length() <= 38) 
				{
					if(villeInput.getText().length() <= 32) 
					{
						if(codePostalInput.getText().length() == 5) 
						{
							String roles = "[\"ROLE_JEUNE\"]";
							User user = new User();
							user.setId(Integer.parseInt(idLabel.getText().substring(4, 6)));
							user.setRoles(roles);
							user.setNom(nomInput.getText());
							user.setPrenom(prenomInput.getText());
							user.setUsername();
							user.setEmail(emailInput.getText());
							user.setTelephone(telephoneInput.getText());
							user.setAdresse(adresseInput.getText());
							user.setVille(villeInput.getText());
							user.setCodepostal(codePostalInput.getText());
							
							boolean statusModificationInformationsDuJeune = UserDAO.modifier(user);
							
							if(statusModificationInformationsDuJeune == true) 
							{
								boolean statusOptionEnvoiEmail = ConfigurationConnexionBaseDeDonneesDAO.obtenir_le_status_de_l_option_d_envoi_d_email();
								
								if(statusOptionEnvoiEmail == true) 
								{
									boolean statusEnvoiEmail = User.email_de_modification(emailInput.getText());
									
									if(statusEnvoiEmail == false) 
									{
										Alert a1 = new Alert(Alert.AlertType.ERROR);
										a1.setTitle("Erreur: n�7");
										a1.setContentText("L'envoi d'email ne fonctionne pas vous pouvez d�sactiver la fonctionnalit� dans le menu de configuration.");
										a1.setHeaderText(null);
										a1.showAndWait();
									}
								}
								
								try 
								{
									mainPane.getChildren().clear();
									FXMLLoader loader = new FXMLLoader();
									loader.setLocation(Main.class.getClassLoader().getResource("views/fxml/Jeune.fxml"));
									AnchorPane userFrame = (AnchorPane) loader.load();
									Scene sc = mainPane.getScene();
									sc.setRoot(userFrame);
									JeuneController jeuneController = loader.<JeuneController>getController();
									jeuneController.recuperer_le_nom_de_la_personne_connecte(this.nomDeLaPersonneConnecte);
									jeuneController.recuperer_le_status_super_administrateur_de_la_personne_connecte(this.roles);
								}catch (IOException e) 
								{
									e.printStackTrace();
								}
							}else {
								Alert a1 = new Alert(Alert.AlertType.ERROR);
								a1.setTitle("Erreur: n�6");
								a1.setContentText("Erreur lors de l'ajout d'un partenaire.");
								a1.setHeaderText(null);
								a1.showAndWait();
							}
						}else {
							Alert a1 = new Alert(Alert.AlertType.ERROR);
							a1.setTitle("Erreur: n�5");
							a1.setContentText("Le code postal devrait avoir un maximum de 5 caract�res.");
							a1.setHeaderText(null);
							a1.showAndWait();
						}
					}else {
						Alert a1 = new Alert(Alert.AlertType.ERROR);
						a1.setTitle("Erreur: n�4");
						a1.setContentText("La ville devrait avoir un maximum de 32 caract�res.");
						a1.setHeaderText(null);
						a1.showAndWait();
					}
				}else {
					Alert a1 = new Alert(Alert.AlertType.ERROR);
					a1.setTitle("Erreur: n�3");
					a1.setContentText("L'addresse devrait avoir un maximum de 38 caract�res.");
					a1.setHeaderText(null);
					a1.showAndWait();
				}
			}else {
				Alert a1 = new Alert(Alert.AlertType.ERROR);
				a1.setTitle("Erreur: n�2");
				a1.setContentText("Le n� de t�l�phone devrait avoir 10 chiffre.");
				a1.setHeaderText(null);
				a1.showAndWait();
			}
		}else {
			Alert a1 = new Alert(Alert.AlertType.ERROR);
			a1.setTitle("Erreur: n�1");
			a1.setContentText("Le format de l'email est incorrect.");
			a1.setHeaderText(null);
			a1.showAndWait();
		}
	}
	
	@FXML
    private void initialize() throws ClassNotFoundException, SQLException {
		mainPane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				 xOffset = event.getSceneX();
				 yOffset = event.getSceneY();
			}
		});
		
		mainPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Main.obtenir_le_primaryStage().setX(event.getScreenX()- xOffset);
				Main.obtenir_le_primaryStage().setY(event.getScreenY()- yOffset);
			}
		});
    }
}
