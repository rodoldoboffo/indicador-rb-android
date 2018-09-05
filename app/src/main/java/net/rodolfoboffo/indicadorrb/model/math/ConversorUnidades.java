package net.rodolfoboffo.indicadorrb.model.math;

import android.content.Context;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;
import net.rodolfoboffo.indicadorrb.model.exceptions.GrandezaIncompativelException;

public class ConversorUnidades {

    public static Double converte(Double valor, UnidadeEnum deUnidade, UnidadeEnum paraUnidade) {
        if (!deUnidade.getGrandeza().equals(paraUnidade.getGrandeza())) {
            throw new GrandezaIncompativelException();
        }
        if (deUnidade.getGrandeza().equals(GrandezaEnum.forca)) {
            return converteForca(valor, deUnidade, paraUnidade);
        } else {
            return converteTemperatura(valor, deUnidade, paraUnidade);
        }
    }

    public static Double converteForca(Double valor, UnidadeEnum deUnidade, UnidadeEnum paraUnidade) {
        if (deUnidade.equals(paraUnidade)) {
            return  valor;
        }
        if (deUnidade.equals(UnidadeEnum.kgf)) {
            if (paraUnidade.equals(UnidadeEnum.gf)) {
                return valor * 1000.0;
            }
            else if (paraUnidade.equals(UnidadeEnum.tf)) {
                return valor / 1000.0;
            }
            return valor;
        }
        else if (deUnidade.equals(UnidadeEnum.tf) && paraUnidade.equals(UnidadeEnum.kgf)) {
            return valor * 1000;
        }
        else if (deUnidade.equals(UnidadeEnum.gf) && paraUnidade.equals(UnidadeEnum.kgf)) {
            return valor / 1000;
        }
        else {
            return converteForca(converteForca(valor, deUnidade, UnidadeEnum.kgf), UnidadeEnum.kgf, paraUnidade);
        }
    }

    public static Double converteTemperatura(Double valor, UnidadeEnum deUnidade, UnidadeEnum paraUnidade) {
        if (deUnidade.equals(paraUnidade)) {
            return  valor;
        }
        if (deUnidade.equals(UnidadeEnum.celsius)) {
            if (paraUnidade.equals(UnidadeEnum.farenheit)) {
                return valor * 9.0 / 5.0 + 32.0;
            }
            return valor;
        }
        else if (deUnidade.equals(UnidadeEnum.farenheit) && paraUnidade.equals(UnidadeEnum.celsius)) {
            return  5.0 * (valor - 32.0) / 9.0;
        }
        else {
            return converteTemperatura(converteTemperatura(valor, deUnidade, UnidadeEnum.celsius), UnidadeEnum.celsius, paraUnidade);
        }
    }
}
