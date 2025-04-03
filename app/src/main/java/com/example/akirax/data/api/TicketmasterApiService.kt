import com.example.akirax.domain.model.AttractionResponse
import com.example.akirax.domain.model.VenueResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketmasterApiService {
    @GET("events")
    suspend fun getMusicEvents(
        @Query("apikey") apiKey: String,
        @Query("classificationName") classificationName: String = "Music",
        @Query("size") size: Int = 50
    ): Response<EventResponse>

    @GET("events")
    suspend fun getSportsEvents(
        @Query("apikey") apiKey: String,
        @Query("classificationName") classificationName: String = "Sports",
        @Query("size") size: Int = 10
    ): Response<EventResponse>

    @GET("venues")
    suspend fun getNearbyVenues(
        @Query("apikey") apiKey: String,
        @Query("countryCode") countryCode: String = "us",  // Filter for US venues
       // @Query("keyword") keyword: String = "Los Angeles", // Add a keyword to filter results
        //@Query("geoPoint") geoPoint: String = "34.052235,-118.243683", // Geo location for LA
        @Query("size") size: Int = 10,
      //  @Query("sort") sort: String = "distance,asc" // Sort by distance
    ): Response<VenueResponse>


    @GET("attractions")
    suspend fun getAttractions(
        @Query("apikey") apiKey: String,
        @Query("size") size: Int = 10
    ): Response<AttractionResponse>
}





