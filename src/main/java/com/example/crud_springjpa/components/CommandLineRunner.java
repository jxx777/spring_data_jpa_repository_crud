package com.example.crud_springjpa.components;

import com.example.crud_springjpa.entity.Game;
import com.example.crud_springjpa.entity.Genre;
import com.example.crud_springjpa.entity.Platform;
import com.example.crud_springjpa.repository.GameRepository;
import com.example.crud_springjpa.repository.GenreRepository;
import com.example.crud_springjpa.repository.PlatformRepository;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {
    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final PlatformRepository platformRepository;
    private static final LocalDate CURRENT_DATE = LocalDate.now();

    public CommandLineRunner(
            GameRepository gameRepository,
            GenreRepository genreRepository,
            PlatformRepository platformRepository
    ) {
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
        this.platformRepository = platformRepository;
    }

    @Transactional
    @Override
    public void run(String... args) {
        // Create and save genres
        Genre action = new Genre("Action");
        genreRepository.save(action);

        Genre adventure = new Genre("Adventure");
        genreRepository.save(adventure);

        Genre strategy = new Genre("Strategy");
        genreRepository.save(strategy);

        Genre mmorpg = new Genre("MMORPG");
        genreRepository.save(mmorpg);

        String[] platformNames = {"Mobile", "Desktop", "Tabletop"};
        List<Platform> platforms = Arrays.stream(platformNames)
                .map(this::createAndSavePlatform)
                .toList();

        System.out.println("\nCreated platforms:");
        platforms.forEach(System.out::println);

        // Game containing all genres and all platforms.
        Game multiGame = new Game();
        multiGame.setTitle("Multiverse");
        multiGame.setReleaseDate(generateReleaseDate());
        genreRepository.findAll().forEach(multiGame::addGenre); // Using Method Reference
        platformRepository.findAll().forEach(multiGame::addPlatform); // Using Method Reference
        gameRepository.save(multiGame);

        LocalDate oldReleaseData = LocalDate.of(1989, 7, 1);
        Game retroGame = new Game();
        retroGame.setTitle("Indiana Jones and the Last Crusade");
        retroGame.addGenre(adventure);
        retroGame.setReleaseDate(oldReleaseData);
        gameRepository.save(retroGame);

        System.out.println("\nUsing JPQL to return genre like: ");
        genreRepository.searchByName("ventu").forEach(System.out::println);


        // Create and save a game for Strategy on Mobile
        LocalDate futureReleaseDate = LocalDate.of(2026, 1, 20);
        Game unreleasedGame = new Game();
        unreleasedGame.setTitle("Grand Theft Auto: VI");
        unreleasedGame.setReleaseDate(futureReleaseDate);
        unreleasedGame.addGenre(action);

        platforms.stream()
                .filter(platform -> "desktop".equalsIgnoreCase(platform.getName()))
                .findFirst()
                .ifPresent(unreleasedGame::addPlatform);
        gameRepository.save(unreleasedGame);

        Game gameWithFragmentName = new Game();
        gameWithFragmentName.setTitle("World of Warcraft");
        gameWithFragmentName.addGenre(mmorpg);
        gameWithFragmentName.addPlatform(platformRepository.findByNameContaining("Desk"));
        gameWithFragmentName.setReleaseDate(generateReleaseDate());
        gameRepository.save(gameWithFragmentName);

        Game gameWithFragmentNameTwo = new Game();
        gameWithFragmentNameTwo.setTitle("World of Tanks");
        gameWithFragmentNameTwo.setReleaseDate(generateReleaseDate());
        gameRepository.save(gameWithFragmentNameTwo);

        System.out.println("\nAction Games: ");
        gameRepository.findByGenresName("Action").forEach(System.out::println);

        System.out.println("\nDesktop Games: ");
        gameRepository.findByPlatformsName("Desktop").forEach(System.out::println);

        List<Game> unreleasedGames = gameRepository.findByReleaseDateAfter(CURRENT_DATE);
        System.out.println("\nGames unreleased as of " + CURRENT_DATE + ": " + unreleasedGames);

        gameRepository
            .findByGenresNameAndPlatformsName("Action", "Desktop")
            .forEach(System.out::println);

        String nameFragment = "World";
        System.out.printf("\nGames with '%s' in name: \n", nameFragment);
        gameRepository
            .findByTitleContaining(nameFragment)
            .stream()
                .map(Game::getTitle)
                .forEach(System.out::println);

        Optional<LocalDate> earliestAdventureGame = gameRepository.findAll().stream() // stream all games
                .filter(game -> game.getGenres().stream() // filter by genre
                        .anyMatch(genre -> genre.getName().equals("Adventure"))) // matching the passed string
                .map(Game::getReleaseDate) // get the release date from matched entries
                .min(LocalDate::compareTo); // finally, return the minimum value for date

        earliestAdventureGame.ifPresent(date -> System.out.println("\nEarliest Release Date for Adventure Games: " + date));

        String upcomingGenre = "Action";
        System.out.printf("Upcoming %s Games: ", upcomingGenre);
        gameRepository.findByGenresNameAndReleaseDateAfter(upcomingGenre, CURRENT_DATE).forEach(System.out::println);


        Platform platformsContainingDesk = platformRepository.findByNameContaining("Desk");
        System.out.println("\nPlatforms containing 'Desk': " + platformsContainingDesk);

        Map<Platform, Long> gamesCountByPlatform = gameRepository.findAll().stream()
                .flatMap(game -> game.getPlatforms().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        System.out.println("\nGames Count by Platform: " + gamesCountByPlatform);

        // Calling JPQL (Java Persistence Query Language) method
        System.out.println(
            platformRepository.countPlatformsWithNoGames() != 0
                ? "Found platforms without games"
                : "No platforms without games"
        );


        System.out.println("\nAttempting to delete genre used in other games, by it's name");
        genreRepository.deleteByName("Adventure");

        String nameOfGenreToDelete = "Point & Click Puzzle";
        Genre emptyGenre = new Genre(nameOfGenreToDelete);
        genreRepository.save(emptyGenre);
        genreRepository.findAll().forEach(System.out::println);

        System.out.printf("\nDeleting %s \n", nameOfGenreToDelete);
        genreRepository.deleteByName(nameOfGenreToDelete);

        System.out.println("\nDeleting game");
        gameRepository.deleteByTitle("World Of Warcraft");
    }

    private Platform createAndSavePlatform(String platformName) {
        Platform platform = new Platform(platformName);
        platformRepository.save(platform);
        return platform;
    }

    private LocalDate generateReleaseDate() {
        Faker faker = new Faker();
        Date releaseDate = faker.date().between(
                java.sql.Date.valueOf(LocalDate.of(1990, 1, 1)),
                java.sql.Date.valueOf(LocalDate.of(2034, 12, 31))
        );
        return releaseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}