package `in`.mercuryai.chat.data.`object`//object ApiUtilities {


//    val headers = mapOf<String, String>(
//        "Accept" to "apllication/json",
//        "x-rapidapi-key" to "484d703f5emsh62e6197a6afd87ep1573c6jsn2e8932281954",
//        "x-rapidapi-host" to "bhagavad-gita3.p.rapidapi.com"
//    )
//
//
//    val client = OkHttpClient.Builder().apply {
//        addInterceptor { chain ->
//
//            val newRequest = chain.request().newBuilder().apply {
//                headers.forEach { (key, value) -> addHeader(key, value) }
//            }.build()
//
//            chain.proceed(newRequest)
//
//        }
//    }.build()
//
//
//    val api: ApiInterface by lazy {
//        Retrofit.Builder()
//            .baseUrl("https://bhagavad-gita3.p.rapidapi.com")
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiInterface::class.java)
//    }
//
//
//}