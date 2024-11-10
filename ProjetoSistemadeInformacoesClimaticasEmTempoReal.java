import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProjetoSistemadeInformacoesClimaticasEmTempoReal {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Digite o nome da cidade: ");
		String cidade = scanner.nextLine();

		try {
			String dadosClimaticos = getDadosClimaticos(cidade);

			//código 1006 significa localização não encontrada
			if (dadosClimaticos.contains("\"code\":1006")) { // \"code\":1006 representa "code": 1006
				System.out.println("Localizacao nao encontrada. Por favor, tente novamente.");
			} else {
				imprimirDadosClimaticos(dadosClimaticos);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static String getDadosClimaticos(String cidade) throws Exception {
		String apiKey = Files.readString( Paths.get("api-key.txt")).trim();

		String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
		String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + formataNomeCidade;
		HttpRequest request = HttpRequest.newBuilder() // começa a construção de uma nova solicitação HTTP
			.uri(URI.create(apiUrl)) // este método define o URI da solicitação HTTP
			.build(); // finaliza a construção da solicitação HTTP

		// criar objeto enviar solicitações http e receber respostas http para acessar o site da weather api
		HttpClient client = HttpClient.newHttpClient();

		// agora enviaremos requisições http e receber respostas http, comunicar com o site da api meteorológica
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body(); // retorna os dados meteorológicos obtidos no site da api
	}

	public static void imprimirDadosClimaticos(String dados) {
		JSONObject dadosJson = new JSONObject(dados);
		JSONObject informacoesMeteorologicas = dadosJson.getJSONObject("current");

		// extrai os dados da localização
		String cidade = dadosJson.getJSONObject("location").getString("name");
		String pais = dadosJson.getJSONObject("location").getString("country");

		// extrai os dados adicionais
		String condicaoTempo = informacoesMeteorologicas.getJSONObject("condition").getString("text");
		int umidade = informacoesMeteorologicas.getInt("humidity");
		float velocidadeVento = informacoesMeteorologicas.getFloat("wind_kph");
		float pressaoAtmosferica = informacoesMeteorologicas.getFloat("pressure_mb");
		float sensacaoTermica = informacoesMeteorologicas.getFloat("feelslike_c");
		float temperaturaAtual = informacoesMeteorologicas.getFloat("temp_c");

		// extrai a data e a hora da string retornada pela api; dados do momento da aferição
		String dataHoraString = informacoesMeteorologicas.getString("last_updated");

		System.out.println("Informações meteorológicas para " + cidade + ", " + pais);
		System.out.println("Data e hora: " + dataHoraString);
		System.out.println("Temperatura atual: " + temperaturaAtual + "C");
		System.out.println("Sensação térmica: " + sensacaoTermica + "C");
		System.out.println("Condição do tempo: " + condicaoTempo);
		System.out.println("Umidade: " + umidade + "%");
		System.out.println("Velocidade do vento: " + velocidadeVento + "km/h");
		System.out.println("Pressão atmosférica: " + pressaoAtmosferica + "mb");
	}
}
