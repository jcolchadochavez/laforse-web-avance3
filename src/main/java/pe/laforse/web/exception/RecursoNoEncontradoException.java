package pe.laforse.web.exception;

/** Se lanza cuando un id no existe en la base de datos (HTTP 404). */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
