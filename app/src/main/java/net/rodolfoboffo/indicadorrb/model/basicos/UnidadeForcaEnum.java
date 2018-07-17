package net.rodolfoboffo.indicadorrb.model.basicos;

import net.rodolfoboffo.indicadorrb.R;

public class UnidadeForcaEnum extends AbstractEnumeration {
    public static final UnidadeForcaEnum forca = new UnidadeForcaEnum("KGF", R.string.kgf);
    public static final UnidadeForcaEnum tensao = new UnidadeForcaEnum("TF", R.string.gf);

    protected UnidadeForcaEnum(String codigo, int resourceId) {
        super(codigo, resourceId);
    }
}
