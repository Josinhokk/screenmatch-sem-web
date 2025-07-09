package br.com.kayke.screenmatch.principal;

import br.com.kayke.screenmatch.model.DadosEpisodio;
import br.com.kayke.screenmatch.model.DadosSerie;
import br.com.kayke.screenmatch.model.DadosTemporada;
import br.com.kayke.screenmatch.service.ConsumoApi;
import br.com.kayke.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    Scanner scanner = new Scanner(System.in);

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=ca47f257";

    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    public void exibirMenu(){
        System.out.println("Digite o nome do titulo: ");
        var serie = scanner.nextLine();
        var json = consumoApi.obterDados(ENDERECO + serie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        //lista de temporadas
        List<DadosTemporada> temporadas = new ArrayList<>();

        for(int i =1; i<=dados.totalTemporadas(); i++){
            json = consumoApi.obterDados(ENDERECO + serie.replace(" ", "+") + "&season=" + i  + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);

            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

//        for(int i = 0; i<temporadas.size(); i++){
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for(int j = 0; j<episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }
}
