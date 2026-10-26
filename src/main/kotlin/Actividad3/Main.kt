package Actividad3

import java.nio.file.Path

fun main() {

    val rutaCSV: Path = Path.of("GameLibraryAlin", "datos", "datos_ini", "videojuegos.csv")
    val rutaXML: Path = Path.of("GameLibraryAlin", "datos", "datos_ini", "videojuegos.xml")
    val rutaJSON: Path = Path.of("GameLibraryAlin", "datos", "datos_ini", "videojuegos.json")
    val destinoXML: Path = Path.of("GameLibraryAlin", "datos", "datos_fin", "videojuegos.xml")
    val destinoJSON: Path = Path.of("GameLibraryAlin", "datos", "datos_fin", "videojuegos.json")
    val destinoCSV: Path = Path.of("GameLibraryAlin", "datos", "datos_fin", "videojuegos.csv")


    val repo = VideojuegoRepository()
    val service = VideojuegoService()

    var games: List<Videojuego> = repo.leerDatosCSV(rutaCSV)

    fun submenu() {

        var opcion: Int?

        do {
            println("--- Gestion CSV ---")
            println("0. Volver al menu principal")
            println("1. Listar todos los videojuegos")
            println("2. Buscar videojuego por ID")
            println("3. Add nuevo videojuego")
            println("4. Modificar nota de un videojuego")
            println("5. Eliminar videojuego")
            println("> Elige una opcion:")
            println()

            opcion = readLine()?.toIntOrNull()


            var id: Int?
            var nota: Double?

            when (opcion) {
                1 -> service.listarVideojuegos(games)

                2 -> {
                    println("Introduce el id:")
                    id = readLine()?.toIntOrNull()
                    service.buscarPorId(games, id)
                }

                3 -> {
                    games = service.addVideojuego(games)
                    repo.escribirCSV(rutaCSV, games)
                }

                4 -> {
                    println("Introduce el id:")
                    id = readLine()?.toIntOrNull()
                    println("Introduce la nota actualizada")
                    nota = readLine()?.toDoubleOrNull()

                    if (nota != null) {
                        if (nota < 0.0 || nota > 10.0) {
                            println("Nota fuera del rango valido")
                        } else {
                            games = service.modificarNota(games, id, nota)
                            repo.escribirCSV(rutaCSV, games)
                        }
                    } else {
                        println("La nota no es un numero")
                    }
                }

                5 -> {
                    println("Introduce el ID:")
                    id = readLine()?.toIntOrNull()
                    games = service.eliminarVideojuego(games, id)
                    repo.escribirCSV(rutaCSV, games)
                }

                0 -> println("Volviendo...")

                else -> println("Opcion Invalida")
            }

        } while (opcion != 0)
    }

    fun submenuConversiones() {
1
        var opcion : Int?

        do {
            println("--- CONVERSIONES ---")
            println("0. Volver")
            println("1. CSV -> JSON")
            println("2. CSV -> XML")
            println("3. JSON -> CSV")
            println("4. JSON -> XML")
            println("5. XML -> CSV")
            println("6. XML -> JSON")

            println("> Elige una opcion")
            opcion = readLine()?.toIntOrNull()

            when (opcion) {
                0 -> println("Volviendo...")
                1 -> repo.convertir(rutaCSV, destinoJSON , repo::leerDatosCSV, repo::escribirJSON)
                2 -> repo.convertir(rutaCSV, destinoXML, repo::leerDatosCSV, repo::escribirDatosXML)
                3 -> repo.convertir(rutaJSON, destinoCSV, repo::leerVideojuegosJSON, repo::escribirCSV)
                4 -> repo.convertir(rutaJSON, destinoXML, repo::leerVideojuegosJSON, repo::escribirDatosXML)
                5 -> repo.convertir(rutaXML, destinoCSV, repo::leerVideojuegosXML, repo::escribirCSV)
                6 -> repo.convertir(rutaXML, destinoJSON, repo::leerVideojuegosXML, repo::escribirJSON)
                else -> println("Opcion Invalida!")
            }

        } while (opcion != 0)

    }

    fun submenuBIN() {

        var opcion : Int?

        do {
            println("=== CRUD FICHERO BIN ===")
            println("0. Volver")
            println("1. Importar informacion desde CSV")
            println("2. Leer informacion desde CSV")
            println("3. Add un registro nuevo")
            println("4. Modificar la nota de un registro exsistente (por ID)")
            println("5. Eliminar un registro exsistente (por ID)")
            println("> Elige una opcion:")
            println()

            opcion = readLine()?.toIntOrNull()

            when (opcion) {
                0 -> println("Volviendo...")
                1 -> repo.importarBinario(rutaCSV)
                2 -> service.listarVideojuegos(repo.leerRegistro())
                3 -> {
                    val juegos = repo.leerRegistro();
                    val id = service.pedirIdNuevo(juegos)

                    println(" > Introduce el titulo del videojuego")
                    val titulo : String = readLine().orEmpty()

                    println(" > Introduce el genero del videojuego")
                    val genero : String = readLine().orEmpty()

                    val anio = service.pedirEntero("> Introduce el año de lanzamiento")

                    val nota = service.pedirNota("> Introduce la nota")

                    val nuevo = Videojuego(id, titulo, genero, anio, nota)

                    repo.addRegsitro(nuevo)
                }

                4 -> {
                    val id = service.pedirEntero("> Introduce el ID del videojuego")
                    val registro = repo.leerRegistro()
                    val juego = registro.find { it.id == id}

                    if (juego == null) {
                        println("Videojuego no encontrado!")
                    } else {
                        println("> Titulo ${juego.titulo}")
                        val nota: Double = service.pedirNota("> Modifica la nota")

                        repo.modificarRegistro(id, nota)
                    }
                }

                5 -> {
                    val id = service.pedirEntero("> Introduce el ID del videojuego")
                    val registro = repo.leerRegistro()
                    val juego = registro.find {it.id == id}

                    if (juego == null) {
                        println("Videojuego no encontrado!")
                    } else {
                        println("Estas seguro de que quieres eliminar ${juego.titulo} ? \n > S/N")
                        var respuesta : String = readLine().orEmpty()

                        if (respuesta.isEmpty()) {
                            respuesta = "n"
                        }

                        if (respuesta.lowercase() == "s") {
                            repo.eliminarRegistro(id)
                        } else if (respuesta.lowercase() == "n") {
                            println("El videojuego NO se ha eliminado")
                        } else {
                            println("Opcion Invalida, el videojuego NO se ha eliminado")
                        }
                    }
                }
                else -> println("Opcion Invalida!")
            }
        } while (opcion != 0)
    }

    var opcion: Int?

    do {
        println("=== CATALOGO DE VIDEOJUEGOS ===")
        println("1. Gestion CSV")
        println("2. Leer catalogo desde XML")
        println("3. Leer catalogo desde JSON")
        println("4. Conversiones entre formatos")
        println("5. Gestion fichero BIN")
        println("0. Salir")
        println("===============================")
        println("> Elige una opcion:")
        println()

        opcion = readLine()?.toIntOrNull()


        when (opcion) {
            0 -> println("Hasta la proxima!")
            1 -> submenu()
            2 -> {
                val gamesXML: List<Videojuego> = repo.leerVideojuegosXML(rutaXML)
                games = service.integrarCatalogo(games, gamesXML)

                service.listarVideojuegos(gamesXML)
                repo.escribirCSV(rutaCSV, games)
            }

            3 -> {
                val gamesJSON: List<Videojuego> = repo.leerVideojuegosJSON(rutaJSON)
                service.listarVideojuegos(gamesJSON)
                games = service.integrarCatalogo(games, gamesJSON)
                repo.escribirCSV(rutaCSV, games)
            }

            4-> submenuConversiones()
            5 -> submenuBIN()

            else -> println("Error")
        }

    } while (opcion != 0)
}
