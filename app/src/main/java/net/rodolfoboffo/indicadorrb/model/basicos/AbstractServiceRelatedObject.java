package net.rodolfoboffo.indicadorrb.model.basicos;

import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public abstract class AbstractServiceRelatedObject {

    protected IndicadorService service;

    protected AbstractServiceRelatedObject(IndicadorService service) {
        this.service = service;
    }

}
