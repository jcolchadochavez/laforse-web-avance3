package pe.laforse.web.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/** Perito informático del laboratorio (tabla perito). Se usa también como objeto del formulario. */
@Entity
@Table(name = "perito")
public class Perito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ingrese los nombres y apellidos")
    @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
    @Column(nullable = false, length = 80)
    private String nombres;

    @NotBlank(message = "Ingrese la especialidad")
    @Size(max = 60, message = "La especialidad admite hasta 60 caracteres")
    @Column(nullable = false, length = 60)
    private String especialidad;

    @NotBlank(message = "Ingrese el correo institucional")
    @Email(message = "Formato de correo no válido")
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Pattern(regexp = "^$|^9\\d{8}$", message = "Celular peruano de 9 dígitos (inicia con 9)")
    @Column(length = 9)
    private String telefono;

    /** Un perito inactivo no puede recibir casos nuevos. */
    @Column(nullable = false)
    private boolean activo = true;

    public Perito() { }

    public Perito(String nombres, String especialidad, String correo, String telefono) {
        this.nombres = nombres;
        this.especialidad = especialidad;
        this.correo = correo;
        this.telefono = telefono;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
