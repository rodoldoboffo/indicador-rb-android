package net.rodolfoboffo.indicadorrb.model.basicos;

import android.util.Log;

import net.rodolfoboffo.indicadorrb.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;

public class GrandezaEnum extends AbstractEnumeration {
    public static final GrandezaEnum forca = new GrandezaEnum("FORCA", R.string.forca);
    public static final GrandezaEnum tensao = new GrandezaEnum("TENSAO", R.string.tensao);
    public static final GrandezaEnum temperatura = new GrandezaEnum("TEMPERATURA", R.string.temperatura);

    protected GrandezaEnum(String codigo, int resourceId) {
        super(codigo, resourceId);
    }
}
