package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Sql(
        scripts = {"/schema.sql", "/data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmoRateApplicationTests {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private FilmDbStorage filmStorage;

    // ================= USER TESTS =================

    @Test
    void testCreateUser() {
        User user = new User();

        user.setName("Иван");
        user.setEmail("ivan@mail.ru");
        user.setLogin("ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.createUser(user);

        assertThat(created.getId()).isPositive();
    }

    @Test
    void testGetUserById() {
        User user = new User();

        user.setName("Иван");
        user.setEmail("ivan@mail.ru");
        user.setLogin("ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.createUser(user);

        Optional<User> found = userStorage.getUserById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId())
                .isEqualTo(created.getId());

        assertThat(found.get().getEmail())
                .isEqualTo("ivan@mail.ru");
    }

    @Test
    void testGetAllUsers() {
        User user = new User();

        user.setName("Иван");
        user.setEmail("ivan@mail.ru");
        user.setLogin("ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        userStorage.createUser(user);

        List<User> users = userStorage.getAllUsers();

        assertThat(users).isNotEmpty();
        assertThat(users.size())
                .isGreaterThanOrEqualTo(1);
    }

    @Test
    void testUpdateUser() {
        User user = new User();

        user.setName("Иван");
        user.setEmail("ivan@mail.ru");
        user.setLogin("ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.createUser(user);

        created.setName("Петр");
        created.setEmail("petr@mail.ru");

        userStorage.updateUser(created);

        Optional<User> updated =
                userStorage.getUserById(created.getId());

        assertThat(updated).isPresent();

        assertThat(updated.get().getName())
                .isEqualTo("Петр");

        assertThat(updated.get().getEmail())
                .isEqualTo("petr@mail.ru");
    }

    // ================= FILM TESTS =================

    private Film createTestFilm() {

        Film film = new Film();

        film.setName("Avatar");
        film.setDescription("Description");
        film.setDuration(180);
        film.setReleaseDate(
                LocalDate.of(2009, 12, 18)
        );

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        mpa.setName("G");
        film.setMpa(mpa);

        return film;
    }

    @Test
    void testCreateFilm() {

        Film created =
                filmStorage.createFilm(createTestFilm());

        assertThat(created.getId())
                .isPositive();
    }

    @Test
    void testGetFilmById() {

        Film created =
                filmStorage.createFilm(createTestFilm());
        Optional<Film> found =
                filmStorage.getFilmById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName())
                .isEqualTo("Avatar");
    }

    @Test
    void testGetAllFilms() {

        filmStorage.createFilm(createTestFilm());
        filmStorage.createFilm(createTestFilm());

        List<Film> films =
                filmStorage.getAllFilms();

        assertThat(films)
                .isNotEmpty();
        assertThat(films.size())
                .isGreaterThanOrEqualTo(2);
    }

    @Test
    void testTopFilms() {

        filmStorage.createFilm(createTestFilm());
        List<Film> top =
                filmStorage.getTopFilms(10);

        assertThat(top)
                .isNotNull();
    }

    // ================= UPDATE FILM TEST =================

    @Test
    void testUpdateFilm() {

        // создаём фильм
        Film film = createTestFilm();
        Film created =
                filmStorage.createFilm(film);
        // меняем данные
        created.setName("Avatar Updated");

        created.setDescription(
                "New description"
        );
        created.setDuration(200);
        created.setReleaseDate(
                LocalDate.of(2010, 1, 1)
        );
        Mpa mpa = new Mpa();
        mpa.setId(2L);
        mpa.setName("PG");
        created.setMpa(mpa);

        // обновляем
        filmStorage.updateFilm(created);

        // читаем из базы
        Optional<Film> updated =
                filmStorage.getFilmById(created.getId());

        assertThat(updated)
                .isPresent();

        Film result = updated.get();

        assertThat(result.getName())
                .isEqualTo("Avatar Updated");
        assertThat(result.getDescription())
                .isEqualTo("New description");
        assertThat(result.getDuration())
                .isEqualTo(200);
        assertThat(result.getReleaseDate())
                .isEqualTo(LocalDate.of(2010, 1, 1));
        assertThat(result.getMpa())
                .isNotNull();
        assertThat(result.getMpa().getId())
                .isEqualTo(2L);
    }
}