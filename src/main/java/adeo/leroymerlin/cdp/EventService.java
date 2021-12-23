package adeo.leroymerlin.cdp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAllBy();
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> getFilteredEvents(String query) {
        List<Event> events = filterAndGetEvents(query, eventRepository.findAllBy());

        updateWithCounters(events);

        return events;
    }

    public void updateEvent(Event event) {
        eventRepository.save(event);
    }

    private void updateWithCounters(List<Event> events) {
        for (Event event : events) {
            event.setTitle(format("%s [%s]", getReturnableValue(event.getTitle()), event.getBands().size()));
            updateBrandNames(event);
        }
    }

    private void updateBrandNames(Event event) {
        for (Band band : event.getBands()) {
            band.setName(format("%s [%s]", getReturnableValue(band.getName()), band.getMembers().size()));
        }
    }

    private String getReturnableValue(String valueToCheck) {
        if (valueToCheck != null) {
            return valueToCheck;
        }
        return "";
    }

    private List<Event> filterAndGetEvents(String query, List<Event> events) {
        return events.stream()
                .filter(event -> hasMatchingMember(query, event))
                .collect(Collectors.toList());
    }

    private boolean hasMatchingMember(String query, Event event) {
        if (event.getBands() != null) {
            return event.getBands().stream()
                    .anyMatch(band -> hasMemberWithMatchingName(query, band));
        }
        return false;
    }

    private boolean hasMemberWithMatchingName(String query, Band band) {
        if (band.getMembers() != null) {
            return band.getMembers().stream()
                    .anyMatch(member -> hasNameWithQuery(query, member));
        }
        return false;
    }

    private boolean hasNameWithQuery(String query, Member member) {
        if (member.getName() != null) {
            return member.getName().contains(query);
        }
        return false;
    }
}
