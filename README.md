# GameLibraryAlin

Catálogo de videojuegos por consola hecho en **Kotlin** para la asignatura de **Acceso a Datos** (2º DAM). El proyecto ha ido creciendo práctica a práctica: empezó leyendo un CSV y ahora trabaja con CSV, XML, JSON y un fichero binario de acceso aleatorio.

## Qué hace

Al arrancar carga el catálogo desde `videojuegos.csv` y muestra este menú:

```
===== CATÁLOGO DE VIDEOJUEGOS =====
1. Gestión CSV
2. Leer catálogo desde XML
3. Leer catálogo desde JSON
4. Conversiones entre formatos
5. Gestión fichero BIN
0. Salir
```

- **1. Gestión CSV**: listar, buscar por ID, añadir, modificar la nota y eliminar videojuegos. Cada cambio se guarda en el CSV.
- **2 y 3. Leer desde XML / JSON**: lee el fichero, muestra los juegos y los mete en el catálogo sin duplicar IDs. Al final dice cuántos juegos nuevos se han añadido y guarda el catálogo en el CSV.
- **4. Conversiones**: pasa los datos de un formato a otro (CSV, XML y JSON, en todas las combinaciones). Lee de `datos_ini/` y guarda el resultado en `datos_fin/` como `videojuegos.<extensión>`. Si el fichero de origen no existe, avisa en vez de petar.
- **5. Gestión fichero BIN**: CRUD sobre un fichero binario de acceso aleatorio (importar desde CSV, leer, añadir, modificar la nota y eliminar).

## Estructura del proyecto

Todo el código está en el paquete `Actividad3`:

| Fichero | Para qué sirve |
|---|---|
| `Main.kt` | Menús y opciones. Es lo que habla con el usuario. |
| `Videojuego.kt` | La data class que se usa en todo el proyecto (`id`, `titulo`, `genero`, `anio`, `nota`). Lleva `@Serializable` para el JSON y un `toString()` para mostrarla bonita. |
| `VideogameXML.kt` | Clase contenedora para el elemento raíz `<videojuegos>` del XML. |
| `VideojuegoRepository.kt` | Todo lo que lee o escribe ficheros: CSV, XML, JSON, binario y la función `convertir`. |
| `VideojuegoService.kt` | La lógica con listas (buscar, añadir, modificar, eliminar, integrar sin duplicados) y las funciones que piden datos por teclado validándolos. |
| Fichero de constantes | Los tamaños en bytes del registro binario. |
| `Practica2.kt` | Programa aparte de la práctica 2 que crea las carpetas `datos/datos_ini` y `datos/datos_fin`. |

He intentado separar responsabilidades: el **repository** solo toca ficheros y el **service** hace la lógica, así el `main` queda bastante limpio.

## Ficheros de datos

```
GameLibraryAlin/datos/
├── datos_ini/   → videojuegos.csv, .xml, .json y .bin (los de trabajo)
└── datos_fin/   → resultados de las conversiones
```

## El fichero binario

Cada videojuego ocupa siempre **96 bytes**, para poder saltar directamente a cualquier registro:

| Campo | Tipo | Bytes |
|---|---|---|
| id | Int | 4 |
| titulo | String | 60 (rellenado con espacios) |
| genero | String | 20 (rellenado con espacios) |
| anio | Int | 4 |
| nota | Double | 8 |

- Los textos se guardan en **ISO-8859-1** para que cada carácter ocupe 1 byte (con UTF-8 las tildes ocupan 2 y se descuadraría todo).
- **Modificar** salta con `FileChannel.position()` a los 8 bytes de la nota y solo reescribe eso.
- **Eliminar** copia todos los registros menos el borrado a un `.tmp` y luego sustituye el original. Así, si algo falla a mitad, el `.bin` no se rompe.

## Librerías

- **kotlin-csv** para leer y escribir el CSV.
- **Jackson XML** (con el módulo de Kotlin) para el XML.
- **kotlinx.serialization** para el JSON.
- **Java NIO** (`FileChannel`, `ByteBuffer`) para el binario.

Las dependencias están en `build.gradle.kts`.

## Cómo ejecutarlo

1. Abrir el proyecto en IntelliJ y dejar que Gradle descargue las dependencias.
2. Ejecutar el `main` de `Actividad3/Main.kt` desde la raíz del proyecto (las rutas son relativas).

