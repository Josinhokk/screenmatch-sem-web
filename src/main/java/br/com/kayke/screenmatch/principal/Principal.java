package br.com.kayke.screenmatch.principal;

import br.com.kayke.screenmatch.model.DadosEpisodio;
import br.com.kayke.screenmatch.model.DadosSerie;
import br.com.kayke.screenmatch.model.DadosTemporada;
import br.com.kayke.screenmatch.model.Episodio;
import br.com.kayke.screenmatch.service.ConsumoApi;
import br.com.kayke.screenmatch.service.ConverteDados;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
//      Esse codigo é a mesma coisa do codigo de cima!
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()).collect(Collectors.toList());

        System.out.println("\nTop 5 episodios:");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equals("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do titulo do episodio: ");
        var trechoTitulo = scanner.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                        .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase())).findFirst();
        if(episodioBuscado.isPresent()){
            System.out.println("Episodio encontrado!! ");
            System.out.println(episodioBuscado.get());
        }

        System.out.println("A partir de que ano voce deseja ver os episodios:");
        var ano = scanner.nextInt();
        scanner.nextLine();

        LocalDate dataBusca = LocalDate.of(ano,1,1);

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream().
                filter(e -> e.getDataLancamento() != null && e.getDataLancamento()
                        .isAfter(dataBusca)).forEach(e -> System.out.println(
                                "Temporada:  " + e.getTemporada() +
                                        "Episodio:  " + e.getTitulo() +
                                        "Data lançamento:  " + e.getDataLancamento().format(formatador)
                ));
        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter( e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episodio " + est.getMax());
        System.out.println("Pior episodio " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}
