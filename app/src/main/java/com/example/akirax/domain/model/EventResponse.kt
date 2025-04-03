import com.example.akirax.domain.model.Attraction
import com.example.akirax.domain.model.Venue

data class EventResponse(
    val _embedded: EmbeddedEvents?
)

data class EmbeddedEvents(
    val events: List<Event>?
)

data class Event(
    val id: String,
    val name: String,
    val images: List<Image>?,
    val description: String?,
    val dates: EventDates,
    val _embedded: EmbeddedVenues?
)

data class EventDates(
    val start: EventStart
)

data class EventStart(
    val localDate: String
)

data class EmbeddedVenues(
    val venues: List<Venue>?
)

data class EmbeddedAttractions(
    val attractions: List<Attraction>?
)

data class Image(
    val url: String
)