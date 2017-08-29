package org.superbiz.moviefun;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albumsapi.AlbumFixtures;
import org.superbiz.moviefun.albumsapi.AlbumInfo;
import org.superbiz.moviefun.albumsapi.AlbumsClient;
import org.superbiz.moviefun.moviesapi.MovieFixtures;
import org.superbiz.moviefun.moviesapi.MovieInfo;
import org.superbiz.moviefun.moviesapi.MoviesClient;

import java.util.List;
import java.util.Map;

@Controller
public class SetupController {

    private final MoviesClient moviesClient;
    private final AlbumsClient albumsClient;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;

    public SetupController(MoviesClient moviesClient, AlbumsClient albumsClient, MovieFixtures movieFixtures, AlbumFixtures albumFixtures) {
        this.moviesClient = moviesClient;
        this.albumsClient = albumsClient;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {

        boolean isFirstTime = true;

        List<MovieInfo> movies = moviesClient.getMovies();
        List<MovieInfo> fixtureMovies = movieFixtures.load();

        for(MovieInfo databaseMovie : movies) {
            for(MovieInfo fixtureMovie : movies) {
                if (!databaseMovie.equalIgnoringID(fixtureMovie)) {
                    isFirstTime = false;
                    break;
                }
            }
        }

        if (isFirstTime) {
            for (MovieInfo movie : fixtureMovies) {
                moviesClient.addMovie(movie);
            }
            for (AlbumInfo album : albumFixtures.load()) {
                albumsClient.addAlbum(album);
            }
        }

        model.put("movies", moviesClient.getMovies());
        model.put("albums", albumsClient.getAlbums());

        return "setup";
    }
}
