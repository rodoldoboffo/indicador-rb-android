package net.rodolfoboffo.indicadorrb.model.dispositivos;

import java.util.Objects;

public class DispositivoBLE {
    private String nome;
    private String endereco;

    public DispositivoBLE(String nome, String endereco) {
        this.nome = nome;
        this.endereco = endereco;
    }

    public DispositivoBLE(String nome) {
        this(nome, null);
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DispositivoBLE that = (DispositivoBLE) o;
        if (this.nome != null) {
            if (!this.nome.equals(that.nome)) return false;
        }
        if (this.endereco != null) {
            if (!this.endereco.equals(that.endereco)) return false;
        }
        return true;
    }

}
