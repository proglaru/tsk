package com.example.players;

import com.example.players.dto.Currency;
import com.example.players.dto.Item;
import com.example.players.dto.Player;
import com.example.players.dto.Progress;

import java.util.List;

public class CrudService {
    protected List<Player> players;

    protected Player findPlayerWithId(long id) {
        for (Player player : players) {
            if (player.getPlayerId() == id) {
                return player;
            }
        }
        return null;
    }

    protected Progress findProgressWithId(long id) {
        for (Player player : players) {
            for (Progress progress : player.getProgresses()) {
                if (progress.getId() == id) {
                    return progress;
                }
            }
        }
        return null;
    }

    protected Currency findCurrencyWithId(long id) {
        for (Player player : players) {
            if (player.getCurrencies().containsKey(id)) {
                return player.getCurrencies().get(id);
            }
        }
        return null;
    }

    protected Item findItemWithId(long id) {
        for (Player player : players) {
            if (player.getItems().containsKey(id)) {
                return player.getItems().get(id);
            }
        }
        return null;
    }
}
