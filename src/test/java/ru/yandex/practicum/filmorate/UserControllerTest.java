package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    @Autowired
    private UserController controller;
    private User validUser;

    @BeforeEach
    void setUp() {

        validUser = new User();
        validUser.setEmail("user@example.com");
        validUser.setLogin("validLogin");
        validUser.setName("Valid Name");
        validUser.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @DirtiesContext
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

    @DirtiesContext
    @Test
    void createUserWhenNameIsNullShouldSetNameToLogin() {
        validUser.setName(null);
        User created = controller.create(validUser);

        assertEquals("validLogin", created.getName());
    }

    @DirtiesContext
    @Test
    void createUserWhenNameIsBlankShouldSetNameToLogin() {
        validUser.setName("   ");
        User created = controller.create(validUser);

        assertEquals("validLogin", created.getName());
    }

    @DirtiesContext
    @Test
    void createUserWithBirthdayTodayShouldSucceed() {
        validUser.setBirthday(LocalDate.now());
        assertDoesNotThrow(() -> controller.create(validUser));
    }

    @DirtiesContext
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

    @DirtiesContext
    @Test
    void createUserWithNullEmailShouldThrowConditionsNotMetException() {
        validUser.setEmail(null);
        ConditionsNotMetException ex = assertThrows(ConditionsNotMetException.class,
                () -> controller.create(validUser));
        assertTrue(ex.getMessage().contains("Имейл должен быть указан"));
    }

    @DirtiesContext
    @Test
    void createUserWithBlankEmailShouldThrowConditionsNotMetException() {
        validUser.setEmail("");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));

        validUser.setEmail("   ");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));
    }

    @DirtiesContext
    @Test
    void createUserWithEmailWithoutAtSymbolShouldThrowConditionsNotMetException() {
        validUser.setEmail("userexample.com");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));
    }

    @DirtiesContext
    @Test
    void createUserWithLoginNullShouldThrowConditionsNotMetException() {
        validUser.setLogin(null);
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));
    }

    @DirtiesContext
    @Test
    void createUserWithLoginContainingSpaceShouldThrowConditionsNotMetException() {
        validUser.setLogin("my login");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));

        validUser.setLogin(" login");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));

        validUser.setLogin("login ");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));
    }

    @DirtiesContext
    @Test
    void createUserWithBirthdayInFutureShouldThrowConditionsNotMetException() {
        validUser.setBirthday(LocalDate.now().plusDays(1));
        ConditionsNotMetException ex = assertThrows(ConditionsNotMetException.class,
                () -> controller.create(validUser));
        assertTrue(ex.getMessage().contains("не может быть в будущем"));
    }

    @DirtiesContext
    @Test
    void createUserWithBirthdayNullShouldThrowNullPointerException() {
        validUser.setBirthday(null);
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validUser));
    }

    @DirtiesContext
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

    @DirtiesContext
    @Test
    void updateUserWithNullIdShouldThrowConditionsNotMetException() {
        validUser.setId(null);
        assertThrows(ConditionsNotMetException.class, () -> controller.update(validUser));
    }

    @DirtiesContext
    @Test
    void updateUserWithNonExistentIdShouldThrowNotFoundException() {
        validUser.setId(999L);
        assertThrows(NotFoundException.class, () -> controller.update(validUser));
    }

    @DirtiesContext
    @Test
    void updateUserWithBlankEmailShouldThrowConditionsNotMetException() {
        User created = controller.create(validUser);
        created.setEmail("");
        assertThrows(ConditionsNotMetException.class, () -> controller.update(created));
    }

    @DirtiesContext
    @Test
    void updateUserWithLoginContainingSpaceShouldThrowConditionsNotMetException() {
        User created = controller.create(validUser);
        created.setEmail("");
        created.setLogin("bad login");
        assertThrows(ConditionsNotMetException.class, () -> controller.update(created));
    }

    @DirtiesContext
    @Test
    void updateUserWithBirthdayInFutureShouldThrowConditionsNotMetException() {
        User created = controller.create(validUser);
        created.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ConditionsNotMetException.class, () -> controller.update(created));
    }

    @DirtiesContext
    @Test
    void updateUserWithNullEmailShouldNotChangeEmail() {
        User created = controller.create(validUser);
        String oldEmail = created.getEmail();
        User updated = controller.update(created);
        assertEquals(oldEmail, updated.getEmail());
    }

    @DirtiesContext
    @Test
    void updateUserWithNullLoginShouldNotChangeLogin() {
        User created = controller.create(validUser);
        String oldLogin = created.getLogin();
        User updated = controller.update(created);
        assertEquals(oldLogin, updated.getLogin());
    }

    @DirtiesContext
    @Test
    void addFriendShouldThrowNotFoundExceptionWhenUserNotFound() {
        // Создаём одного пользователя
        User user = controller.create(validUser);
        Long nonExistentId = 999L;

        // Пытаемся добавить друга с несуществующим id
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> controller.addFriend(user.getId(), nonExistentId));
        assertTrue(ex.getMessage().contains("не найден"));
    }


    @DirtiesContext
    @Test
    void deleteFriendShouldThrowNotFoundExceptionShenUserNotFound() {
        // Создаём одного пользователя
        User user = controller.create(validUser);
        Long nonExistentId = 999L;

        // Пытаемся удалить друга с несуществующим id
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> controller.deleteFriend(user.getId(), nonExistentId));
        assertTrue(ex.getMessage().contains("не найден"));
    }

    @DirtiesContext
    @Test
    void getFriendsShouldReturnListOfFriends() {
        // Создаём двух пользователей и добавляем дружбу
        User user1 = controller.create(validUser);
        User user2 = new User();
        user2.setEmail("friend@example.com");
        user2.setLogin("friendLogin");
        user2.setName("Friend Name");
        user2.setBirthday(LocalDate.of(2001, 1, 1));
        User friend = controller.create(user2);

        controller.addFriend(user1.getId(), friend.getId());

        // Получаем список друзей
        Collection<User> friends = controller.getFriends(user1.getId());

        assertEquals(1, friends.size());
        User firstFriend = friends.iterator().next();
        assertEquals(friend.getId(), firstFriend.getId());
        assertEquals(friend.getLogin(), firstFriend.getLogin());
    }

    @DirtiesContext
    @Test
    void getFriendsShouldThrowNotFoundExceptionWhenUserNotFound() {
        Long nonExistentId = 999L;
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> controller.getFriends(nonExistentId));
        assertTrue(ex.getMessage().contains("не найден"));
    }

    @DirtiesContext
    @Test
    void getCommonFriendsShouldReturnCommonFriends() {
        // Создаём трёх пользователей: user1, user2, commonFriend
        User user1 = controller.create(validUser);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2Login");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(2002, 2, 2));
        User user2Created = controller.create(user2);

        User commonFriend = new User();
        commonFriend.setEmail("common@example.com");
        commonFriend.setLogin("commonLogin");
        commonFriend.setName("Common Friend");
        commonFriend.setBirthday(LocalDate.of(2003, 3, 3));
        User commonFriendCreated = controller.create(commonFriend);

        // Добавляем commonFriend в друзья к user1 и user2
        controller.addFriend(user1.getId(), commonFriendCreated.getId());
        controller.addFriend(user2Created.getId(), commonFriendCreated.getId());

        // Получаем общих друзей
        Collection<User> commonFriends = controller.getCommonFriends(user1.getId(), user2Created.getId());

        assertEquals(1, commonFriends.size());
        User result = commonFriends.iterator().next();
        assertEquals(commonFriendCreated.getId(), result.getId());
        assertEquals(commonFriendCreated.getLogin(), result.getLogin());
    }

    @DirtiesContext
    @Test
    void getCommonFriendsShouldThrowNotFoundExceptionWhenUserNotFound() {
        // Создаём одного пользователя
        User user = controller.create(validUser);
        Long nonExistentId = 999L;

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> controller.getCommonFriends(user.getId(), nonExistentId));
        assertTrue(ex.getMessage().contains("не найден"));
    }
}