import java.nio.file.Path
import kotlin.io.path.createDirectories
import java.nio.file.Files
import kotlin.io.path.exists

fun main() {

    val datos = Path.of("GameLibraryAlin","datos");
    val datos_ini = Path.of("GameLibraryAlin","datos", "datos_ini")
    val datos_fin = Path.of("GameLibraryAlin","datos", "datos_fin")

    println("CREACION DE RUTAS PROYECTO")

    if (!datos.exists()) {
        datos.createDirectories()
    }

    if (!datos_ini.exists()) {
        datos_ini.createDirectories()
        println("Creacion de ruta para DATOS_INI")
    }

    if (!datos_fin.exists()) {
        datos_fin.createDirectories()
        println("Creacion de ruta para DATOS_FIN")
    }

    println("MOSTRANDO ESTRUCTURA DE DIRECTORIOS Y FICHEROS")
    try {
        Files.walk(datos).use { stream ->
            // Ordenar el stream para una visualización ordenada por categorías
            stream.sorted().forEach { path ->
                // Calcular profundidad para la indentación
                // Restamos el número de componentes de la ruta base para que la raíz no tenga sangrado
                val profundidad = path.nameCount - datos.nameCount
                val indentacion = "\t".repeat(profundidad)

                // Determinamos si es un directorio o un fichero
                val prefijo = if (Files.isDirectory(path)) "[DIR]" else "[FILE]"

                // No imprimimos la propia carpeta raíz, solo su contenido clasificado
                if (profundidad > 0) {
                    println("$indentacion$prefijo ${path.fileName}")
                }
            }
        }
    } catch (e: Exception) {
        println("\n--- Ocurrio un error durante la visualizacion de la estructura del proyecto ---")
        e.printStackTrace()
    }


}
