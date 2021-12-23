package adeo.leroymerlin.cdp;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface EventRepository extends CrudRepository<Event, Long> {

    void deleteById(Long eventId);

    List<Event> findAllBy();
}
