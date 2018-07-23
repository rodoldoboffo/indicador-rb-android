package net.rodolfoboffo.indicadorrb.model.basicos;

import net.rodolfoboffo.indicadorrb.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnidadeEnum extends AbstractEnumeration {

    private static final Map<GrandezaEnum, List<UnidadeEnum>> mapaGrandezaUnidade = new HashMap<>();

    protected GrandezaEnum grandeza;

    public static final UnidadeEnum kgf = new UnidadeEnum("KGF", R.string.kgf, GrandezaEnum.forca);
    public static final UnidadeEnum tf = new UnidadeEnum("TF", R.string.gf, GrandezaEnum.forca);
    public static final UnidadeEnum mpa = new UnidadeEnum("MPA", R.string.mpa, GrandezaEnum.tensao);
    public static final UnidadeEnum gpa = new UnidadeEnum("GPA", R.string.gpa, GrandezaEnum.tensao);
    public static final UnidadeEnum celsius = new UnidadeEnum("CELSIUS", R.string.celsius, GrandezaEnum.temperatura);

    protected UnidadeEnum(String codigo, int resourceId, GrandezaEnum grandeza) {
        super(codigo, resourceId);
        this.grandeza = grandeza;
        this.putOnGrandezaUnidadeMap(this);
    }

    public GrandezaEnum getGrandeza() {
        return grandeza;
    }

    private void putOnGrandezaUnidadeMap(UnidadeEnum unidade) {
        if (!mapaGrandezaUnidade.containsKey(unidade.getGrandeza())) {
            mapaGrandezaUnidade.put(unidade.getGrandeza(), new ArrayList<UnidadeEnum>());
        }
        mapaGrandezaUnidade.get(unidade.getGrandeza()).add(unidade);
    }

    public static List<UnidadeEnum> getAllByGrandeza(GrandezaEnum grandeza) {
        if (!mapaGrandezaUnidade.containsKey(grandeza)) {
            return new ArrayList<>();
        }
        return mapaGrandezaUnidade.get(grandeza);
    }
}
