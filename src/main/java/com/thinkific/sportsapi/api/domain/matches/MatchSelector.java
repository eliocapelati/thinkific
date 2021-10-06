package com.thinkific.sportsapi.api.domain.matches;


import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

public enum MatchSelector {
    FUTURE,
    PAST;

    public static MatchSelector get(LocalDateTime date){
        return now().isBefore(date) ? FUTURE : PAST;
    }
}
