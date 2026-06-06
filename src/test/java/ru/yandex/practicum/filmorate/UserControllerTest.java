package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;
    private User validUser;

    @BeforeEach
    void setUp() {
        controller = new UserController();
        validUser = new User();
        validUser.setEmail("user@example.com");
        validUser.setLogin("validLogin");
        validUser.setName("Valid Name");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));
    }


    @Test
    void createUserWithValidDataShouldGenerateIdAndReturnUser() {
        User created = controller.create(validUser);

        assertNotNull(created.getId());
        assertEquals(1L, created.getId()); // первый пользователь — id=1
        assertEquals("user@example.com", created.getEmail());
        assertEquals("validLogin", created.getLogin());
        assertEquals("Valid Name", created.getName());
        assertEquals(LocalDate.of(2000, 1, 1), created.getBirthday());
    }

    @Test
    void createUserWhenNameIsNullShouldSetNameToLogin() {
        validUser.setName(null);
        User created = controller.create(validUser);

        assertEquals("validLogin", created.getName());
    }

    @Test
    void createUserWhenNameIsBlankShouldSetNameToLogin() {
        validUser.setName("   ");
        User created = controller.create(validUser);

        assertEquals("validLogin", created.getName());
    }

    @Test
    void createUserWithBirthdayTodayShouldSucceed() {
        validUser.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> controller.create(validUser));
    }

    @Test
    void createUserShouldIncrementId() {
        User first = controller.create(validUser);
        User second = new User();
        second.setEmail("second@example.com");
        second.setLogin("secondLogin");
        second.setName("Second");
        second.setBirthday(LocalDate.of(1990, 1, 1));

        User createdSecond = controller.create(second);

        assertEquals(1L, first.getId());
        assertEquals(2L, createdSecond.getId());
    }

    @Test
    void createUserWithNullEmailShouldThrowConditionsNotMetException() {
        validUser.setEmail(null);
        ConditionsNotMetException ex = assertThrows(ConditionsNotMetException.class,
                () -> controller.create(validUser));
        assertTrue(ex.getMessage().contains("Имейл должен быть указан"));
    }

    @Test
    void createUserWithBlankEmailShouldThrowConditionsNotMetException() {
        validUser.setEmail("");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));

        validUser.setEmail("   ");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));
    }

    @Test
    void createUserWithEmailWithoutAtSymbolShouldThrowConditionsNotMetException() {
        validUser.setEmail("userexample.com");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));
    }

    @Test
    void createUserWithLoginNullShouldThrowConditionsNotMetException() {
        validUser.setLogin(null);
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));
    }

    @Test
    void createUserWithLoginContainingSpaceShouldThrowConditionsNotMetException() {
        validUser.setLogin("my login");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));

        validUser.setLogin(" login");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));

        validUser.setLogin("login ");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));
    }

    @Test
    void createUserWithBirthdayInFutureShouldThrowConditionsNotMetException() {
        validUser.setBirthday(LocalDate.now().plusDays(1));
        ConditionsNotMetException ex = assertThrows(ConditionsNotMetException.class,
                () -> controller.create(validUser));
        assertTrue(ex.getMessage().contains("не может быть в будущем"));
    }

    @Test
    void createUserWithBirthdayNullShouldThrowNullPointerException() {
        validUser.setBirthday(null);
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));
    }

    @Test
    void updateUserWithValidDataShouldUpdateExistingUser() {
        User created = controller.create(validUser);
        created.setName("Updated Name");
        created.setLogin("newLogin");

        User updated = controller.update(created);

        assertEquals("Updated Name", updated.getName());
        assertEquals("newLogin", updated.getLogin());
        assertEquals("user@example.com", updated.getEmail());
        assertEquals(LocalDate.of(2000, 1, 1), updated.getBirthday());
    }

    @Test
    void updateUserWithNullIdShouldThrowConditionsNotMetException() {
        validUser.setId(null);
        assertThrows(ConditionsNotMetException.class, () -> controller.update(validUser));
    }

    @Test
    void updateUserWithNonExistentIdShouldThrowNotFoundException() {
        validUser.setId(999L);
        assertThrows(NotFoundException.class, () -> controller.update(validUser));
    }

    @Test
    void updateUserWithBlankEmailShouldThrowConditionsNotMetException() {
        User created = controller.create(validUser);
        created.setEmail("");
        assertThrows(ConditionsNotMetException.class, () -> controller.update(created));
    }

    @Test
    void updateUserWithLoginContainingSpaceShouldThrowConditionsNotMetException() {
        User created = controller.create(validUser);
        created.setEmail("");
        created.setLogin("bad login");
        assertThrows(ConditionsNotMetException.class, () -> controller.update(created));
    }

    @Test
    void updateUserWithBirthdayInFutureShouldThrowConditionsNotMetException() {
        User created = controller.create(validUser);
        created.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ConditionsNotMetException.class, () -> controller.update(created));
    }


    @Test
    void updateUserWithNullEmailShouldNotChangeEmail() {
        User created = controller.create(validUser);
        String oldEmail = created.getEmail();
        User updated = controller.update(created);
        assertEquals(oldEmail, updated.getEmail());
    }

    @Test
    void updateUserWithNullLoginShouldNotChangeLogin() {
        User created = controller.create(validUser);
        String oldLogin = created.getLogin();
        User updated = controller.update(created);
        assertEquals(oldLogin, updated.getLogin());
    }

}