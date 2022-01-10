package com.dunice.nerd_kotlin.common.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.Nullable;

import java.time.OffsetDateTime;
import java.util.Date;


@WritingConverter
public class OffsetDateTimeToDocumentConverter implements Converter<OffsetDateTime, Document> {

    static final String DATE_TIME = "dateTime";
    static final String ZONE = "zone";

    @Override
    public Document convert(@Nullable OffsetDateTime zonedDateTime) {
        if (zonedDateTime == null) return null;
        Document document = new Document();
        document.put(DATE_TIME, Date.from(zonedDateTime.toInstant()));
        document.put(ZONE, zonedDateTime.getOffset().toString());
        return document;
    }
}