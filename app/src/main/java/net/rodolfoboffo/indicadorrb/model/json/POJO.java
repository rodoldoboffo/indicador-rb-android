package net.rodolfoboffo.indicadorrb.model.json;

public abstract class POJO<T> {

    public POJO() {}

    public POJO(T objeto) {}

    public abstract T convertToModel();

}
