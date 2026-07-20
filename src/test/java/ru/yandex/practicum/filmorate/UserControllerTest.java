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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private UserController controller;

    private User validUser;

    private String uniqueEmail() {
        return "user_" + UUID.randomUUID() + "@mail.com";
    }

    private String uniqueLogin() {
        return "login_" + UUID.randomUUID();
    }

    private String uniqueName() {
        return "name_" + UUID.randomUUID();
    }

    private User createTestUser() {

        User user = new User();
        user.setEmail(uniqueEmail());
        user.setLogin(uniqueLogin());
        user.setName(uniqueName());
        user.setBirthday(
                LocalDate.of(2000, 1, 1)
        );
        return user;
    }

    @BeforeEach
    void setUp() {

        validUser = createTestUser();

    }

    @Test
    void createUserWithValidDataShouldGenerateId() {

        User created =
                controller.create(validUser);

        assertNotNull(created.getId());
        assertTrue(created.getId() > 0);
        assertNotNull(created.getEmail());

        assertNotNull(created.getLogin());
    }


    @Test
    void createUserWhenNameIsNullShouldSetLogin() {

        validUser.setName(null);
        User created =
                controller.create(validUser);
        assertEquals(
                created.getLogin(),
                created.getName());
    }

    @Test
    void createUserWithInvalidEmailShouldThrow() {
        validUser.setEmail("wrongEmail");

        assertThrows(
                ConditionsNotMetException.class,
                () -> controller.create(validUser));
    }

    @Test
    void createUserWithEmptyLoginShouldThrow() {

        validUser.setLogin("");

        assertThrows(
                ConditionsNotMetException.class,
                () -> controller.create(validUser));
    }

    @Test
    void createUserWithFutureBirthdayShouldThrow() {

        validUser.setBirthday(
                LocalDate.now().plusDays(1));

        assertThrows(
                ConditionsNotMetException.class,
                () -> controller.create(validUser));
    }


    @Test
    void updateUserShouldChangeData() {

        User created =
                controller.create(validUser);
        created.setName(
                "Updated_" + UUID.randomUUID());
        User updated =
                controller.update(created);
        assertEquals(
                created.getName(),
                updated.getName()
        );
    }

    @Test
    void updateUserWithoutIdShouldThrow() {

        validUser.setId(null);
        assertThrows(
                ConditionsNotMetException.class,
                () -> controller.update(validUser));
    }

    @Test
    void updateUnknownUserShouldThrow() {

        validUser.setId(999999L);

        assertThrows(
                NotFoundException.class,
                () -> controller.update(validUser));
    }


    @Test
    void addFriendShouldWork() {

        User user1 =
                controller.create(createTestUser());
        User user2 =
                controller.create(createTestUser());

        controller.addFriend(
                user1.getId(),
                user2.getId());

        List<User> friends =
                controller.getFriends(user1.getId());

        assertEquals(
                1,
                friends.size());
        assertEquals(
                user2.getId(),
                friends.get(0).getId());
    }


    @Test
    void deleteFriendShouldWork() {

        User user1 =
                controller.create(createTestUser());
        User user2 =
                controller.create(createTestUser());

        controller.addFriend(
                user1.getId(),
                user2.getId());
        controller.deleteFriend(
                user1.getId(),
                user2.getId());

        List<User> friends =
                controller.getFriends(user1.getId());
        assertTrue(
                friends.isEmpty());
    }

    @Test
    void getFriendsUnknownUserShouldThrow() {

        assertThrows(
                NotFoundException.class,
                () -> controller.getFriends(999999L));
    }


    @Test
    void getCommonFriendsShouldReturnFriend() {

        User user1 =
                controller.create(createTestUser());
        User user2 =
                controller.create(createTestUser());
        User common =
                controller.create(createTestUser());
        controller.addFriend(
                user1.getId(),
                common.getId());
        controller.addFriend(
                user2.getId(),
                common.getId());
        List<User> result =
                controller.getCommonFriends(
                        user1.getId(),
                        user2.getId());
        assertEquals(
                1,
                result.size());
        assertEquals(
                common.getId(),
                result.get(0).getId());
    }
}