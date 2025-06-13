package com.is1.proyecto; // Asegúrate de que el paquete coincide con tu groupId y estructura de carpetas

import com.fasterxml.jackson.databind.ObjectMapper; // Necesario para convertir objetos Java a JSON y viceversa

import java.util.List;
import java.util.Map; // Para Map.of() y HashMap
import java.util.HashMap; // Para la instancia de HashMap

import static spark.Spark.*; // Importa los métodos estáticos de Spark

public class App {

    // ObjectMapper es una clase de Jackson que convierte objetos Java a JSON y viceversa.
    // La declaramos como estática y final para que solo se cree una vez.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        port(8080); // La aplicación Spark escuchará en el puerto 8080

        get("/", (req, res) -> {
            res.type("application/json"); // Indica que la respuesta será JSON
            return objectMapper.writeValueAsString(Map.of("message", "¡Bienvenido a la API StudTracking!"));
        });


get("/student/form", (req, res) -> {
    res.type("text/html"); // Indica que la respuesta será HTML

    // Construye la cadena HTML con el formulario
    String htmlForm = "<!DOCTYPE html>\n" +
                      "<html lang=\"es\">\n" +
                      "<head>\n" +
                      "    <meta charset=\"UTF-8\">\n" +
                      "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                      "    <title>Registrar Nuevo Estudiante</title>\n" +
                      "    \n" +
                      "    <script src=\"https://cdn.tailwindcss.com\"></script>\n" +
                      "    <style>\n" +
                      "        body {\n" +
                      "            font-family: 'Inter', sans-serif;\n" +
                      "        }\n" +
                      "    </style>\n" +
                      "</head>\n" +
                      "<body class=\"bg-gray-100 flex items-center justify-center min-h-screen\">\n" +
                      "    <div class=\"bg-white p-8 rounded-lg shadow-lg w-full max-w-md\">\n" +
                      "        <h1 class=\"text-3xl font-bold text-center text-gray-800 mb-6\">Registrar Nuevo Estudiante</h1>\n" +
                      "        <form action=\"/students\" method=\"post\" class=\"space-y-4\">\n" +
                      "            <div>\n" +
                      "                <label for=\"firstName\" class=\"block text-gray-700 text-sm font-medium mb-1\">Nombre:</label>\n" +
                      "                <input type=\"text\" id=\"firstName\" name=\"firstName\" required\n" +
                      "                       class=\"w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500\">\n" +
                      "            </div>\n" +
                      "            <div>\n" +
                      "                <label for=\"lastName\" class=\"block text-gray-700 text-sm font-medium mb-1\">Apellido:</label>\n" +
                      "                <input type=\"text\" id=\"lastName\" name=\"lastName\" required\n" +
                      "                       class=\"w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500\">\n" +
                      "            </div>\n" +
                      "            <div>\n" +
                      "                <label for=\"dni\" class=\"block text-gray-700 text-sm font-medium mb-1\">DNI:</label>\n" +
                      "                <input type=\"text\" id=\"dni\" name=\"dni\" required\n" +
                      "                       class=\"w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500\">\n" +
                      "            </div>\n" +
                      "            <div>\n" +
                      "                <label for=\"email\" class=\"block text-gray-700 text-sm font-medium mb-1\">Email:</label>\n" +
                      "                <input type=\"email\" id=\"email\" name=\"email\" required\n" +
                      "                       class=\"w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500\">\n" +
                      "            </div>\n" +
                      "            <div>\n" +
                      "                <label for=\"address\" class=\"block text-gray-700 text-sm font-medium mb-1\">Dirección:</label>\n" +
                      "                <input type=\"text\" id=\"address\" name=\"address\" required\n" +
                      "                       class=\"w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500\">\n" +
                      "            </div>\n" +
                      "            <button type=\"submit\"\n" +
                      "                    class=\"w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2\">\n" +
                      "                Registrar Estudiante\n" +
                      "            </button>\n" +
                      "            <div class=\"text-center mt-4\">\n" +
                      "                <a href=\"/students\" class=\"text-blue-600 hover:underline text-sm\">Ver todos los estudiantes (JSON)</a>\n" +
                      "            </div>\n" +
                      "            <div class=\"text-center\">\n" +
                      "                <a href=\"/\" class=\"text-blue-600 hover:underline text-sm\">Volver al inicio (JSON)</a>\n" +
                      "            </div>\n" +
                      "        </form>\n" +
                      "    </div>\n" +
                      "</body>\n" +
                      "</html>";
    return htmlForm;});























    }
}