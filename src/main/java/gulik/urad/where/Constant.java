package gulik.urad.where;

import gulik.urad.value.Value;

public class Constant extends Clause {
    Value value;

    public Constant(Value value) {
        this.value = value;
    }
}
