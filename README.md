# Agenda de Contactos · PM1EA0471

**Examen Primer Parcial · Programación Móvil I (PMO-0602) · Versión A**

| | |
|---|---|
| **Estudiante** | Angel Manuel Rivera |
| **Cuenta** | 202330010471 |
| **Catedrático** | Ing. Ricardo Enrique Lagos Mendoza |
| **Periodo** | III / 2026 |

Aplicación Android (Java) para registrar contactos con su fotografía, consultarlos, compartirlos y llamarlos. Está construida con el patrón **MVC + DAO** y guarda los datos en **SQLite**.

## Pantallas

1. **Registro:** país, nombre, teléfono, nota y foto (cámara o galería). Valida los datos y guarda el contacto.
2. **Lista:** muestra los contactos guardados, con buscador por nombre o teléfono. Cada contacto permite llamar, compartir, ver la imagen, actualizar y eliminar.
3. **Llamar:** muestra el nombre y el número con código de país (por ejemplo `+504 9876-5432`) y un botón para marcar.

## Arquitectura MVC + DAO

```
com.example.pm1ea0471
├── modelo/        Contacto (id, país, nombre, teléfono, nota, imagen)
├── dao/           BaseDatos (SQLiteOpenHelper), ContactoDao, PaisDao
├── controlador/   ContactoControlador, PaisControlador
├── vista/         MainActivity, ListaContactosActivity, LlamarActivity,
│                  EditarContactoActivity, AdaptadorContactos
└── util/          Alertas, Utilidades
```

- **Modelo:** la clase `Contacto` con los datos de cada contacto.
- **DAO:** es la única capa que ejecuta SQL. Las vistas y los controladores nunca tocan la base de datos directamente.
- **Controlador:** contiene las reglas de negocio: validaciones con expresiones regulares, guardar, buscar y dar formato al teléfono.
- **Vista:** las activities y el adaptador solo muestran datos y atienden los eventos del usuario.

## Cumplimiento de la rúbrica

| Requisito | Dónde está |
|---|---|
| Guardar contactos con la ruta de la foto en SQLite | `ContactoDao.insertar()`, `BaseDatos` |
| Clase modelo `Contacto` | `modelo/Contacto.java` |
| Alertas si un campo obligatorio está vacío (nombre, teléfono, nota) | `ContactoControlador.validar()` y `Alertas` |
| Regex: nombre solo letras y teléfono de 8 dígitos | `ContactoControlador` (`PATRON_NOMBRE`, `PATRON_TELEFONO`) |
| Listar contactos desde la base de datos | `ListaContactosActivity` → `ContactoDao.listar()` |
| Eliminar contacto | `ListaContactosActivity.alEliminar()` (con confirmación) |
| Actualizar contacto | `EditarContactoActivity` → `ContactoDao.actualizar()` |
| Compartir con `Intent.ACTION_SEND` | `AdaptadorContactos.compartir()` |
| Ver la imagen guardada | `AdaptadorContactos.verFoto()` → `Alertas.verFoto()` |
| `ACTION_CALL` con permiso `CALL_PHONE` en tiempo de ejecución | `LlamarActivity` |
| Confirmar la llamada con `AlertDialog` («¿Desea llamar a …?») | `ListaContactosActivity.alLlamar()` → `Alertas.confirmar()` |
| Punto extra: agregar países desde la app | Botón «+» junto al país → `PaisControlador.agregar()` |

## Detalles de la implementación

- **Validaciones:** el nombre solo acepta letras y espacios, el teléfono exactamente 8 dígitos y la nota es obligatoria. El campo con error se marca en rojo y se muestra una alerta.
- **Fotografías:** se toman con la cámara (`FileProvider`) o se eligen de la galería. Las de galería se copian a la carpeta privada de la app para poder leerlas siempre. Si una foto ya no está disponible, se muestra una silueta en lugar de cerrar la app.
- **Países:** vienen precargados (Honduras, El Salvador, Guatemala, Costa Rica, Nicaragua, Panamá, Estados Unidos y México). El código de llamada se toma del texto, por ejemplo `Honduras (+504)`.
- **Base de datos:** tablas `contactos` y `paises`. La versión 3 renombra las tablas de la versión anterior sin perder datos.
- **Diseño:** Material Design 3 con modo claro y modo oscuro. Todas las alertas están centralizadas en la clase `Alertas`.

## Permisos

`CALL_PHONE` (llamar), `CAMERA` (tomar fotos), `READ_MEDIA_IMAGES` y `READ_EXTERNAL_STORAGE` (galería en versiones antiguas). Todos se solicitan en tiempo de ejecución cuando hacen falta.

## Tecnologías

Java 11 · Android Studio · SQLite · Material Components 1.10 · RecyclerView · `minSdk 24` · `targetSdk 37`

## Cómo ejecutarlo

1. Clonar el repositorio y abrirlo en Android Studio.
2. Esperar a que termine la sincronización de Gradle.
3. Ejecutar en un emulador o en un dispositivo con Android 7.0 (API 24) o superior.
