package edots.utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * @author lilijiang
 * @since 1/13/15
 * Time picker dialog class
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TheListener listener;

    public interface TheListener{
        public void returnTime(Date time);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default time in the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        listener = (TheListener) getActivity();

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        if (listener != null)
        {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY,hour);
            cal.set(Calendar.MINUTE,minute);
            cal.set(Calendar.SECOND,0);
            cal.set(Calendar.MILLISECOND,0);
            Date d = cal.getTime();
            listener.returnTime(d);
        }
    }
}
