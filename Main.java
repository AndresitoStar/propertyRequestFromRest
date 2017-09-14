
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

public class Main {
	private Map<String, String> propertiesReturned = new HashMap<>();
	static String tokenSeguridad;

	private static String TOKEN_SERVICE_URL = "http://192.168.101.115:9000/api/authenticate";
	private static String PROPERTY_SERVICE_URL = "http://192.168.101.115:9000/api/system-properties/";

	public static void main(String[] args) {
		String id = "";
		Client client = ClientBuilder.newClient();
		Main m = new Main();

		try {
			while (true) {
				System.out.println("Teclee el <id> de la propiedad que desea conocer:");
				BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
				id = br2.readLine();
				if (id.equals("exit"))
					break;
				System.out.println(m.getProperty(id, client));
				System.out.println("----");
			}
			System.out.println("Bye bye..");
		} catch (Exception e) {
			System.out.println("Ha ocurrido un error...");
			e.printStackTrace();
		}
	}

	private static String getTokenSeguridad(Client client) {
		String response = client.target(TOKEN_SERVICE_URL).path("/{user}, {keep}, {pass}")
				.resolveTemplate("user", "admin")
				.resolveTemplate("keep", true)
				.resolveTemplate("pass", "admin")
				.request(MediaType.APPLICATION_JSON).get(String.class);
		return response;
	}

	private String getProperty(String id, Client client) {
		if (id == null) {
			return null;
		}
		// Utilizando la cache
		if (propertiesReturned.containsKey(id)) {
			return propertiesReturned.get(id);
		}		
		 //Obteniendo el token de seguridad
		 if (tokenSeguridad == null) {
			 tokenSeguridad = getTokenSeguridad(client);
		 }
		 // Consultando el servicio para conocer la property deseada 
		 String response = client.target(PROPERTY_SERVICE_URL).path("/{token},{id}")
		 .resolveTemplate("id", id)
		 .resolveTemplate("token", tokenSeguridad)
		 .request(MediaType.APPLICATION_JSON).get(String.class); 
		 // Almacenando la property a la cache
		propertiesReturned.put(id, response);
		
		return response;
	}
}
