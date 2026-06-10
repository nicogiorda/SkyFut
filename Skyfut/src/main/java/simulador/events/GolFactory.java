package simulador.events;

import simulador.domain.IJugador;
import simulador.dto.ContextoEvento;

public class GolFactory implements EventoFactory {
    @Override
    public EventoPartido crearEvento(ContextoEvento ctx) {
        double ataque = ctx.getRendimientoAtaque(ctx.local);
        double defensa = ctx.getRendimientoDefensa(ctx.visitante);

        
    }
}