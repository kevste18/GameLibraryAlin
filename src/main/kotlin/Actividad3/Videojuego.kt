package Actividad3

import kotlinx.serialization.Serializable

@Serializable
data class Videojuego(
    var id: Int,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
) {
    @Override
    override fun toString() : String {
        return "ID: $id | Titulo: $titulo | Genero: $genero | Fecha Lanzamiento: $anio | Nota: $nota"}
}

