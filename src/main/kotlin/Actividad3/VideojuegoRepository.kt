package Actividad3

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import kotlin.collections.mutableListOf
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.pathString

class VideojuegoRepository {

    val rutaBin = Path.of("GameLibraryAlin", "datos", "datos_ini", "videojuegos.bin")

    fun leerDatosCSV(ruta: Path): List<Videojuego> {
        if (!Files.isReadable(ruta)) {
            println("Error: No se puede leer el fichero en la ruta: $ruta")
        } else {
            val lector = csvReader {
                delimiter = ';'
            }

            val filas: List<List<String>> = lector.readAll((ruta.toFile()))

            val games = filas.mapNotNull { columnas ->
                if (columnas.size >= 5) {
                    try {
                        val idGame: Int = columnas[0].toInt()
                        val title: String = columnas[1]
                        val genre: String = columnas[2]
                        val year: Int = columnas[3].toInt()
                        val nota: Double = columnas[4].toDouble()
                        Videojuego(idGame, title, genre, year, nota)
                    } catch (e: Exception) {
                        println("Fila inválida ignorada: $columnas -> Error: ${e.message}")
                        null
                    }
                } else {
                    println("Fila con formato incompleto ignorada: $columnas")
                    null
                }
            }
            println("--- Informacion leida con exito de: $ruta")
            return games
        }

        return emptyList();
    }

    fun escribirCSV(ruta: Path, games: List<Videojuego>) {
        try {
            val fichero: File = ruta.toFile()
            csvWriter {
                delimiter = ';'
            }.writeAll(
                games.map { game ->
                    listOf(
                        game.id.toString(),
                        game.titulo,
                        game.genero,
                        game.anio.toString(),
                        game.nota.toString()
                    )
                },
                fichero
            )
            println("--- Informacion guardada con exito en: $fichero")
        } catch (e: Exception) {
            println("Error al escribir el fichero CSV: ${e.message}")
        }
    }

    fun leerVideojuegosXML(ruta: Path): List<Videojuego> {
        var contenedor = VideogameXML(emptyList())

        if (!Files.isReadable(ruta)) {
            println("Error: No se puede leer el fichero en la ruta: $ruta")
        } else {
            val fichero = ruta.toFile()
            val xmlMapper = XmlMapper().registerKotlinModule()

            contenedor = xmlMapper.readValue(fichero)
        }

        return contenedor.listaVideojuegos
    }

    fun escribirDatosXML(ruta: Path, videojuegos: List<Videojuego>) {
        try {
            val fichero = ruta.toFile()
            val contenedor = VideogameXML(videojuegos)
            val xmlMapper = XmlMapper().registerKotlinModule()

            val xmlString = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contenedor)
            fichero.writeText(xmlString)

            println("--- Informacion guardada en XML: $fichero")
        } catch (e: Exception) {
            println("Error al guardar XML: ${e.message}")
        }
    }

    fun leerVideojuegosJSON(ruta: Path): List<Videojuego> {

        var videojuegos: List<Videojuego> = emptyList()

        if (!Files.isReadable(ruta)) {
            println("Error: No se puede leer el fichero en la ruta $ruta")
        } else {
            // Leemos el contenido completo del JSON como String
            val jsonString = Files.readString(ruta)

            // Convertimos de texto JSON a una lista de objetos Planta
            videojuegos = Json.decodeFromString<List<Videojuego>>(jsonString)
            println("Informacion leida con exito de: $ruta")
        }

        return videojuegos
    }


    fun escribirJSON(ruta: Path, videojuegos: List<Videojuego>) {

        try {
            val jsonConfigurador = Json { prettyPrint = true }
            val jsonString = jsonConfigurador.encodeToString(videojuegos)

            Files.writeString(ruta, jsonString)
            println("--- Informacion guardada en: $ruta")

        } catch (e: Exception) {
            println("Error al guardar JSON ${e.message}")
        }
    }

    fun convertir(
        origen: Path,
        destino: Path,
        leer: (Path) -> List<Videojuego>,
        escribir: (Path, List<Videojuego>) -> Unit
    ) {

        if (!origen.exists()) {
            println("El archivo de origen no exsiste!")
            return
        }

        destino.createParentDirectories()

        val readFile = leer(origen)
        escribir(destino, readFile)

        println("El archivo se ha generado en ${destino.pathString} con un numero de ${readFile.size} registros")
    }

    fun importarBinario(rutaCSV: Path) {

        val datos = leerDatosCSV(rutaCSV)

        try {
            FileChannel.open(
                rutaBin,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            ).use { canal ->
                for (i in datos) {
                    escribirRegistro(canal, i)
                }

                println("Registros Importados = ${datos.size}")

            }
        } catch (e: Exception) {
            println("Error al importar: ${e.message}")
        }
    }

    fun escribirRegistro(canal: FileChannel, juego: Videojuego) {

        val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

        // 1. Escribimos el ID (4 bytes)
        buffer.putInt(juego.id)

        // 2. Escribimos el titulo (60 bytes). Rellenamos con espacios si es más corto.
        val tituloBytes = juego.titulo
            .padEnd(TAMANO_TITULO, ' ')
            .toByteArray(Charsets.ISO_8859_1)
        buffer.put(tituloBytes, 0, TAMANO_TITULO)

        // 3. Escribimos el genero (20 bytes). Rellenamos con espacios si es más corto.
        val generoBytes = juego.genero
            .padEnd(TAMANO_GENERO, ' ')
            .toByteArray(Charsets.ISO_8859_1)
        buffer.put(generoBytes, 0, TAMANO_GENERO)

        // 4. Escribimos anio (4 bytes)
        buffer.putInt(juego.anio)

        // 5. Escribimos la nota (8 bytes)
        buffer.putDouble(juego.nota)

        // Preparamos el buffer para volcar la información al canal
        buffer.flip()
        while (buffer.hasRemaining()) {
            canal.write(buffer)
        }
        println("- Juego '${juego.titulo.trim()}' added correctamente.")
    }

    fun leerRegistro(): List<Videojuego> {

        val videojuegos = mutableListOf<Videojuego>()

        if (!Files.isReadable(rutaBin)) return emptyList()

        FileChannel.open(rutaBin, StandardOpenOption.READ).use { canal ->
            val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

            // Cada lectura llena exactamente un registro de 96 bytes
            while (canal.read(buffer) > 0) {
                buffer.flip()

                // 1. Leemos el ID
                val id = buffer.getInt()

                // 2. Leemos los bytes del nombre y los decodificamos limpiando los espacios sobrantes
                val tituloBytes = ByteArray(TAMANO_TITULO)
                buffer.get(tituloBytes)

                val titulo = String(tituloBytes, Charsets.ISO_8859_1).trim()

                // 3. Leemos los bytes del genero y los decodificamos limpiando los espacios sobrantes
                val generoBytes = ByteArray(TAMANO_GENERO)
                buffer.get(generoBytes)

                val genero = String(generoBytes, Charsets.ISO_8859_1).trim()

                // 4. Leemos el año
                val anio = buffer.getInt()

                // 5. Leemos la nota
                val nota = buffer.getDouble()

                videojuegos.add(Videojuego(id, titulo, genero, anio, nota))
                buffer.clear()
            }
        }

        return videojuegos
    }

    fun addRegsitro(videojuego: Videojuego) {

        try {
            FileChannel.open(
                rutaBin,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            ).use { canal ->
                escribirRegistro(canal, videojuego)
            }

        } catch (e: Exception) {
            println("Error al añadir el videojuego ${e.message}")
        }
    }

    fun modificarRegistro(idVideojuego: Int, nuevaNota: Double) {

        try {
            // Abrimos el canal con permisos de Lectura y Escritura
            FileChannel.open(rutaBin, StandardOpenOption.READ, StandardOpenOption.WRITE).use { canal ->
                val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)
                var encontrado = false

                while (canal.read(buffer) > 0 && !encontrado) {
                    // Al finalizar la lectura de un registro completo, guardamos el puntero actual
                    val posicionActual = canal.position()
                    buffer.flip()

                    val id = buffer.getInt()
                    if (id == idVideojuego) {
                        encontrado = true
                        // Calculamos la posición del campo nota en bytes dentro del fichero

                        // Calcular el inicio del registro actual
                        val inicioRegistro = posicionActual - TAMANO_REGISTRO
                        // Calcular los bytes que ocupan los campos anteriores (desplazamiento)
                        val desplazamientoNota = TAMANO_ID + TAMANO_TITULO + TAMANO_GENERO + TAMANO_ANIO
                        // "rebobinar" al inicio del registro actual y avanzar el desplazamiento
                        val posicionNota = inicioRegistro + desplazamientoNota

                        // Nos situamos en el canal exactamente sobre el campo nota
                        canal.position(posicionNota)

                        val bufferNota = ByteBuffer.allocate(TAMANO_NOTA)

                        bufferNota.putDouble(nuevaNota)

                        bufferNota.flip()

                        while (bufferNota.hasRemaining()) {
                            canal.write(bufferNota)
                        }
                    }
                    buffer.clear()
                }

                if (encontrado) {
                    println("\n--- Nota del videojuego con ID $idVideojuego modificada correctamente a ${nuevaNota}")
                } else {
                    println("No se encontro ningun videojuego con el ID: $idVideojuego")
                }
            }
        } catch (e: Exception) {
            println("Error al modificar el registro: ${e.message}")
        }
    }

    fun eliminarRegistro(idVideojuego: Int) {

        val rutaTmp = Path.of("GameLibraryAlin", "datos", "datos_ini", "videojuegos.tmp")

        var videojuegoEncontrado = false

        try {
            FileChannel.open(rutaBin, StandardOpenOption.READ).use { canalLectura ->
                FileChannel.open(
                    rutaTmp,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                ).use { canalEscritura ->
                    val buffer = ByteBuffer.allocate(TAMANO_REGISTRO)

                    // Cada lectura llena exactamente un registro de 96 bytes
                    while (canalLectura.read(buffer) > 0) {
                        buffer.flip()
                        val id = buffer.getInt()

                        if (id == idVideojuego) {
                            videojuegoEncontrado = true
                            // Si coincide con el ID a eliminar, lo ignoramos (no se escribe en el temporal)
                        } else {
                            // Rebobinamos el puntero del buffer para escribir el registro completo original
                            buffer.rewind()
                            canalEscritura.write(buffer)
                        }
                        buffer.clear()
                    }
                }
            }

            if (videojuegoEncontrado) {
                // Reemplazamos el fichero original por el limpio temporal
                Files.move(rutaTmp, rutaBin, StandardCopyOption.REPLACE_EXISTING)
                println("\n**** Videojuego con ID $idVideojuego eliminado con exito")
            } else {
                Files.deleteIfExists(rutaTmp)
                println("No se encontro el videojuego con ID: $idVideojuego")
            }
        } catch (e: Exception) {
            println("Error durante la eliminacion: ${e.message}")
        }
    }
}

