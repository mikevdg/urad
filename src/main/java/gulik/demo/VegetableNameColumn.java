package gulik.demo;

import gulik.urad.Column;
import gulik.urad.Type;

/** TODO: delete me. I'm only for this demo. */
public class VegetableNameColumn implements Column {
    @Override
    public String getName() {
        return "Name";
    }

    @Override
    public String getTitle() {
        return "Name";
    }

    @Override
    public Type getType() {
        return Type.String;
    }
}
