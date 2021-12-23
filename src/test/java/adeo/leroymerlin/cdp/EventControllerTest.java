package adeo.leroymerlin.cdp;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles("test")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void shouldGetEvents() throws Exception {
        //Arrange
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Title");
        event.setImgUrl("imgUrl");
        event.setNbStars(12);
        event.setComment("new_comment");

        Band band = new Band();
        band.setId(2L);
        band.setName("band_name");

        Member member = new Member();
        member.setId(3L);
        member.setName("member_name");

        band.setMembers(new HashSet<>(List.of(member)));

        event.setBands(new HashSet<>(List.of(band)));

        when(eventService.getEvents()).thenReturn(List.of(event));

        //Act
        ResultActions resultActions = mockMvc.perform(get("/api/events/"));

        //Assert
        resultActions.andExpect(status().isOk());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        assertEquals("""
                [
                    {
                        "id": 1,
                        "title": "Title",
                        "imgUrl": "imgUrl",
                        "bands": [
                            {
                                "id": 2,
                                "name": "band_name",
                                "members": [
                                    {
                                        "id": 3,
                                        "name": "member_name"
                                    }
                                ]
                            }
                        ],
                        "nbStars": 12,
                        "comment": "new_comment"
                    }
                ]
                """, contentAsString, true);
    }

    @Test
    void shouldGetEventsFiltered_withQuery() throws Exception {
        //Arrange
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Title");
        event.setImgUrl("imgUrl");
        event.setNbStars(12);
        event.setComment("new_comment");

        Band band = new Band();
        band.setId(2L);
        band.setName("band_name");

        Member member = new Member();
        member.setId(3L);
        member.setName("member_name");

        band.setMembers(new HashSet<>(List.of(member)));

        event.setBands(new HashSet<>(List.of(band)));

        when(eventService.getFilteredEvents("Query")).thenReturn(List.of(event));

        //Act
        ResultActions resultActions = mockMvc.perform(get("/api/events/search/Query"));

        //Assert
        resultActions.andExpect(status().isOk());
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        assertEquals("""
                [
                    {
                        "id": 1,
                        "title": "Title",
                        "imgUrl": "imgUrl",
                        "bands": [
                            {
                                "id": 2,
                                "name": "band_name",
                                "members": [
                                    {
                                        "id": 3,
                                        "name": "member_name"
                                    }
                                ]
                            }
                        ],
                        "nbStars": 12,
                        "comment": "new_comment"
                    }
                ]
                """, contentAsString, true);
    }

    @Test
    void shouldDeleteEvent() throws Exception {
        //Arrange

        //Act
        ResultActions resultActions = mockMvc.perform(delete("/api/events/1"));

        //Assert
        resultActions.andExpect(status().isOk());

        verify(eventService).delete(1L);
    }

    @Test
    void shouldReturnOk200_whenUpdateEvent() throws Exception {
        //Arrange

        //Act
        ResultActions resultActions = mockMvc.perform(
                put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                    "id": 1,
                                    "title": "Title",
                                    "imgUrl": "imgUrl",
                                    "bands": [
                                        {
                                            "id": 2,
                                            "name": "band_name",
                                            "members": [
                                                {
                                                    "id": 3,
                                                    "name": "member_name"
                                                }
                                            ]
                                        }
                                    ],
                                    "nbStars": 12,
                                    "comment": "new_comment"
                                }
                                """)
        );

        //Assert
        resultActions.andExpect(status().isOk());

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventService).updateEvent(captor.capture());

        Event value = captor.getValue();
        assertThat(value.getId()).isEqualTo(1L);
        assertThat(value.getTitle()).isEqualTo("Title");
        assertThat(value.getImgUrl()).isEqualTo("imgUrl");
        assertThat(value.getNbStars()).isEqualTo(12);
        assertThat(value.getComment()).isEqualTo("new_comment");
        assertThat(value.getBands()).hasSize(1);

        Band firstBrand = value.getBands().iterator().next();
        assertThat(firstBrand.getId()).isEqualTo(2L);
        assertThat(firstBrand.getName()).isEqualTo("band_name");
        assertThat(firstBrand.getMembers()).hasSize(1);

        Member firstMember = firstBrand.getMembers().iterator().next();
        assertThat(firstMember.getId()).isEqualTo(3L);
        assertThat(firstMember.getName()).isEqualTo("member_name");
    }

}