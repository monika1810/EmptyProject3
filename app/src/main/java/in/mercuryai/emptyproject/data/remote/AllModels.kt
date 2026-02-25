package `in`.mercuryai.emptyproject.data.remote

data class ModelListResponse(
    val models: List<Model>
)

data class Model(
    val name: String,
    val supportedGenerationMethods: List<String>?
)

