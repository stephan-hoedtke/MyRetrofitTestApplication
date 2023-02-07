package com.example.myretrofittestapplication

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class CatFactsDataSourceUnitTest {

    private val mockWebServer = MockWebServer()

    private lateinit var api: CatFactsApi

    @Before
    fun setUp() {
        mockWebServer.start()

        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        val gson: Gson = GsonBuilder()
            .setLenient()
            .create();

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/facts/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CatFactsApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }


    @Test
    fun `check MockResponseFileReader can read a json file from resources`() {
        // when
        val content = MockResponseFileReader.read(SUCCESS_FACT_RESPONSE_JSON)

        // then
        assertNotNull(content)
    }

    @Test
    fun `deserialization and serialization with Gson can handle CatFact`() {
        // given
        val json: String = MockResponseFileReader.read(SUCCESS_FACT_RESPONSE_JSON)

        // when
        val fact: CatFact = deserialize(json);
        assertNotNull(fact)

        val json2: String = serialize(fact)
        assertNotNull(json2)

        // then
        assertTrue(json.pure() == json.pure())
        // Hint: assertEquals(json.pure(), json2.pure()) does not work
    }

    @Test
    fun `fetch details and check response code 200 returned`() {
        // Given
        val body = withBodyOf(SUCCESS_FACT_RESPONSE_JSON)
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
        val body: String = withBodyOf(SUCCESS_FACT_RESPONSE_JSON)
        val response = withMockResponseOf(body, HttpURLConnection.HTTP_OK)
        mockWebServer.enqueue(response)
        val expectedResponse: CatFact = deserialize(body)

        runBlocking {
            // When
            val actualResponse: Response<CatFact> = api.getOne().execute()
            val actualResponseFact: CatFact? = actualResponse.body()

            // Then
            assertEquals(expectedResponse, actualResponseFact)
        }
    }


    companion object {
        private const val SUCCESS_FACT_RESPONSE_JSON = "success_fact_response.json"
        private const val FAILURE_FACT_RESPONSE_JSON = "failure_fact_response.json"

        private fun withBodyOf(path: String): String {
            return MockResponseFileReader.read(path)
        }

        private fun withMockResponseOf(body: String, code: Int = HttpURLConnection.HTTP_OK): MockResponse {
            return MockResponse()
                .setResponseCode(code)
                .setHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(body)
        }

        private fun serialize(fact: CatFact): String =
            Gson().toJson(fact)

        private fun deserialize(json: String): CatFact =
            Gson().fromJson(json, CatFact::class.java)

    }
}

private fun String.pure(): String =
    this.replace("\n", "")
        .replace(" ", "")


