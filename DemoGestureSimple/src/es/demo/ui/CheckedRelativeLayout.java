package es.demo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * RelativeLayout que implementa la interfaz Checkable para
 * gestionar estados de selección/deselección
 */
public class CheckedRelativeLayout extends RelativeLayout implements Checkable {
 
    /**
     * Variable para almacenar el estado de este widget
     */
    private boolean mChecked = false;
 
    /**
     * Este array se usa para que los drawables que se usen
     * reaccionen al cambio de estado especificado
     * En nuestro caso al "state_checked"
     * que es el que utilizamos en nuestro selector
     */
    private final int[] STATE_CHECKABLE = {
            android.R.attr.state_checked
    };
 
    public CheckedRelativeLayout(Context context) {
        super(context);
    }
 
    public CheckedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    /**
     * Este método es el que cambia el estado de nuestro widget
     * @param checked true para activarlo y false para desactivarlo
     */
    public void setChecked(boolean checked) {
        mChecked = checked;
        //Cuando cambiamos el estado, debemos informar a los drawables
        //que este widget tenga vinculados
        refreshDrawableState();
        invalidate();
    }
 
    /**
     * Este método devuelve el estado de nuestro widget
     * @return true o false
     */
    public boolean isChecked() {
        return mChecked;
    }
 
    /**
     * Este método cambia el estado de nuestro widget
     * Si estaba activo se desactiva y viceversa
     */
    public void toggle() {
        setChecked(!mChecked);
    }
 
    /**
     * Este método es un poco más complejo
     * Se encarga de combinar los diferentes "estados" de un widget
     * para informar a los drawables.
     *
     * Si nuestro widget está "checked" le añadimos el estado CHECKED_STATE_SET
     * que definimos al principio
     *
     * @return el array de estados de nuestro widget
     */
    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, STATE_CHECKABLE);
        }
        return drawableState;
    }
}