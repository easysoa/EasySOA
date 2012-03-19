import java.security.InvalidParameterException;

public interface DiscoveryReplayService {

	/**
	 * Rejoue un jeu de découverte.
	 * Bloque jusqu'à la fin de son émission.
	 * 
	 * @param recordName Le nom du jeu de découverte à lancer
	 * @param environmentName Le nom de l'environnement sur lequel adresser les notifications
	 * @throws InvalidParameterException Si le nom du jeu de découverte n'existe pas
	 */
	void replayRecord(String recordName, String environmentName) throws InvalidParameterException;

}
