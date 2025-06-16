package com.is1.proyecto; // Asegúrate de que el paquete coincide con tu groupId y estructura de carpetas

import com.fasterxml.jackson.databind.ObjectMapper; // Necesario para convertir objetos Java a JSON
import java.util.stream.Collectors;

import static spark.Spark.*; // Importa los métodos estáticos de Spark (get, post, before, after, etc.)

// Importaciones de ActiveJDBC
import org.javalite.activejdbc.Base; // Clase central de ActiveJDBC para gestión de DB

import com.is1.proyecto.models.Task;
import com.is1.proyecto.models.Team;
import com.is1.proyecto.models.User; // Tu modelo User para interactuar con la tabla 'users'

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map; // Para usar Map.of() en las respuestas JSON


public class App {

    // ObjectMapper es una clase de Jackson para serializar/deserializar JSON.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        port(8080); // La aplicación Spark escuchará en el puerto 8080

        // --- Configuración de filtros para abrir y cerrar conexiones de ActiveJDBC ---
        // Este filtro se ejecuta ANTES de que cualquier ruta sea procesada.
        // Aquí abrimos la conexión a la base de datos para la solicitud actual.
        before((req, res) -> {
            try {
                // Base.open() gestiona la conexión. Se conecta a la BD 'students_db' con el usuario 'muva'.
                // ¡ADVERTENCIA! Credenciales en código fuente NO SEGURAS para producción.
                Base.open("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/students_db", "muva", "muva");
            } catch (Exception e) {
                System.err.println("Error al abrir conexión con ActiveJDBC: " + e.getMessage());
                // Si la conexión falla, detiene la solicitud y envía un error 500 al cliente.
                halt(500, "{\"error\": \"Error interno del servidor: Fallo al conectar a la base de datos.\"}" + e.getMessage());
            }
        });

        // Este filtro se ejecuta DESPUÉS de que cualquier ruta ha sido procesada (o si hubo un error).
        // Aquí cerramos la conexión de la base de datos, devolviéndola al pool de ActiveJDBC.
        after((req, res) -> {
            try {
                Base.close(); // Cierra la conexión de ActiveJDBC para esta solicitud.
            } catch (Exception e) {
                System.err.println("Error al cerrar conexión con ActiveJDBC: " + e.getMessage());
                // Este error es de limpieza, no debería detener la respuesta principal.
            }
        });
        // -------------------------------------------------------------------------

        // --- Endpoint de prueba simple (ruta raíz) ---
        get("/", (req, res) -> {
            res.type("application/json"); // La respuesta será en formato JSON.
            return objectMapper.writeValueAsString(Map.of("message", "¡Bienvenido a la API simplificada! Usa /users para crear usuarios."));
        });


get("/users/2form", (req, res) -> {
    return new ModelAndView(null, "user_form.mustache");
}, new MustacheTemplateEngine());




get("/users/altaform", (req, res) -> {
    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Alta usuario");
    return new ModelAndView(model, "user_form_con_titulo.mustache");
}, new MustacheTemplateEngine());




get("/altausuario", (req, res) -> {
    List<Team> teams = Team.findAll(); // Trae todos los equipos
    List<Map<String, Object>> teamData = teams.stream().map(team -> Map.of(
            "id", team.getId(),
            "name", team.getString("name")
    )).collect(Collectors.toList());

    Map<String, Object> model = new HashMap<>();
    model.put("teams", teamData); // Enviamos los equipos a la plantilla

    return new ModelAndView(model, "user_form_team.mustache");
}, new MustacheTemplateEngine());



post("/postusers", (req, res) -> {
    res.type("application/json");

    String name = req.queryParams("name");
    String password = req.queryParams("password");
    String teamIdStr = req.queryParams("team_id");

    // Validaciones básicas
    if (name == null || name.isEmpty() || password == null || password.isEmpty() || teamIdStr == null || teamIdStr.isEmpty()) {
        res.status(400);
        return objectMapper.writeValueAsString(Map.of(
            "error", "Nombre, contraseña y equipo son requeridos."
        ));
    }

    try {
        int teamId = Integer.parseInt(teamIdStr); // Convertimos el team_id a entero

        User newUser = new User();
        newUser.set("name", name);
        newUser.set("password", password); // En producción, siempre encriptar
        newUser.set("team_id", teamId);
        newUser.saveIt();

        res.status(201);
        return objectMapper.writeValueAsString(Map.of(
            "message", "Usuario '" + name + "' registrado con éxito.",
            "id", newUser.getId(),
            "team_id", teamId
        ));
    } catch (Exception e) {
        System.err.println("Error al registrar usuario: " + e.getMessage());
        res.status(500);
        return objectMapper.writeValueAsString(Map.of(
            "error", "Error interno al registrar usuario."
        ));
    }
});
//--------------------------------------------------


        // --- NUEVO ENDPOINT: Vincular una Tarea a un Equipo ---
        // Este endpoint espera 'team_id' y 'task_id' en el cuerpo de la solicitud (form-urlencoded o JSON).
        // Ejemplo de uso con curl (form-urlencoded):
        // curl -X POST -d "team_id=1&task_id=1" http://localhost:8080/teams/tasks/link
        post("/teams/tasks/link", (req, res) -> {
            res.type("application/json");

            // Obtener los IDs del equipo y la tarea de los parámetros de la solicitud
            String teamIdStr = req.queryParams("team_id");
            String taskIdStr = req.queryParams("task_id");

            // Validaciones básicas
            if (teamIdStr == null || teamIdStr.isEmpty() || taskIdStr == null || taskIdStr.isEmpty()) {
                res.status(400);
                return objectMapper.writeValueAsString(Map.of("error", "Los IDs del equipo y la tarea son requeridos."));
            }

            try {
                // Convertir los IDs a enteros
                long teamId = Long.parseLong(teamIdStr);
                long taskId = Long.parseLong(taskIdStr);

                // Buscar el equipo y la tarea en la base de datos
                Team team = Team.findById(teamId);
                Task task = Task.findById(taskId);

                // Verificar si el equipo y la tarea existen
                if (team == null) {
                    res.status(404);
                    return objectMapper.writeValueAsString(Map.of("error", "Equipo con ID " + teamId + " no encontrado."));
                }
                if (task == null) {
                    res.status(404);
                    return objectMapper.writeValueAsString(Map.of("error", "Tarea con ID " + taskId + " no encontrada."));
                }

                // Vincular la tarea al equipo usando el método 'add' de ActiveJDBC para relaciones Many2Many
                // ActiveJDBC se encargará de insertar la entrada en la tabla 'teams_tasks'
                team.add(task);
                // No es estrictamente necesario llamar a team.save() después de add() para relaciones Many2Many,
                // ya que add() ya persiste la asociación, pero no hace daño y es buena práctica si se modificaron otros atributos del team.
                // team.save();

                res.status(200); // OK
                return objectMapper.writeValueAsString(Map.of("message", "Tarea con ID " + taskId + " vinculada exitosamente al Equipo con ID " + teamId + "."));

            } catch (NumberFormatException e) {
                res.status(400);
                return objectMapper.writeValueAsString(Map.of("error", "Los IDs de equipo y tarea deben ser números válidos."));
            } catch (Exception e) {
                System.err.println("Error al vincular tarea a equipo: " + e.getMessage());
                e.printStackTrace();
                res.status(500);
                return objectMapper.writeValueAsString(Map.of("error", "Error interno al vincular tarea a equipo: " + e.getMessage()));
            }
        });



 // --- NUEVO ENDPOINT: Mostrar el formulario para vincular Tarea a Equipo (GET) ---
        get("/teams/tasks/linkform", (req, res) -> {
            // Cargar todos los equipos y tareas de la base de datos
            List<Team> teams = Team.findAll();
            List<Task> tasks = Task.findAll();

            // Preparar los datos para la plantilla Mustache
            Map<String, Object> model = new HashMap<>();
            // Mapear la lista de Teams a un formato que Mustache pueda procesar fácilmente (id y name)
            model.put("teams", teams.stream()
                                  .map(team -> Map.of("id", team.getId(), "name", team.getString("name")))
                                  .collect(Collectors.toList()));
            // Mapear la lista de Tasks a un formato que Mustache pueda procesar fácilmente (id y name)
            model.put("tasks", tasks.stream()
                                  .map(task -> Map.of("id", task.getId(), "name", task.getString("name")))
                                  .collect(Collectors.toList()));

            // Renderizar la plantilla Mustache con los datos
            return new ModelAndView(model, "link_team_task.mustache");
        }, new MustacheTemplateEngine());















// --- NUEVO ENDPOINT: Formulario para crear un User ---
        get("/users/form", (req, res) -> {
            res.type("text/html"); // La respuesta será HTML

            // Construye la cadena HTML para el formulario de registro de usuario
            String htmlForm = "<!DOCTYPE html>\n" +
                              "<html lang=\"es\">\n" +
                              "<head>\n" +
                              "    <meta charset=\"UTF-8\">\n" +
                              "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                              "    <title>Registrar Nuevo Usuario</title>\n" +
                              "    <script src=\"https://cdn.tailwindcss.com\"></script>\n" +
                              "    <style>\n" +
                              "        body {\n" +
                              "            font-family: 'Inter', sans-serif;\n" +
                              "        }\n" +
                              "    </style>\n" +
                              "</head>\n" +
                              "<body class=\"bg-gray-100 flex items-center justify-center min-h-screen p-4\">\n" +
                              "    <div class=\"bg-white p-8 rounded-lg shadow-lg w-full max-w-md\">\n" +
                              "        <h1 class=\"text-3xl font-bold text-center text-gray-800 mb-6\">Registrar Nuevo Usuario</h1>\n" +
                              "        <form action=\"/users\" method=\"post\" class=\"space-y-4\">\n" +
                              "            <div>\n" +
                              "                <label for=\"name\" class=\"block text-gray-700 text-sm font-medium mb-1\">Nombre de Usuario:</label>\n" +
                              "                <input type=\"text\" id=\"name\" name=\"name\" required\n" +
                              "                       class=\"w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500\">\n" +
                              "            </div>\n" +
                              "            <div>\n" +
                              "                <label for=\"password\" class=\"block text-gray-700 text-sm font-medium mb-1\">Contraseña:</label>\n" +
                              "                <input type=\"password\" id=\"password\" name=\"password\" required\n" +
                              "                       class=\"w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500\">\n" +
                              "            </div>\n" +
                              "            <button type=\"submit\"\n" +
                              "                    class=\"w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2\">\n" +
                              "                Registrar Usuario\n" +
                              "            </button>\n" +
                              "            <div class=\"text-center mt-4\">\n" +
                              "                <a href=\"/\" class=\"text-blue-600 hover:underline text-sm\">Volver al inicio</a>\n" +
                              "            </div>\n" +
                              "        </form>\n" +
                              "    </div>\n" +
                              "</body>\n" +
                              "</html>";
            return htmlForm;
        });










        // --- Endpoint para dar de alta un nuevo User (método POST) ---
        // Espera parámetros 'name' y 'password' en el cuerpo de la solicitud (form-urlencoded).
        // Ejemplo de uso con curl:
        // curl -X POST -d "name=Alice&password=pass123" http://localhost:8080/users
        post("/users", (req, res) -> {
            res.type("application/json"); // La respuesta será en formato JSON.

            // Obtiene los parámetros 'name' y 'password' de la solicitud.
            String name = req.queryParams("name");
            String password = req.queryParams("password");

            // --- Validaciones básicas ---
            if (name == null || name.isEmpty() || password == null || password.isEmpty()) {
                res.status(400); // Código de estado HTTP 400 (Bad Request).
                return objectMapper.writeValueAsString(Map.of("error", "Nombre y contraseña son requeridos."));
            }

            try {
                // --- Creación y guardado del usuario usando el modelo ActiveJDBC ---
                User newUser = new User(); // Crea una nueva instancia de tu modelo User.
                newUser.set("name", name); // Asigna el nombre al campo 'name'.
                newUser.set("password", password); // Asigna la contraseña al campo 'password'.
                // ¡ADVERTENCIA DE SEGURIDAD CRÍTICA!
                // En una aplicación real, las contraseñas DEBEN ser hasheadas (ej. con BCrypt)
                // ANTES de guardarse en la base de datos, NUNCA en texto plano.

                newUser.saveIt(); // Guarda el nuevo usuario en la tabla 'users'.

                res.status(201); // Código de estado HTTP 201 (Created) para una creación exitosa.
                // Devuelve una respuesta JSON con el mensaje y el ID del nuevo usuario.
                return objectMapper.writeValueAsString(Map.of("message", "Usuario '" + name + "' registrado con éxito.", "id", newUser.getId()));

            } catch (Exception e) {
                // Si ocurre cualquier error durante la operación de DB, se captura aquí.
                System.err.println("Error al registrar usuario: " + e.getMessage());
                e.printStackTrace(); // Imprime el stack trace para depuración.
                res.status(500); // Código de estado HTTP 500 (Internal Server Error).
                return objectMapper.writeValueAsString(Map.of("error", "Error interno al registrar usuario: " + e.getMessage()));
            }
        });

        // Este mensaje se mostrará en la consola cuando la aplicación se inicie.
        System.out.println("Servidor Spark simplificado iniciado en http://localhost:8080");
        System.out.println("Prueba POST a /users con 'name' y 'password' en el cuerpo.");
    }
}
