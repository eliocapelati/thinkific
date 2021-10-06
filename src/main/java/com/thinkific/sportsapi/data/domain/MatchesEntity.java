package com.thinkific.sportsapi.data.domain;


import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document("matches")
public class MatchesEntity extends AbstractAuditEntity {

    @Id
    private String id;
    @Indexed
    private String teamId;
    private String opponent;
    private String location;
    @Indexed
    private LocalDateTime date;
    private List<String> details = new ArrayList<>();
    private Set<PlayerResponse> players = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public void setDetails(String detail){
        this.details.add(detail);
    }

    public Set<PlayerResponse> getPlayers() {
        return players;
    }

    public void setPlayers(Set<PlayerResponse> players) {
        this.players = players;
    }

    public void addPlayers(PlayerResponse players){
        this.players.add(players);
    }

    @Override
    public String toString() {
        return "MatchesEntity{" +
                "id='" + id + '\'' +
                ", teamId='" + teamId + '\'' +
                ", opponent='" + opponent + '\'' +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", details=" + details +
                ", players=" + players +
                '}';
    }
}
