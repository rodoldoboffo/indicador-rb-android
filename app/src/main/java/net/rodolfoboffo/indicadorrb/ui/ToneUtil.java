package net.rodolfoboffo.indicadorrb.ui;

import android.media.AudioManager;
import android.media.ToneGenerator;

import net.rodolfoboffo.indicadorrb.model.basicos.AbstractServiceRelatedObject;
import net.rodolfoboffo.indicadorrb.services.IndicadorService;

public class ToneUtil extends AbstractServiceRelatedObject {

    public ToneUtil(IndicadorService service) {
        super(service);
    }

    public void tocarBeep() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE,150);
    }

}
