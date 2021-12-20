package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PlayerUtils {
    public List<Player> handleRequest(List<Player> list, Map<String, String> query) {
        for (Map.Entry<String, String> q : query.entrySet()) {
            list = filterPlayers(list, q.getKey(), q.getValue());
        }

        return sortPlayers(list, query.get("order"));
    }

    private List<Player> filterPlayers(List<Player> players, String key, String value) throws IllegalArgumentException {
        switch (key) {
            case "name":
                return players.stream()
                        .filter(el -> el.getName().contains(value))
                        .collect(Collectors.toList());
            case "title":
                return players.stream()
                        .filter(el -> el.getTitle().contains(value))
                        .collect(Collectors.toList());
            case "race":
                return players.stream()
                        .filter(el -> el.getRace().name().equals(value))
                        .collect(Collectors.toList());
            case "profession":
                return players.stream()
                        .filter(el -> el.getProfession().name().equals(value))
                        .collect(Collectors.toList());
            case "after":
                return players.stream()
                        .filter(el -> el.getBirthday().after(new Date(Long.parseLong(value))))
                        .collect(Collectors.toList());
            case "before":
                return players.stream()
                        .filter(el -> el.getBirthday().before(new Date(Long.parseLong(value))))
                        .collect(Collectors.toList());
            case "banned":
                return players.stream()
                        .filter(el -> el.isBanned() == Boolean.parseBoolean(value))
                        .collect(Collectors.toList());
            case "minExperience":
                return players.stream()
                        .filter(el -> el.getExperience() >= Integer.parseInt(value))
                        .collect(Collectors.toList());
            case "maxExperience":
                return players.stream()
                        .filter(el -> el.getExperience() <= Integer.parseInt(value))
                        .collect(Collectors.toList());
            case "minLevel":
                return players.stream()
                        .filter(el -> el.getLevel() >= Integer.parseInt(value))
                        .collect(Collectors.toList());
            case "maxLevel":
                return players.stream()
                        .filter(el -> el.getLevel() <= Integer.parseInt(value))
                        .collect(Collectors.toList());
            default:
                return players;
        }
    }

    public Player updateLevel(Player player, Player newPlayer) {
        if (newPlayer.getExperience() == null) return player;

        player.setExperience(newPlayer.getExperience());
        player.setLevel((int) (Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());

        return player;
    }

    public boolean checkValid(Player player) {
        if (player.getName() != null && !checkBorders(player.getName().length(), 12, 1)
            || player.getTitle() != null && !checkBorders(player.getTitle().length(), 30, 0)
            || player.getExperience() != null && !checkBorders(player.getExperience(), 10000000, 0)
            || player.getBirthday() != null && !checkBorders(player.getBirthday().getYear() + 1900, 3000, 2000)) {
            return false;
        }

        return true;
    }

    public boolean checkBorders(int actualLength, int maxLength, int minLength) {
        return actualLength >= minLength && actualLength <= maxLength;
    }

    public List<Player> sortPlayers(List<Player> list, String order) {
        if (order == null)
            return list.stream()
                    .sorted(Comparator.comparing(Player::getId))
                    .collect(Collectors.toList());

        PlayerOrder playerOrder = PlayerOrder.valueOf(order);

        switch (playerOrder) {
            case ID:
                return list.stream()
                        .sorted(Comparator.comparing(Player::getId))
                        .collect(Collectors.toList());
            case BIRTHDAY:
                return list.stream()
                        .sorted(Comparator.comparing(Player::getBirthday))
                        .collect(Collectors.toList());
            case LEVEL:
                return list.stream()
                        .sorted(Comparator.comparing(Player::getLevel))
                        .collect(Collectors.toList());
            case NAME:
                return list.stream()
                        .sorted(Comparator.comparing(Player::getName))
                        .collect(Collectors.toList());
            case EXPERIENCE:
                return list.stream()
                        .sorted(Comparator.comparing(Player::getExperience))
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedOperationException();
        }
    }
}