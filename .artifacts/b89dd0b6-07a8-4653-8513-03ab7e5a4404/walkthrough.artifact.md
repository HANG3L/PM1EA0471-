# Walkthrough - Agenda de Contactos (Examen PM01 - Versión A)

Desarrollo completo de la aplicación móvil **Agenda de Contactos** utilizando Android Studio, Java y base de datos SQLite, cumpliendo con todos los requisitos de la rúbrica del examen.

## Cambios Realizados

### 1. Modelo de Datos
- [Contact.java](file:///C:/Users/ambar/AndroidStudioProjects/PM1EA0471/app/src/main/java/com/example/pm1ea0471/Contact.java): Clase modelo con los campos requeridos (`id`, `pais`, `nombre`, `telefono`, `nota`, `imagen`).

### 2. Base de Datos & DAO (con Registro de Países)
- [DatabaseHelper.java](file:///C:/Users/ambar/AndroidStudioProjects/PM1EA0471/app/src/main/java/com/example/pm1ea0471/DatabaseHelper.java): Gestor SQLite que implementa las tablas `contacts` y `countries` (con pre-carga de países predeterminados y métodos `addCountry` / `getAllCountries`).

### 3. Pantalla de Registro (MainActivity)
- [MainActivity.java](file:///C:/Users/ambar/AndroidStudioProjects/PM1EA0471/app/src/main/java/com/example/pm1ea0471/MainActivity.java) y [activity_main.xml](file:///C:/Users/ambar/AndroidStudioProjects/PM1EA0471/app/src/main/res/layout/activity_main.xml):
  - Formulario con selector de país dinámico, botón **"+ País"** para registrar nuevos países directamente desde la app (punto extra), y opciones de **Tomar Foto** o **Galería**.
  - **Validaciones**: Alertas con `AlertDialog` si hay campos vacíos, validación con expresiones regulares para el nombre (solo letras y espacios) y el teléfono (exactamente 8 dígitos numéricos).
  - Guardado exitoso en SQLite.

### 4. Pantalla de Lista & Operaciones (ContactListActivity & ContactAdapter)
- [ContactListActivity.java](file:///C:/Users/ambar/AndroidStudioProjects/PM1EA0471/app/src/main/java/com/example/pm1ea0471/ContactListActivity.java), [ContactAdapter.java](file:///C:/Users/ambar/AndroidStudioProjects/PM1EA0471/app/src/main/java/com/example/pm1ea0471/ContactAdapter.java), y [item_contact.xml](file:///C:/Users/ambar/AndroidStudioProjects/PM1EA0471/app/src/main/res/layout/item_contact.xml):
  - Listado de contactos guardados en SQLite usando `RecyclerView`.
  - **Eliminar**: Elimina el contacto seleccionado de la base de datos con confirmación.
  - **Actualizar**: Permite editar los datos e imagen del contacto mediante [ContactEditActivity.java](file:///C:/Users/ambar/AndroidStudioProjects/PM1EA0471/app/src/main/java/com/example/pm1ea0471/ContactEditActivity.java) (con selector de país dinámico, registro de nuevos países, cámara y galería).
  - **Compartir**: Comparte la información del contacto con otras aplicaciones utilizando `Intent.ACTION_SEND`.
  - **Ver Imagen**: Muestra la fotografía guardada del contacto en tamaño grande mediante un `AlertDialog`.

### 5. Pantalla de Llamada
- **Llamar**: Al hacer clic en el botón de llamar, se solicita confirmación con un `AlertDialog.Builder` («¿Desea llamar a ...?»), se solicita el permiso `CALL_PHONE` en tiempo de ejecución y se ejecuta la llamada mediante `Intent.ACTION_CALL`.

---

## Verificación de Resultados

- **Compilación**: El proyecto compila correctamente sin errores (`BUILD SUCCESSFUL`).
- **Nomenclatura**: Proyecto configurado con el nombre `PM1EA0471`.
