package com.game.repository;

import com.game.entity.Player;
import com.game.service.PlayerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest")
public class TestRepository {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerUtils utils;

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getResult(@PathVariable String id) {
        try {
            long id2 = Long.parseLong(id);
            if (id2 < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Player player = playerRepository.getById(id2);
            return player == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(player, HttpStatus.OK);
        } catch (NumberFormatException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> testString(@RequestParam Map<String, String> map) {
        try {
            int pageNumber = map.get("pageNumber") == null ? 0 : Integer.parseInt(map.remove("pageNumber"));
            int pageSize = map.get("pageSize") == null ? 3 : Integer.parseInt(map.remove("pageSize"));

            List<Player> players = utils.handleRequest(playerRepository.findAll(), map);
            players = players.stream()
                    .skip((long) pageNumber * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(players, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            // В тз про это ничего не написано, но я не понял, как обрабатывать исключения. Поэтому бросаю 400-ую ошибку
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/players/count")
    public ResponseEntity<Integer> getAllPlayer(@RequestParam Map<String, String> map) {
        List<Player> players = utils.handleRequest(playerRepository.findAll(), map);

        return new ResponseEntity<>(players.size(), HttpStatus.OK);
    }

    @PostMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable String id, @RequestBody Player newPlayer) {
        try {
            long id2 = Long.parseLong(id);
            if (id2 < 1 || !utils.checkValid(newPlayer)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Player player = playerRepository.getById(id2);
            if (player == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            if (newPlayer.getName() != null) player.setName(newPlayer.getName());
            if (newPlayer.getTitle() != null) player.setTitle(newPlayer.getTitle());
            if (newPlayer.getRace() != null) player.setRace(newPlayer.getRace());
            if (newPlayer.getProfession() != null) player.setProfession(newPlayer.getProfession());
            if (newPlayer.getBirthday() != null) player.setBirthday(newPlayer.getBirthday());
            if (newPlayer.isBanned() != null) player.setBanned(newPlayer.isBanned());
            player = utils.updateLevel(player, newPlayer);

            playerRepository.save(player);

            return new ResponseEntity<>(player, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable String id) {
        try {
            long id2 = Long.parseLong(id);
            if (id2 < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            Player player = playerRepository.getById(id2);
            if (player == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            playerRepository.delete(player);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        if (player.getName() == null || player.getTitle() == null || player.getRace() == null
                || player.getProfession() == null || player.getBirthday() == null || player.isBanned() == null
                || player.getExperience() == null || !utils.checkValid(player)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            player = utils.updateLevel(player, player);
            if (player.isBanned() == null)
                player.setBanned(false);
            return new ResponseEntity<>(playerRepository.save(player), HttpStatus.OK);
        }
    }
}
