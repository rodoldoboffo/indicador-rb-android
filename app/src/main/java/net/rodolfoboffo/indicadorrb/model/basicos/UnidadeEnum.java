package net.rodolfoboffo.indicadorrb.model.basicos;

import net.rodolfoboffo.indicadorrb.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnidadeEnum extends AbstractEnumeration {

    private static final Map<String, List<UnidadeEnum>> mapaGrandezaUnidade = new HashMap<>();

    protected GrandezaEnum grandeza;

    public static final UnidadeEnum tf = new UnidadeEnum("TF", R.string.tf, GrandezaEnum.forca);
    public static final UnidadeEnum kgf = new UnidadeEnum("KGF", R.string.kgf, GrandezaEnum.forca);
    public static final UnidadeEnum gf = new UnidadeEnum("GF", R.string.gf, GrandezaEnum.forca);
    public static final UnidadeEnum mpa = new UnidadeEnum("MPA", R.string.mpa, GrandezaEnum.tensao);
    public static final UnidadeEnum gpa = new UnidadeEnum("GPA", R.string.gpa, GrandezaEnum.tensao);
    public static final UnidadeEnum celsius = new UnidadeEnum("CELSIUS", R.string.celsius, GrandezaEnum.temperatura);
    public static final UnidadeEnum farenheit = new UnidadeEnum("FARENHEIT", R.string.farenheit, GrandezaEnum.temperatura);

    protected UnidadeEnum(String codigo, int resourceId, GrandezaEnum grandeza) {
        super(codigo, resourceId);
        this.grandeza = grandeza;
        this.putOnGrandezaUnidadeMap(this);
    }

    public GrandezaEnum getGrandeza() {
        return grandeza;
    }

    private void putOnGrandezaUnidadeMap(UnidadeEnum unidade) {
        if (!mapaGrandezaUnidade.containsKey(unidade.getGrandeza().getCodigo())) {
            mapaGrandezaUnidade.put(unidade.getGrandeza().getCodigo(), new ArrayList<UnidadeEnum>());
        }
        mapaGrandezaUnidade.get(unidade.getGrandeza().getCodigo()).add(unidade);
    }

    public static List<UnidadeEnum> getAllByGrandeza(GrandezaEnum grandeza) {
        if (!mapaGrandezaUnidade.containsKey(grandeza.getCodigo())) {
            return new ArrayList<>();
        }
        return mapaGrandezaUnidade.get(grandeza.getCodigo());
    }
}
