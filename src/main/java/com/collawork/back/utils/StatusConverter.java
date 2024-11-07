package com.collawork.back.utils;

import com.collawork.back.model.Friend;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Friend.Status, String> {
    @Override
    public String convertToDatabaseColumn(Friend.Status attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    @Override
    public Friend.Status convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Friend.Status.valueOf(dbData.toUpperCase());
    }
}
