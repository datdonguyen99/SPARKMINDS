package net.sparkminds.librarymanagement.service.criteria;

import org.springframework.format.annotation.DateTimeFormat;
import tech.jhipster.service.filter.RangeFilter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class InstantFilterV2 extends RangeFilter<Instant> {
    private static final long serialVersionUID = 1L;

    public InstantFilterV2() {
    }

    public InstantFilterV2(InstantFilterV2 filterV2) {
        super(filterV2);
    }

    public InstantFilterV2 copy() {
        return new InstantFilterV2(this);
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilterV2 setEquals(String equals) {
        super.setEquals(convertStringFormatToInstant(equals));
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilterV2 setNotEquals(String equals) {
        super.setNotEquals(convertStringFormatToInstant(equals));
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilterV2 setGreaterThan(String equals) {
        super.setGreaterThan(convertStringFormatToInstant(equals));
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilterV2 setLessThan(String equals) {
        super.setLessThan(convertStringFormatToInstant(equals));
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilterV2 setGreaterThanOrEqual(String equals) {
        super.setGreaterThanOrEqual(convertStringFormatToInstant(equals));
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilterV2 setLessThanOrEqual(String equals) {
        super.setLessThanOrEqual(convertStringFormatToInstant(equals));
        return this;
    }

    private Instant convertStringFormatToInstant(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy HHmmss");
        LocalDateTime dateTime = LocalDateTime.parse(input, formatter);
        return dateTime.toInstant(ZoneOffset.UTC);
    }
}
