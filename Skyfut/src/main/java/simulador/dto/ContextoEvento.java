package simulador.dto;

import simulador.domain.Equipo;

/**
 * [PATRON: (ninguno) — DTO inmutable; desacopla factories de Partido]
 *
 * Que hace: Empaqueta el estado relevante del partido en un minuto dado para
 * pasarlo a las factories sin exponer el objeto Partido completo. Es inmutable
 * (clase final, todos los campos final, sin setters). Calcula getRendimientoAtaque()
 * y getRendimientoDefensa() aplicando los modificadores tacticos del equipo y el
 * modificador del equipo favorecido (DT), desacoplando esta logica de las factories.
 *
 * Relaciones:
 * - Hereda de: (ninguna)
 * - Composicion con: (ninguna — referencias no exclusivas)
 * - Asociacion con: Equipo local, Equipo visitante (referencias de solo lectura)
 * - Usada por (dependencia): GolFactory, TarjetaFactory, LesionFactory, CambioFactory
 *   (todas reciben un ContextoEvento en crearEvento()), MotorSimulacion (lo construye
 *   y lo pasa a las factories), GestorPartido (lo construye para cambios manuales)
 * - Crea (Creator GRASP): (no aplica)
 *
 * GRASP:
 * - Bajo Acoplamiento: cumple porque aísla a las factories del objeto Partido completo;
 *   solo reciben el contexto minimo necesario para tomar su decision.
 * - Information Expert: cumple porque centraliza el calculo del rendimiento efectivo
 *   (rendimientoTotal * modificadorTactica * modificadorEquipoDT), que requiere
 *   informacion de Equipo y TacticaStrategy.
 */
public final class ContextoEvento {
    private final int minuto;
    private final Equipo local;
    private final Equipo visitante;
    private final boolean esSegundoTiempo;
    private final int idEquipoFavorecido;
    private final double modificadorEquipoFavorecido;

    public ContextoEvento(int minuto, Equipo local, Equipo visitante, boolean esSegundoTiempo) {
        this(minuto, local, visitante, esSegundoTiempo, -1, 1.0);
    }

    public ContextoEvento(
            int minuto,
            Equipo local,
            Equipo visitante,
            boolean esSegundoTiempo,
            int idEquipoFavorecido,
            double modificadorEquipoFavorecido) {
        this.minuto = minuto;
        this.local = local;
        this.visitante = visitante;
        this.esSegundoTiempo = esSegundoTiempo;
        this.idEquipoFavorecido = idEquipoFavorecido;
        this.modificadorEquipoFavorecido = modificadorEquipoFavorecido;
    }

    public int getMinuto() {
        return minuto;
    }

    public Equipo getLocal() {
        return local;
    }

    public Equipo getVisitante() {
        return visitante;
    }

    public boolean isEsSegundoTiempo() {
        return esSegundoTiempo;
    }

    public double getRendimientoAtaque(Equipo e) {
        return e.getRendimientoTotal() * e.getTactica().getModificadorAtaque() * getModificador(e);
    }

    public double getRendimientoDefensa(Equipo e) {
        return e.getRendimientoTotal() * e.getTactica().getModificadorDefensa() * getModificador(e);
    }

    private double getModificador(Equipo equipo) {
        return equipo.getId() == idEquipoFavorecido ? modificadorEquipoFavorecido : 1.0;
    }
}
