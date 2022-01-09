package com.dunice.nerd_kotlin.common.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.Nullable;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;



@ReadingConverter
public class DocumentToOffsetDateTimeConverter implements Converter<Document, OffsetDateTime> {

    @Override
    public OffsetDateTime convert(@Nullable Document document) {
        if (document == null) return null;

        Date dateTime = document.getDate("dateTime");
        String zoneId = document.getString("zone");

        return OffsetDateTime.ofInstant(dateTime.toInstant(), ZoneId.of(zoneId));
    }
}