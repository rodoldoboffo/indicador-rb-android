package net.rodolfoboffo.indicadorrb.model.basicos;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractEnumeration implements Serializable {

    private String codigo;
    private int resourceId;

    private static final Map<Class, Map<String, AbstractEnumeration>> map = new HashMap<>();

    public AbstractEnumeration() {}

    protected AbstractEnumeration(String n, int resourceId) {
        this.codigo = n;
        this.resourceId = resourceId;
        this.putOnMap();
    }

    public String getCodigo() {
        return this.codigo;
    }

    public int getResourceString() {
        return this.resourceId;
    }

    private void putOnMap() {
        Map<String, AbstractEnumeration> mapEnums = null;
        if (!map.containsKey(this.getClass())) {
            map.put(this.getClass(), new HashMap<String, AbstractEnumeration>());
        }
        mapEnums = map.get(this.getClass());
        mapEnums.put(this.getCodigo(), this);
    }

    public static <T extends AbstractEnumeration> List<T> getAll(Class<T> clss) {
        Map<String, AbstractEnumeration> mapEnum = map.get(clss);
        if (mapEnum == null) {
            try {
                initializeEnum(clss);
            }
            catch (ClassNotFoundException e) {
                Log.d(AbstractEnumeration.class.getName(), String.format("Impossível encontrar enum %s", clss.getName()));
                return new ArrayList<>();
            }
            mapEnum = map.get(clss);
        }
        List<T> lista = new ArrayList<>();
        lista.addAll((Collection<? extends T>) mapEnum.values());
        return lista;
    }

    private static <T extends AbstractEnumeration> void initializeEnum(final Class<T> enumClass) throws ClassNotFoundException {
        Class.forName(enumClass.getName(), true, Thread.currentThread().getContextClassLoader());
    }

    public static <T extends AbstractEnumeration> T getByKey(Class<T> clss, String codigo) {
        return (T)map.get(clss).get(codigo);
    }
}
