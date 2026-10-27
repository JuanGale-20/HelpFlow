package com.helpflow.dispatch.service;

import com.helpflow.dispatch.dto.DispatchRequest;
import com.helpflow.dispatch.dto.DispatchResponse;
import org.springframework.stereotype.Service;

@Service
public class DispatchService {

    private static final int TOTAL_TECNICOS = 20;
    private static final int MAX_HORAS = 12;

    public DispatchResponse dispatch(DispatchRequest request) {
        int seed = seed(request);

        int tecnicoNumero = Math.floorMod(seed, TOTAL_TECNICOS) + 1;
        int horas = Math.floorMod(seed / TOTAL_TECNICOS, MAX_HORAS + 1);

        return new DispatchResponse(
                request.ticketId(),
                String.format("TEC-%03d", tecnicoNumero),
                clasificar(horas),
                horas
        );
    }

    // String.hashCode() es estable entre ejecuciones, por eso el resultado es determinista
    private int seed(DispatchRequest request) {
        String key = request.ticketId() + "|" + request.categoria() + "|" + request.prioridad();
        return key.hashCode();
    }

    private String clasificar(int horas) {
        if (horas <= 2) return "ALTA";
        if (horas <= 8) return "MEDIA";
        return "BAJA";
    }
}