package com.game.settings;

public class EnumSetting implements Setting<Enum<?>>{
    private final String name;
    private final Class<? extends Enum<?>> value;
    private Enum<?> enumValue;


    public EnumSetting (String name, Class<? extends Enum<?>> value){
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Enum<?> getValue() {
        return enumValue;
    }

    @Override
    public void setValue(Enum<?> value) {
        enumValue = value;
    }

    @Override
    public void nextValue() {
        enumValue = value.getEnumConstants()[(enumValue.ordinal() + 1)%value.getEnumConstants().length];
    }

    @Override
    public void previousValue() {
        enumValue = value.getEnumConstants()[(enumValue.ordinal() - 1) >= 0 ? (enumValue.ordinal() - 1) : value.getEnumConstants().length-1];
    }

    public Class<? extends Enum<?>> getEnumClass() {
        return value;
    }


}
