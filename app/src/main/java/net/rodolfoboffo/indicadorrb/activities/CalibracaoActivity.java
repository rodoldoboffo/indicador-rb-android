package net.rodolfoboffo.indicadorrb.activities;

import android.content.Intent;
import android.view.View;

import net.rodolfoboffo.indicadorrb.R;

public class CalibracaoActivity extends AbstractBaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_calibracao;
    }

    public void onNovaCalibracaoButtonClick(View view) {
        EditarCalibracaoActivity.novaCalibracao(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EditarCalibracaoActivity.NOVA_CALIBRACAO:
                this.resultadoNovaCalibracao(resultCode, data);
            default:
                break;
        }
    }

    private void resultadoNovaCalibracao(int resultCode, Intent data) {

    }
}
