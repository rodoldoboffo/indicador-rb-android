package net.rodolfoboffo.indicadorrb.model.math;

import android.content.Context;

import net.rodolfoboffo.indicadorrb.R;
import net.rodolfoboffo.indicadorrb.model.basicos.GrandezaEnum;
import net.rodolfoboffo.indicadorrb.model.basicos.UnidadeEnum;

public class ConversorUnidades {

    public static Double converte(Context context, Double valor, UnidadeEnum deUnidade, UnidadeEnum paraUnidade) {
        if (!deUnidade.getGrandeza().equals(paraUnidade.getGrandeza())) {
            throw new RuntimeException(context.getString(R.string.conversaoGrandezasIncompativeis));
        }
        if (deUnidade.getGrandeza().equals(GrandezaEnum.forca)) {
            return converteForca(context, valor, deUnidade, paraUnidade);
        } else {
            return converteTemperatura(context, valor, deUnidade, paraUnidade);
        }
    }

    public static Double converteForca(Context context, Double valor, UnidadeEnum deUnidade, UnidadeEnum paraUnidade) {
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
            return converteForca(context, converteForca(context, valor, deUnidade, UnidadeEnum.kgf), UnidadeEnum.kgf, paraUnidade);
        }
    }

    public static Double converteTemperatura(Context context, Double valor, UnidadeEnum deUnidade, UnidadeEnum paraUnidade) {
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
            return converteTemperatura(context, converteTemperatura(context, valor, deUnidade, UnidadeEnum.celsius), UnidadeEnum.celsius, paraUnidade);
        }
    }
}
