package dev.dpham.transactional.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dpham.transactional.rest.model.BookingDto;
import dev.dpham.transactional.rest.model.PersonDto;
import dev.dpham.transactional.rest.model.RoomDto;
import dev.dpham.transactional.service.BookingService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "logging.level.org.springframework.transaction=DEBUG",
        "logging.level.org.springframework.orm.jpa=DEBUG"
})
@Testcontainers
@SqlGroup({@Sql(scripts = {"classpath:setup.sql"}, executionPhase = BEFORE_TEST_METHOD)})
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private BookingService bookingService;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14.9")
            .withDatabaseName("hotel")
                    .withUsername("postgres")
                    .withPassword("password")
                    .withExposedPorts(5432);

    @Test
    void createBooking_givenSimultaneousConflictingRequests_fastestWins() throws Exception {
        CompletableFuture<MockHttpServletResponse> frodo = postAsync("/booking", BookingDto.builder()
                .id(1L)
                .room(RoomDto.builder().id(1L).name("Rivendell").build())
                .person(PersonDto.builder().id(1L).name("Frodo").build())
                .fromDay(LocalDate.now())
                .toDay(LocalDate.now().plusDays(3))
                .build());
        CompletableFuture<MockHttpServletResponse> sam = postAsync("/booking", BookingDto.builder()
                .id(2L)
                .room(RoomDto.builder().id(1L).name("Rivendell").build())
                .person(PersonDto.builder().id(2L).name("Sam").build())
                .fromDay(LocalDate.now())
                .toDay(LocalDate.now().plusDays(4))
                .build());
        CompletableFuture.allOf(frodo, sam).join();
        MockHttpServletResponse frodoResponse = frodo.get();
        MockHttpServletResponse samResponse = sam.get();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(List.of(frodoResponse.getStatus(), samResponse.getStatus()))
                .contains(HttpStatus.OK.value(), HttpStatus.CONFLICT.value());
        soft.assertThatCode(()-> verify(bookingService, times(2)).createNewBookingWithLock(any()))
                .doesNotThrowAnyException();
        soft.assertAll();
    }

    @Test
    void createBooking_givenSimultaneousConflictingRequests_fastestWinsOtherRetries() throws Exception {
        CompletableFuture<MockHttpServletResponse> frodo = postAsync("/booking_serializable", BookingDto.builder()
                .id(1L)
                .room(RoomDto.builder().id(1L).name("Rivendell").build())
                .person(PersonDto.builder().id(1L).name("Frodo").build())
                .fromDay(LocalDate.now())
                .toDay(LocalDate.now().plusDays(3))
                .build());
        CompletableFuture<MockHttpServletResponse> sam = postAsync("/booking_serializable", BookingDto.builder()
                .id(2L)
                .room(RoomDto.builder().id(1L).name("Rivendell").build())
                .person(PersonDto.builder().id(2L).name("Sam").build())
                .fromDay(LocalDate.now())
                .toDay(LocalDate.now().plusDays(4))
                .build());
        CompletableFuture.allOf(frodo, sam).join();
        MockHttpServletResponse frodoResponse = frodo.get();
        MockHttpServletResponse samResponse = sam.get();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(List.of(frodoResponse.getStatus(), samResponse.getStatus()))
                .contains(HttpStatus.OK.value(), HttpStatus.CONFLICT.value());
        soft.assertThatCode(()-> verify(bookingService, Mockito.atLeast(3)).createNewBookingWithSerializableIsolation(any()))
                .doesNotThrowAnyException();
        soft.assertAll();
    }

    private CompletableFuture<MockHttpServletResponse> postAsync(String uri, Object object) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return mockMvc.perform(post(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(object)))
                        .andReturn().getResponse();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}