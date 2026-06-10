package simulador.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import simulador.domain.Equipo;
import simulador.dto.Goleador;
import simulador.dto.ResultadoPartido;
import simulador.events.EventoPartido;
import simulador.state.EstadoPartido;
import simulador.state.Finalizado;
import simulador.state.NoIniciado;

public class Partido implements ComponenteTorneo {
    private Equipo local;
    private Equipo visitante;
    private int golesLocal;
    private int golesVisitante;
    private EstadoPartido estado;
    private int minuto;
    private List<EventoPartido> eventos;

    public Partido(Equipo local, Equipo visitante) {
        this.local = local;
        this.visitante = visitante;
        this.estado = new NoIniciado();
        this.eventos = new ArrayList<>();
    }

    public Equipo getLocal() {
        return local;
    }

    public Equipo getVisitante() {
        return visitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public EstadoPartido getEstado() {
        return estado;
    }

    public int getMinuto() {
        return minuto;
    }

    public List<EventoPartido> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    public void iniciar() {
        estado.iniciar(this);
    }

    public void avanzarMinuto() {
        minuto++;
        if (minuto == 45 || minuto == 46 || minuto == 90) {
            estado.avanzar(this);
        }
    }

    public void setEstado(EstadoPartido e) {
        this.estado = e;
    }

    public void registrarEvento(EventoPartido e) {
        eventos.add(e);
    }

    public Equipo getGanador() {
        if (!estaCompleto() || golesLocal == golesVisitante) {
            return null;
        }
        return golesLocal > golesVisitante ? local : visitante;
    }

    @Override
    public List<ResultadoPartido> getResultados() {
        ResultadoPartido resultado = new ResultadoPartido(
                local.getNombre(),
                visitante.getNombre(),
                golesLocal,
                golesVisitante,
                getGanador() != null ? getGanador().getNombre() : null,
                estado.getNombre());
        return List.of(resultado);
    }

    @Override
    public boolean estaCompleto() {
        return estado instanceof Finalizado;
    }

    @Override
    public List<Goleador> getGoleadores() {
        return List.of();
    }
}
