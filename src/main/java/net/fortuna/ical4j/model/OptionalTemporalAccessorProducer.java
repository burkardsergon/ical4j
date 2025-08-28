package net.fortuna.ical4j.model;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Optional;

import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;

/**
 * This class contains alternatives for the e.g. {@link OffsetDateTime#from(TemporalAccessor)} methods of the following classes
 * <ul>
 * <li>{@link OffsetDateTime}</li>
 * <li>{@link LocalDateTime}</li>
 * <li>{@link LocalDate}</li>
 * <li>{@link LocalTime}</li>
 * <li>{@link ZoneOffset}</li>
 * <li>{@link Instant}</li>
 * </ul>
 * <p>
 * Instead of throwing an exception, they return an empty optional (unexpected exceptions are still being thrown).
 */
public class OptionalTemporalAccessorProducer {

    public static Optional<OffsetDateTime> offsetDateTime(TemporalAccessor temporal) {
        if (temporal == null) {
            return Optional.empty();
        }
        if (temporal instanceof OffsetDateTime) {
            return Optional.of((OffsetDateTime) temporal);
        }
        Optional<ZoneOffset> offset = zoneOffset(temporal);
        if (offset.isEmpty()) {
            return Optional.empty();
        }
        LocalDate date = temporal.query(TemporalQueries.localDate());
        LocalTime time = temporal.query(TemporalQueries.localTime());
        if (date != null && time != null) {
            return Optional.of(OffsetDateTime.of(date, time, offset.get()));
        } else {
            Instant instant = Instant.from(temporal);
            return Optional.of(OffsetDateTime.ofInstant(instant, offset.get()));
        }
    }

    public static Optional<LocalDateTime> localDateTime(TemporalAccessor temporal) {
        if (temporal == null) {
            return Optional.empty();
        }
        if (temporal instanceof LocalDateTime) {
            return Optional.of((LocalDateTime) temporal);
        } else if (temporal instanceof ZonedDateTime) {
            return Optional.of(((ZonedDateTime) temporal).toLocalDateTime());
        } else if (temporal instanceof OffsetDateTime) {
            return Optional.of(((OffsetDateTime) temporal).toLocalDateTime());
        }
        Optional<LocalDate> localDate = localDate(temporal);
        if (localDate.isEmpty()) {
            return Optional.empty();
        }
        Optional<LocalTime> localTime = localTime(temporal);
        return localTime.map(time -> LocalDateTime.of(localDate.get(), time));
    }

    public static Optional<LocalDate> localDate(TemporalAccessor temporal) {
        if (temporal == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(temporal.query(TemporalQueries.localDate()));
    }

    public static Optional<LocalTime> localTime(TemporalAccessor temporal) {
        if (temporal == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(temporal.query(TemporalQueries.localTime()));
    }

    public static Optional<ZoneOffset> zoneOffset(TemporalAccessor temporal) {
        if (temporal == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(temporal.query(TemporalQueries.offset()));
    }

    public static Optional<Instant> instant(TemporalAccessor temporal) {
        if (temporal == null) {
            return Optional.empty();
        }
        if (temporal instanceof Instant) {
            return Optional.of((Instant) temporal);
        }
        long instantSecs = temporal.getLong(INSTANT_SECONDS);
        int nanoOfSecond = temporal.get(NANO_OF_SECOND);
        return Optional.of(Instant.ofEpochSecond(instantSecs, nanoOfSecond));
    }
}
