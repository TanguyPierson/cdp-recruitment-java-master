package adeo.leroymerlin.cdp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @BeforeEach
    void setup() {
        eventService = new EventService(eventRepository);
    }

    @Test
    void shouldGetEvents() {
        //Arrange
        Event eventReturned = new Event();
        List<Event> events = List.of(eventReturned);
        when(eventRepository.findAllBy()).thenReturn(events);

        //Act
        List<Event> resultEvents = eventService.getEvents();

        //Assert
        assertThat(resultEvents).containsExactly(eventReturned);
    }

    @Test
    void shouldDeleteEvents() {
        //Arrange

        //Act
        eventService.delete(1L);

        //Assert
        verify(eventRepository).deleteById(1L);
    }

    @Test
    void shouldGetFilteredEvents_whenNoEventsInDB() {
        //Arrange
        when(eventRepository.findAllBy()).thenReturn(List.of());

        //Act
        List<Event> filteredEvents = eventService.getFilteredEvents("Wa");

        //Assert
        assertThat(filteredEvents).isEmpty();
    }

    @Test
    void shouldGetFilteredEvents_whenEventsWithNoBands() {
        //Arrange7
        Event notMatchingEvent = new Event();
        notMatchingEvent.setId(1L);

        when(eventRepository.findAllBy())
                .thenReturn(List.of(notMatchingEvent));

        //Act
        List<Event> filteredEvents = eventService.getFilteredEvents("Wa");

        //Assert
        assertThat(filteredEvents).isEmpty();
    }

    @Test
    void shouldGetFilteredEvents_whenEventsWithNoMembers() {
        //Arrange7
        Event notMatchingEvent = new Event();
        notMatchingEvent.setId(1L);

        Band band = new Band();
        band.setId(2L);

        notMatchingEvent.setBands(new HashSet<>(List.of(band)));

        when(eventRepository.findAllBy())
                .thenReturn(List.of(notMatchingEvent));

        //Act
        List<Event> filteredEvents = eventService.getFilteredEvents("Wa");

        //Assert
        assertThat(filteredEvents).isEmpty();
    }

    @Test
    void shouldGetFilteredEvents_withMatchingNameOnMember() {
        //Arrange7
        Event event = new Event();
        event.setId(1L);

        Band band = new Band();
        band.setId(2L);

        Member member = new Member();
        member.setId(3L);
        member.setName("name matching");

        band.setMembers(new HashSet<>(List.of(member)));

        event.setBands(new HashSet<>(List.of(band)));

        when(eventRepository.findAllBy())
                .thenReturn(List.of(event));

        //Act
        List<Event> filteredEvents = eventService.getFilteredEvents("matching");

        //Assert
        assertThat(filteredEvents).extracting(Event::getId).containsExactly(1L);
    }

    @Test
    void shouldGetFilteredEvents_withNoNameOnMember() {
        //Arrange7
        Event notMatchingEvent = new Event();
        notMatchingEvent.setId(1L);

        Band band = new Band();
        band.setId(2L);

        Member member = new Member();
        member.setId(3L);

        band.setMembers(new HashSet<>(List.of(member)));

        notMatchingEvent.setBands(new HashSet<>(List.of(band)));

        when(eventRepository.findAllBy())
                .thenReturn(List.of(notMatchingEvent));

        //Act
        List<Event> filteredEvents = eventService.getFilteredEvents("matching");

        //Assert
        assertThat(filteredEvents).isEmpty();
    }

    @Test
    void shouldGetFilteredEvents_withQueryIsBlankOnMember() {
        //Arrange7
        Event notMatchingEvent = new Event();
        notMatchingEvent.setId(1L);

        Band band = new Band();
        band.setId(2L);

        Member member = new Member();
        member.setId(3L);
        member.setName("not matching");

        band.setMembers(new HashSet<>(List.of(member)));

        notMatchingEvent.setBands(new HashSet<>(List.of(band)));

        when(eventRepository.findAllBy())
                .thenReturn(List.of(notMatchingEvent));

        //Act
        List<Event> filteredEvents = eventService.getFilteredEvents("");

        //Assert
        assertThat(filteredEvents).extracting(Event::getId).containsExactly(1L);
    }

    @Test
    void shouldGetFilteredEvents_withCountersOnEventTitleAndBandName() {
        //Arrange7
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Title");

        Band band = new Band();
        band.setId(2L);
        band.setName("band_name");

        Member member = new Member();
        member.setId(3L);
        member.setName("name matching");

        band.setMembers(new HashSet<>(List.of(member)));

        event.setBands(new HashSet<>(List.of(band)));

        when(eventRepository.findAllBy())
                .thenReturn(List.of(event));

        //Act
        List<Event> filteredEvents = eventService.getFilteredEvents("matching");

        //Assert
        assertThat(filteredEvents).extracting(Event::getId).containsExactly(1L);
        assertThat(filteredEvents.get(0).getTitle()).isEqualTo("Title [1]");
        assertThat(filteredEvents.get(0).getBands().iterator().next().getName()).isEqualTo("band_name [1]");
    }

    @Test
    void shouldUpdateEvent() {
        //Arrange
        Event eventToUpdate = new Event();

        //Act
        eventService.updateEvent(eventToUpdate);

        //Assert
        verify(eventRepository).save(eventToUpdate);
    }

}