package com.cinemax.core.helper;

import com.cinemax.core.constants.CommonConstants;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MappingHelpers {

    @Named("formatDateTime")
    public String formatDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.format(DateTimeFormatter.ofPattern(CommonConstants.DATETIME_FORMAT));
    }

    @Named("enumName")
    public String enumName(Enum<?> e) {
        return e == null ? null : e.name();
    }
}
