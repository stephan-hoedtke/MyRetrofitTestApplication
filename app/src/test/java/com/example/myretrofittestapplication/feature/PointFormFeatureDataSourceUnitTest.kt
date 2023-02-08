package com.example.myretrofittestapplication.feature

import com.example.myretrofittestapplication.ResourceFileReader
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.geojson.Point
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.jackson.JacksonConverterFactory
import java.net.HttpURLConnection
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

/**
 * For Feature<T> the GsonConverterFactory without extra TypeAdapters will fail.
 * However, using the jacksonObjectMapper works very well.
 *
 * Please note, the PointFormFeatureApi is not used anywhere in the app, just here,
 * We are faking the json response from mockWebServer.
 * Hence no real web service is called.
 *
 * Mind, the jacksonObjectMapper requires dependencies in build.gradle:
 *      implementation 'de.grundid.opendatalab:geojson-jackson:1.14'
 *      implementation 'com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3'
 *      ...
 */
class PointFormFeatureDataSourceUnitTest {

    private val mockWebServer = MockWebServer()

    private lateinit var api: PointFormFeatureApi

    @Before
    fun setUp() {
        mockWebServer.start()

        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        val mapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule());

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/points/1/"))
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
            .create(PointFormFeatureApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `deserialization with Jackson can handle Feature of PointForm`() {
        // given
        val json: String = "{" +
                "  \"type\": \"Feature\"," +
                "  \"id\": 17," +
                "  \"geometry\": {" +
                "    \"type\": \"Point\"," +
                "    \"coordinates\": [" +
                "      52.1," +
                "      13.2" +
                "    ]" +
                "  }," +
                "  \"properties\": {" +
                "    \"id\": 17," +
                "    \"name\": \"Home\"," +
                "    \"description\": \"Somewhere around Berlin\"," +
                "    \"creationDate\": \"2023-02-08T07:12:00+02\"," +
                "    \"creatorName\": \"STHO\"" +
                "  }" +
                "}"

        // when
        val actualFeature: Feature<PointForm> = deserializeWithJackson(json);

        // then
        assertEquals("Feature", actualFeature.type)
        assertEquals(17, actualFeature.id)
        assertEquals(17, actualFeature.properties.id)
        assertEquals("Home", actualFeature.properties.name)
        assertEquals("Somewhere around Berlin", actualFeature.properties.description)
        assertEquals("STHO", actualFeature.properties.creatorName)
        // TODO: continue here... geometry is Point, Date is ...
    }

    @Test
    fun `serialization with Jackson can handle Feature of PointForm`() {
        // given
        val feature = Feature<PointForm>(
            id = 17,
            type = "Feature",
            geometry = Point(13.2, 52.1),
            properties = PointForm(
                id = 17,
                name = "Home",
                description = "Somewhere around Berlin",
                creationDate = ZonedDateTime.of(2023, 2, 8, 7, 12, 0, 0, ZoneId.of("CET")),
                creatorName = "STHO",
            ),
        )

        // when
        val actualJson: String = serializeWithJackson(feature);

        // then
        assertTrue(actualJson.contains("Somewhere around Berlin"))
        assertTrue(actualJson.contains("Point"))
    }


    @Test
    fun `check MockResponseFileReader can read a json file from resources`() {
        // when
        val content = ResourceFileReader.read(SUCCESS_POINTFORM_FEATURE_RESPONSE_JSON)

        // then
        assertNotNull(content)
    }


    @Test
    fun `deserialization and serialization works with supplied response file`() {
        // given
        val json: String = ResourceFileReader.read(SUCCESS_POINTFORM_FEATURE_RESPONSE_JSON)

        // when
        val feature: Feature<PointForm> = deserializeWithJackson(json);
        assertNotNull(feature)

        val json2: String = serializeWithJackson(feature)
        assertNotNull(json2)

        // then
        assertTrue(json2.contains("Somewhere around Berlin"))
        assertTrue(json2.contains("Point"));
    }

    @Test
    fun `fetch details and check response code 200 returned`() {
        // Given
        val body = withBodyOf(SUCCESS_POINTFORM_FEATURE_RESPONSE_JSON)
        val response = withMockResponseOf(body, HttpURLConnection.HTTP_OK)

        mockWebServer.enqueue(response)

        runBlocking {
            // When
            val actualResponse = api.getOne().awaitResponse()

            // Then
            assertTrue(actualResponse.code().toString().contains("200"))
        }
    }

    @Test
    fun `fetch details and check response success returned`() {
        // Given
        val body: String = withBodyOf(SUCCESS_POINTFORM_FEATURE_RESPONSE_JSON)
        val response = withMockResponseOf(body, HttpURLConnection.HTTP_OK)
        val expectedResponse: Feature<PointForm> = deserializeWithJackson(body)

        mockWebServer.enqueue(response)

        runBlocking {
            // When
            val actualResponse: Response<Feature<PointForm>> = api.getOne().execute()
            val actualResponseFeature: Feature<PointForm>? = actualResponse.body()

            // Then
            assertEquals(expectedResponse, actualResponseFeature)
        }
    }


    companion object {
        private const val SUCCESS_POINTFORM_FEATURE_RESPONSE_JSON = "success_pointform_feature_response.json"

        @Suppress("SameParameterValue")
        private fun withBodyOf(path: String): String {
            return ResourceFileReader.read(path)
        }

        @Suppress("SameParameterValue")
        private fun withMockResponseOf(body: String, code: Int = HttpURLConnection.HTTP_OK): MockResponse {
            return MockResponse()
                .setResponseCode(code)
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
        }

        /**
         * In a real application we would use a generic type parameter instead of "PointForm" for flexibility.
         */
        private fun serializeWithJackson(feature: Feature<PointForm>): String =
            jacksonObjectMapper()
                .registerModule(JavaTimeModule())
                .writeValueAsString(feature)

        /**
         * In a real application we would use a generic type parameter instead of "PointForm" for flexibility.
         */
        private fun deserializeWithJackson(json: String): Feature<PointForm> =
            jacksonObjectMapper() // to deserialize GeoJson
                .registerModule(JavaTimeModule()) // required to deserialize ZonedDateTime
                .readValue(json, object : TypeReference<Feature<PointForm>>() {}) // because of the generic type Feature<>
    }
}
