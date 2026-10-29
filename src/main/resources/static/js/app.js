// Inicializa componentes de Bootstrap que requieren JavaScript
document.addEventListener('DOMContentLoaded', function () {
  // Tooltips (iconos de redes sociales del footer)
  document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(function (el) {
    new bootstrap.Tooltip(el);
  });

  // Validación del lado del cliente con estilos de Bootstrap (.needs-validation)
  document.querySelectorAll('form.needs-validation').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      if (!form.checkValidity()) {
        e.preventDefault();
        e.stopPropagation();
      }
      form.classList.add('was-validated');
    });
  });

  // Modal de detalle de servicio: rellena el contenido con los data-* del botón que lo abrió
  var modal = document.getElementById('modalServicio');
  if (modal) {
    modal.addEventListener('show.bs.modal', function (e) {
      var b = e.relatedTarget;
      modal.querySelector('.modal-title').textContent = b.dataset.nombre;
      modal.querySelector('[data-campo="descripcion"]').textContent = b.dataset.descripcion;
      modal.querySelector('[data-campo="herramientas"]').textContent = b.dataset.herramientas;
      modal.querySelector('[data-campo="plazo"]').textContent = b.dataset.plazo;
      modal.querySelector('[data-campo="solicitar"]').href = b.dataset.solicitar;
      modal.querySelector('[data-campo="detalle"]').href = b.dataset.detalle;
    });
  }

  // Modal de confirmación de eliminación: toma la URL y el nombre del botón que lo abrió
  var modalEliminar = document.getElementById('modalEliminar');
  if (modalEliminar) {
    modalEliminar.addEventListener('show.bs.modal', function (e) {
      var b = e.relatedTarget;
      modalEliminar.querySelector('[data-campo="nombre"]').textContent = b.dataset.nombre;
      modalEliminar.querySelector('[data-campo="form"]').action = b.dataset.url;
    });
  }

  // Contador de caracteres del textarea del formulario
  var mensaje = document.getElementById('mensaje');
  var contador = document.getElementById('contadorMensaje');
  if (mensaje && contador) {
    var actualizar = function () { contador.textContent = mensaje.value.length + ' / 600'; };
    mensaje.addEventListener('input', actualizar);
    actualizar();
  }
});
