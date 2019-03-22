package io.javatrain.moviecatalogservice.resources;

import io.javatrain.moviecatalogservice.models.CatalogItem;
import io.javatrain.moviecatalogservice.models.Movie;
import io.javatrain.moviecatalogservice.models.Rating;
import io.javatrain.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        UserRating ratings = restTemplate.getForObject("http://localhost:8083/ratingsdata/users/foo" + userId, UserRating.class);

        return ratings.getUserRating().stream().map(rating -> {
            /// For each movie ID, call movie info service and get details
            Movie movie = restTemplate.getForObject("http://localhost:8082/movies/foo" + rating.getMovieId(), Movie.class);
            /*
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
            */
            // put them all together
            return new CatalogItem(movie.getName(), "test", rating.getRating());
        }).collect(Collectors.toList());




    }
}
