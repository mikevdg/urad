package gulik.urad.impl;

import gulik.urad.Type;

public class Column implements gulik.urad.Column {
    String name;
    String title;
    Type type;
    int position;

    @Override
    public String getName() {
        return name;
    }

    public Column setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public Column setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Type getType() {
        return type;
    }

    public Column setType(Type type) {
        this.type = type;
        return this;
    }

    @Override
    public int getPosition() {
        return position;
    }

    public Column setPosition(int position) {
        this.position = position;
        return this;
    }
}
