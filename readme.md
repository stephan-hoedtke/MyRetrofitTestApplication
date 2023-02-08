# MyRetrofitTestApplication

Testing Android with

- Dagger Hilt
- Retrofit
- GsonConverterFactory
- Jackson Object Mapper and JacksonConverterFactory to handle GeoJson
- Unit tests for Retrofit and the converters
- Resource files to supply fake response for mockWebServer

### Follow installation steps on

- for Dagger / Hilt:
  <br> [Google Android Developer Training for Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

- for Retrofit:
  <br> [Google Android Developer Training for Retrofit](https://developer.android.com/codelabs/basic-android-kotlin-training-getting-data-internet#7)
  <br> [Vogella](https://www.vogella.com/tutorials/Retrofit/article.html)

- for Testing Retrofit with Mock Web Server:
  <br> [Sachin Kumar](https://sachinkmr375.medium.com/unit-test-retrofit-api-calls-with-mockwebserver-bbb9f66a78a6)
  <br> [Daniel Horowitz](https://proandroiddev.com/testing-retrofit-converter-with-mock-webserver-50f3e1f54013)
  <br> [Testim](https://www.testim.io/blog/how-to-use-mockwebserver/)

- for Geo Json Serialization
  <br> [John Codeos](https://johncodeos.com/how-to-parse-json-with-retrofit-converters-using-kotlin/)
  <br> [filosganga/geogson](https://github.com/filosganga/geogson)

### Links:

- [Markdown](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax)

### Code example:

```
val mapper = jacksonObjectMapper()
    .registerModule(JavaTimeModule());

api = Retrofit.Builder()
   .baseUrl(mockWebServer.url("/points/1/"))
   .client(client)
   .addConverterFactory(JacksonConverterFactory.create(mapper))
   .build()
   .create(PointFormFeatureApi::class.java)
```
