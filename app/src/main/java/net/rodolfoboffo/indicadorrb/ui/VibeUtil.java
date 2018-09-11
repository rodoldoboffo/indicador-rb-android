package net.rodolfoboffo.indicadorrb.ui;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class VibeUtil extends AbstractServiceRelatedObject {

    public VibeUtil(IndicadorService service) {
        super(service);
    }

    private void vibrar(long mili) {
        Vibrator v = this.getVibeService();
        if (v != null || v.hasVibrator()) {
            v.vibrate(mili);
            Log.d(this.getClass().getName(), "Vibe OK!");
        }
        else {
            Log.d(this.getClass().getName(), "Impossivel vibrar");
        }
    }

    public void vibrarCurto() {
        this.vibrar(40);
    }

    public void vibrarLongo() {
        this.vibrar(300);
    }

    private Vibrator getVibeService() {
        Vibrator vibe = (Vibrator) this.service.getSystemService(this.service.VIBRATOR_SERVICE);
        return vibe;
    }

}
