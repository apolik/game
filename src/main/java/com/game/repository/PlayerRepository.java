package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Override
    List<Player> findAll();

    Player getById(long id);

    @Override
    <S extends Player> S save(S entity);
}
