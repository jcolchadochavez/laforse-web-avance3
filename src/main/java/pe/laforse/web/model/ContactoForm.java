package pe.laforse.web.model;

import jakarta.validation.constraints.*;

public class ContactoForm {

    @NotBlank(message = "Ingrese sus nombres completos")
    @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
    private String nombre;

    @NotBlank(message = "Ingrese su correo electrónico")
    @Email(message = "Formato de correo no válido")
    private String correo;

    @Pattern(regexp = "^$|^9\\d{8}$", message = "Ingrese un celular peruano de 9 dígitos (inicia con 9)")
    private String telefono;

    @NotNull(message = "Seleccione el tipo de caso")
    private Integer servicioId;

    @NotNull(message = "Seleccione el nivel de urgencia")
    private Urgencia urgencia;

    @NotBlank(message = "Describa brevemente el caso")
    @Size(min = 20, max = 600, message = "La descripción debe tener entre 20 y 600 caracteres")
    private String mensaje;

    @AssertTrue(message = "Debe aceptar el tratamiento confidencial de su información")
    private boolean acepta;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Integer getServicioId() { return servicioId; }
    public void setServicioId(Integer servicioId) { this.servicioId = servicioId; }
    public Urgencia getUrgencia() { return urgencia; }
    public void setUrgencia(Urgencia urgencia) { this.urgencia = urgencia; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public boolean isAcepta() { return acepta; }
    public void setAcepta(boolean acepta) { this.acepta = acepta; }
}
