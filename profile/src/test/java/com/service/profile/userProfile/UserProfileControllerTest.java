package com.service.profile.userProfile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserProfileControllerTest {

    private static final String BASE_PATH = "/api/v1/profiles";
    private static final String OWNER_ID = "owner-1234";
    private static final String OTHER_OWNER_ID = "owner-5678";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @BeforeEach
    void clean() {
        userProfileRepository.deleteAll();
    }

    private UserProfile persistProfile(String id, String displayName, String bio, String avatarUrl) {
        return userProfileRepository.save(UserProfile.builder()
                .id(id)
                .displayName(displayName)
                .bio(bio)
                .avatarUrl(avatarUrl)
                .build());
    }

    // ---------- GET /me ----------

    @Test
    void meReturns200WithProfileWhenExists() throws Exception {
        persistProfile(OWNER_ID, "Alice", "hello there", "http://example.com/a.png");

        mockMvc.perform(get(BASE_PATH + "/me").with(jwt().jwt(j -> j.subject(OWNER_ID))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(OWNER_ID))
                .andExpect(jsonPath("$.displayName").value("Alice"))
                .andExpect(jsonPath("$.bio").value("hello there"))
                .andExpect(jsonPath("$.avatarUrl").value("http://example.com/a.png"))
                .andExpect(jsonPath("$.version").value(0))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void meReturns404WhenProfileDoesNotExist() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/me").with(jwt().jwt(j -> j.subject(OWNER_ID))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("profile not found: " + OWNER_ID));
    }

    @Test
    void meReturns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meReturnsOnlyCallersOwnProfileNotAnotherOwners() throws Exception {
        persistProfile(OWNER_ID, "Alice", "alice bio", "http://example.com/a.png");
        persistProfile(OTHER_OWNER_ID, "Bob", "bob bio", "http://example.com/b.png");

        mockMvc.perform(get(BASE_PATH + "/me").with(jwt().jwt(j -> j.subject(OTHER_OWNER_ID))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(OTHER_OWNER_ID))
                .andExpect(jsonPath("$.displayName").value("Bob"));
    }

    // ---------- PUT /me ----------

    @Test
    void putReturns201WhenProfileDoesNotExist() throws Exception {
        String body = """
                {"displayName":"Bob","avatarUrl":"http://example.com/b.png","bio":"a new bio"}
                """;

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(OWNER_ID))
                .andExpect(jsonPath("$.displayName").value("Bob"))
                .andExpect(jsonPath("$.avatarUrl").value("http://example.com/b.png"))
                .andExpect(jsonPath("$.bio").value("a new bio"))
                .andExpect(jsonPath("$.version").value(0));

        Optional<UserProfile> saved = userProfileRepository.findById(OWNER_ID);
        assertTrue(saved.isPresent());
        assertEquals("Bob", saved.get().getDisplayName());
    }

    @Test
    void putReturns200AndIncrementsVersionWhenProfileExists() throws Exception {
        UserProfile existing = persistProfile(OWNER_ID, "Old Name", "old bio", "http://example.com/old.png");
        Instant originalUpdatedAt = existing.getUpdatedAt();

        String body = """
                {"displayName":"New Name","avatarUrl":"http://example.com/new.png","bio":"new bio"}
                """;

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(OWNER_ID))
                .andExpect(jsonPath("$.displayName").value("New Name"))
                .andExpect(jsonPath("$.avatarUrl").value("http://example.com/new.png"))
                .andExpect(jsonPath("$.bio").value("new bio"))
                .andExpect(jsonPath("$.version").value(1));

        UserProfile reloaded = userProfileRepository.findById(OWNER_ID).orElseThrow();
        assertEquals(1, reloaded.getVersion());
        assertNotEquals(originalUpdatedAt, reloaded.getUpdatedAt());
    }

    @Test
    void putSecondCallWithSameBodyIsIdempotentAndDoesNotBumpVersion() throws Exception {
        // UserProfile#touch() is annotated @PrePersist/@PreUpdate (UserProfile.java lines 55-59):
        // per the JPA lifecycle contract @PreUpdate only fires immediately before an actual
        // UPDATE statement is issued. Hibernate's dirty checking skips the UPDATE entirely
        // when a flush detects no changed attributes, so re-submitting the identical payload
        // must not touch updatedAt or bump the @Version column.
        String body = """
                {"displayName":"Same Name","avatarUrl":"http://example.com/same.png","bio":"same bio"}
                """;

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.version").value(0));

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Same Name"))
                .andExpect(jsonPath("$.version").value(0));
    }

    @Test
    void putReturns401WhenUnauthenticated() throws Exception {
        String body = """
                {"displayName":"Bob","avatarUrl":null,"bio":null}
                """;

        mockMvc.perform(put(BASE_PATH + "/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void putReturns400WhenDisplayNameBlank() throws Exception {
        String body = """
                {"displayName":" ","avatarUrl":null,"bio":null}
                """;

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.displayName").value("name must not be blank"));
    }

    @Test
    void putReturns400WhenDisplayNameContainsInvalidCharacters() throws Exception {
        String body = """
                {"displayName":"Invalid@Name","avatarUrl":null,"bio":null}
                """;

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.displayName").value("name contains invalid characters"));
    }

    @Test
    void putReturns400WhenDisplayNameOverMaxLength() throws Exception {
        String tooLong = "A".repeat(256);
        String body = String.format("{\"displayName\":\"%s\",\"avatarUrl\":null,\"bio\":null}", tooLong);

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.displayName").value("name must be at most 255 characters"));
    }

    @Test
    void putReturns201WhenDisplayNameAtMaxLength() throws Exception {
        String atLimit = "A".repeat(255);
        String body = String.format("{\"displayName\":\"%s\",\"avatarUrl\":null,\"bio\":null}", atLimit);

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName").value(atLimit));
    }

    @Test
    void putReturns400WhenAvatarUrlOverMaxLength() throws Exception {
        String tooLong = "a".repeat(2049);
        String body = String.format("{\"displayName\":\"Bob\",\"avatarUrl\":\"%s\",\"bio\":null}", tooLong);

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.avatarUrl").value("avatarUrl must be at most 2048 characters"));
    }

    @Test
    void putReturns201WhenAvatarUrlAtMaxLength() throws Exception {
        String atLimit = "a".repeat(2048);
        String body = String.format("{\"displayName\":\"Bob\",\"avatarUrl\":\"%s\",\"bio\":null}", atLimit);

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.avatarUrl").value(atLimit));
    }

    @Test
    void putReturns400WhenBioOverMaxLength() throws Exception {
        String tooLong = "a".repeat(4097);
        String body = String.format("{\"displayName\":\"Bob\",\"avatarUrl\":null,\"bio\":\"%s\"}", tooLong);

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.bio").value("bio must be at most 4096 characters"));
    }

    @Test
    void putReturns201WhenBioAtMaxLength() throws Exception {
        String atLimit = "a".repeat(4096);
        String body = String.format("{\"displayName\":\"Bob\",\"avatarUrl\":null,\"bio\":\"%s\"}", atLimit);

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bio").value(atLimit));
    }

    @Test
    void putUpdatingOneOwnerDoesNotAffectAnotherOwnersProfile() throws Exception {
        persistProfile(OTHER_OWNER_ID, "Untouched", "untouched bio", "http://example.com/untouched.png");

        String body = """
                {"displayName":"Mine","avatarUrl":null,"bio":null}
                """;

        mockMvc.perform(put(BASE_PATH + "/me")
                        .with(jwt().jwt(j -> j.subject(OWNER_ID)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(OWNER_ID));

        UserProfile other = userProfileRepository.findById(OTHER_OWNER_ID).orElseThrow();
        assertEquals("Untouched", other.getDisplayName());
        assertEquals(0, other.getVersion());
    }
}
