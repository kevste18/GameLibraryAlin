package Actividad3

class VideojuegoService {

    fun listarVideojuegos(games: List<Videojuego>) {

        for (i in games) {
            println(i)
        }
    }

    fun buscarPorId(games: List<Videojuego>, id: Int?) {

        for (i in games) {
            if (i.id == id) {
                println(i)
                return
            }
        }

        println("Videojuego no encontado!")
    }


    fun addVideojuego(games: List<Videojuego>): List<Videojuego> {

        val id: Int = (games.maxOfOrNull { it.id } ?: 0) + 1

        println("> Introduce el titulo del juego")
        val title: String? = readlnOrNull()
        println("> Introduce el genero del juego")
        val genre: String? = readlnOrNull()
        println("> Introduce el año de lanzamiento")
        val year: Int? = readlnOrNull()?.toIntOrNull()
        println("> Califica el juego")
        val nota: Double? = readlnOrNull()?.toDoubleOrNull()

        if (title.isNullOrBlank() || genre.isNullOrBlank() || year == null || nota == null) {
            println("Datos inválidos, no se ha añadido el juego")
            return games
        }

        if (nota < 0.0 || nota > 10.0) {
            println("Nota fuera de rango [0.0, 10.0]")
            return games
        }

        val nuevo = Videojuego(id, title, genre, year, nota)

        val copia = games.toMutableList()
        copia.add(nuevo)
        return copia
    }

    fun modificarNota(games: List<Videojuego>, id: Int?, nota: Double?): List<Videojuego> {

        val copia = games.toMutableList();

        var videogameCopia: Videojuego;

        if (nota == null) {
            println("Nota Invalida!")
            return copia
        }

        for (i in games.indices) {
            if (id == games[i].id) {
                videogameCopia = Videojuego(games[i].id, games[i].titulo, games[i].genero, games[i].anio, nota)
                copia[i] = videogameCopia
                return copia;
            }
        }

        println("No se encontro ningun juego con el ID $id")

        return copia;
    }

    fun eliminarVideojuego(games: List<Videojuego>, id: Int?): List<Videojuego> {

        val copia = games.toMutableList()

        var posicion: Int = -1;

        for (i in games.indices) {
            if (id == games[i].id) {
                posicion = i
            }
        }

        if (posicion > -1) {
            println("Estas seguro de que quieres eliminar el juego? (S/N)")
            var respuesta: String? = readlnOrNull()

            if (respuesta == "s" || respuesta == "S") {
                copia.removeAt(posicion)
                println("Juego eliminado con exito!")
            } else {
                println("Volviendo al menu...")
            }
        } else {
            println("Id no encontrado!")
        }

        return copia;
    }

    fun integrarCatalogo(games: List<Videojuego>, addedGames: List<Videojuego>): List<Videojuego> {

        val copia = games.toMutableList()
        var contador: Int = 0;
        var contadorExsistente: Int = 0
        var exsiste: Boolean;

        for (i in addedGames) {
            exsiste = false;

            for (x in games) {
                if (i.id == x.id) {
                    exsiste = true;
                    contadorExsistente++
                    break
                }
            }

            if (exsiste == false) {
                copia.add(i)
                contador++
            }
        }

        println("Juegos added = $contador")
        println("Juegos con el mismo ID = $contadorExsistente")

        return copia;
    }

    fun pedirIdNuevo(videojuegos: List<Videojuego>): Int {

        var opcion: Int?

        do {
            println("> Introduce el ID del videojuego")
            opcion = readLine()?.toIntOrNull()

            val encontrado = videojuegos.any { it.id == opcion }

            if (encontrado) {
                println("EL ID YA EXSISTE")
            }

        } while (encontrado || opcion == null)

        return opcion
    }

    fun pedirNota(mensaje : String): Double {

        var numero: Double?

        do {
            println(mensaje)
            numero = readLine()?.toDoubleOrNull()

            if(numero == null || numero !in 0.0 .. 10.0) {
                println("Datos Invalidos")
            }

        } while (numero == null || numero !in 0.0 .. 10.0)

        return numero
    }

    fun pedirEntero(mensaje : String) : Int {

        var numero: Int?

        do {
            println(mensaje)
            numero = readLine()?.toIntOrNull()

        } while (numero == null)

        return numero
    }

}