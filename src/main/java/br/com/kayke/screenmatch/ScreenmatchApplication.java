package br.com.kayke.screenmatch;

import br.com.kayke.screenmatch.model.DadosSerie;
import br.com.kayke.screenmatch.service.ConsumoApi;
import br.com.kayke.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoApi = new ConsumoApi();
		var json = consumoApi.obterDados("https://www.omdbapi.com/?t=under+the+dome&apikey=ca47f257");
		System.out.println(json);
		ConverteDados converosr = new ConverteDados();
		DadosSerie dados = converosr.obterDados(json, DadosSerie.class);
		System.out.println(dados);
	}
}
