package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;



import org.javalite.activejdbc.annotations.BelongsTo;
@Table("users") // Esta anotación asocia explícitamente el modelo 'User' con la tabla 'users' en la DB.
@BelongsTo(parent = Team.class, foreignKeyName ="team_id") // Un User pertenece a un Team
public class User extends Model {

    // ActiveJDBC mapea automáticamente las columnas de la tabla 'users'
    // (como 'id', 'name', 'password', etc.) a los atributos de esta clase.
    // No necesitas declarar los campos (id, name, password) aquí como variables de instancia,
    // ya que la clase Model base se encarga de la interacción con la base de datos.

    // Opcional: Puedes agregar métodos getters y setters si prefieres un acceso más tipado,
    // aunque los métodos genéricos de Model (getString(), set(), getInteger(), etc.) ya funcionan.

    public String getName() {
        return getString("name"); // Obtiene el valor de la columna 'name'
    }

    public void setName(String name) {
        set("name", name); // Establece el valor para la columna 'name'
    }

    public String getPassword() {
        return getString("password"); // Obtiene el valor de la columna 'password'
    }

    public void setPassword(String password) {
        set("password", password); // Establece el valor para la columna 'password'
    }

    // Opcional: Métodos para validaciones, lógica de negocio específica de usuario, etc.
    // Por ejemplo:
    // public boolean isValidPassword(String inputPassword) {
    //     // Aquí implementarías la lógica de verificación de contraseña hasheada
    //     return get("password").equals(inputPassword); // Solo un ejemplo, NUNCA usar en producción con contraseñas en texto plano.
    // }
}