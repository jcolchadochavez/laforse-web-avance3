package pe.laforse.web.exception;

/**
 * Se lanza cuando una operación viola una regla de negocio (HTTP 409).
 * campo: nombre del atributo del formulario al que se asocia el error (puede ser null).
 */
public class ReglaNegocioException extends RuntimeException {
    private final String campo;

    public ReglaNegocioException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public ReglaNegocioException(String mensaje) {
        this(null, mensaje);
    }

    public String getCampo() { return campo; }
}
