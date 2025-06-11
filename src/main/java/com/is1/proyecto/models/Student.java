package com.is1.proyecto.models;

import io.ebean.Finder; // Importar Finder para consultas
import io.ebean.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // Indica que esta clase es una entidad de persistencia
@Table(name = "student") // Opcional: define el nombre de la tabla si es diferente al de la clase
public class Student extends Model {

    @Id // Marca el campo como la clave primaria
    private Long id;

    private String firstName;
    private String lastName;
    private String dni; // Documento Nacional de Identidad
    private String email;
    private String address;

    // Finder estático para realizar consultas. 'Student' es el tipo de entidad, 'Long' es el tipo de ID.
    public static final Finder<Long, Student> find = new Finder<>(Student.class);

    // Constructor vacío (necesario para ORMs)
    public Student() {
    }

    // Constructor con campos para facilitar la creación de objetos
    public Student(String firstName, String lastName, String dni, String email, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.email = email;
        this.address = address;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Student{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", dni='" + dni + '\'' +
               ", email='" + email + '\'' +
               ", address='" + address + '\'' +
               '}';
    }
}