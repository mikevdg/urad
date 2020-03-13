package gulik.urad.value;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateValue extends Value {
    private Date value;
    private static final DateFormat iso8601=new SimpleDateFormat("yyyy-MM-dd");

    public DateValue(Date value) {
        this.value = value;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public String toString() {
                return iso8601.format(value);
    }
}
