package com;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CharacterProperty {
    DEFAULT(0, "defautl"),
    PROFESSION(1, "Профессия"),
    SEX(2, "Пол"),
    AGE(3, "Возраст"),
    BODY_TYPE(4, "Телосложение"),
    HEALTH(5, "Состояние здоровья"),
    PHOBIA(6, "Фобия"),
    FEATURE(7, "Черта характера"),
    HOBBY(8, "Хобби"),
    ADDITIONAL_INFO(9, "Доп. информация"),
    TOOL(10, "Багаж"),
    FIRST_CARD(11, "Доп. карта №1"),
    SECOND_CARD(12, "Доп. карта №2");

    private int id;
    private String propertyName;

    CharacterProperty(int id, String propertyName) {
        this.id = id;
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public int getId() {
        return id;
    }

    public static CharacterProperty getByName(String name) {
        return Arrays.stream(CharacterProperty.values())
                .filter(characterProperty -> characterProperty.propertyName.equals(name))
                .findAny().orElse(DEFAULT);
    }

    public static List<String> toList() {
        return Arrays.stream(CharacterProperty.values())
                .map(CharacterProperty::getPropertyName)
                .collect(Collectors.toUnmodifiableList());
    }
}
