package com.example.players.web;

import com.example.players.CrudService;
import com.example.players.db.Database;
import com.example.players.dto.Currency;
import com.example.players.dto.Item;
import com.example.players.dto.Player;
import com.example.players.dto.Progress;
import com.example.players.json.CashManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WebCrudService extends CrudService {
    Database database;
    private final Consumer<List<Player>> saveMethod;

    public WebCrudService(String path) {
        players = CashManager.read(path);
        if (players == null) {
            players = new ArrayList<>();
        }
        saveMethod = (players) -> CashManager.write(path, players);
    }

    public WebCrudService(Database database) {
        this.database = database;
        players = this.database.select();
        saveMethod = (players) -> {
            this.database.clearTables();
            this.database.insertPlayers(players);
        };
    }

    public List<Player> read() {
        return players;
    }

    // create methods
    public ResponseEntity<String> addPlayer(Player player) {
        if (findPlayerWithId(player.getPlayerId()) != null) {
            return new ResponseEntity<>("Игрок с таким id уже существует!", HttpStatus.BAD_REQUEST);
        }
        players.add(player);
        saveMethod.accept(players);
        return new ResponseEntity<>("Игрок добавлен", HttpStatus.OK);
    }

    public ResponseEntity<String> addProgress(Progress progress) {
        if (findProgressWithId(progress.getId()) != null) {
            return new ResponseEntity<>("Прогресс с таким id уже существует!", HttpStatus.BAD_REQUEST);
        }
        Player player = findPlayerWithId(progress.getPlayerId());
        if (player == null) {
            return new ResponseEntity<>("Игрока с таким id не существует!", HttpStatus.BAD_REQUEST);
        }
        player.getProgresses().add(progress);
        saveMethod.accept(players);
        return new ResponseEntity<>("Прогресс добавлен", HttpStatus.OK);
    }

    public ResponseEntity<String> addCurrency(Currency currency, long playerId) {
        if (findCurrencyWithId(currency.getId()) != null) {
            return new ResponseEntity<>("Валюта с таким id уже существует!", HttpStatus.BAD_REQUEST);
        }
        Player player = findPlayerWithId(playerId);
        if (player == null) {
            return new ResponseEntity<>("Игрока с таким id не существует!", HttpStatus.BAD_REQUEST);
        }
        player.getCurrencies().put(currency.getId(), currency);
        saveMethod.accept(players);
        return new ResponseEntity<>("Валюта добавлена", HttpStatus.OK);
    }

    public ResponseEntity<String> addItem(Item item, long playerId) {
        if (findItemWithId(item.getId()) != null) {
            return new ResponseEntity<>("Предмет с таким id уже существует!", HttpStatus.BAD_REQUEST);
        }
        Player player = findPlayerWithId(playerId);
        if (player == null) {
            return new ResponseEntity<>("Игрока с таким id не существует!", HttpStatus.BAD_REQUEST);
        }
        player.getItems().put(item.getId(), item);
        saveMethod.accept(players);
        return new ResponseEntity<>("Предмет добавлен", HttpStatus.OK);
    }

    // update methods
    public ResponseEntity<String> updatePlayer(Player player, long playerId) {
        Player foundPlayer = findPlayerWithId(playerId);
        if (foundPlayer == null) {
            return new ResponseEntity<>("Игрок с данным id не найден", HttpStatus.BAD_REQUEST);
        }
        if (player.getNickname() != null) foundPlayer.setNickname(player.getNickname());
        saveMethod.accept(players);
        return new ResponseEntity<>("Игрок изменён", HttpStatus.OK);
    }

    public ResponseEntity<String> updateProgress(Progress progress, long progressId) {
        Progress foundProgress = findProgressWithId(progressId);
        if (foundProgress == null) {
            return new ResponseEntity<>("Прогресс с данным id не найден", HttpStatus.BAD_REQUEST);
        }
        if (progress.getResourceId() > 0) foundProgress.setResourceId(progress.getResourceId());
        if (progress.getScore() > 0) foundProgress.setScore(progress.getScore());
        if (progress.getMaxScore() > 0) foundProgress.setMaxScore(progress.getMaxScore());
        saveMethod.accept(players);
        return new ResponseEntity<>("Прогресс изменён", HttpStatus.OK);
    }

    public ResponseEntity<String> updateCurrency(Currency currency, long currencyId) {
        Currency foundCurrency = findCurrencyWithId(currencyId);
        if (foundCurrency == null) {
            return new ResponseEntity<>("Валюта с данным id не найдена", HttpStatus.BAD_REQUEST);
        }
        if (currency.getResourceId() > 0) foundCurrency.setResourceId(currency.getResourceId());
        if (currency.getName() != null) foundCurrency.setName(currency.getName());
        if (currency.getCount() > 0) foundCurrency.setCount(currency.getCount());
        saveMethod.accept(players);
        return new ResponseEntity<>("Валюта изменена", HttpStatus.OK);
    }

    public ResponseEntity<String> updateItem(Item item, long itemId) {
        Item foundItem = findItemWithId(itemId);
        if (foundItem == null) {
            return new ResponseEntity<>("Предмет с данным id не найден", HttpStatus.BAD_REQUEST);
        }
        if (item.getResourceId() > 0) foundItem.setResourceId(item.getResourceId());
        if (item.getCount() > 0) foundItem.setCount(item.getCount());
        if (item.getLevel() > 0) foundItem.setLevel(item.getLevel());
        saveMethod.accept(players);
        return new ResponseEntity<>("Предмет изменён", HttpStatus.OK);
    }

    // delete methods
    public ResponseEntity<String> deletePlayer(long playerId) {
        Player foundPlayer = findPlayerWithId(playerId);
        if (foundPlayer == null) {
            return new ResponseEntity<>("Игрок с данным id не найден", HttpStatus.BAD_REQUEST);
        }
        players.remove(foundPlayer);
        saveMethod.accept(players);
        return new ResponseEntity<>("Игрок удалён", HttpStatus.OK);
    }

    public ResponseEntity<String> deleteProgress(long progressId) {
        for (Player player : players) {
            if (player.getProgresses().removeIf(progress -> progress.getId() == progressId)) {
                saveMethod.accept(players);
                return new ResponseEntity<>("Прогресс успешно удалён", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Прогресс с таким id не найден", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> deleteCurrency(long currencyId) {
        for (Player player : players) {
            for (Map.Entry<Long, Currency> entry : player.getCurrencies().entrySet()) {
                if (entry.getValue().getId() == currencyId) {
                    player.getCurrencies().remove(currencyId);
                    saveMethod.accept(players);
                    return new ResponseEntity<>("Валюта успешно удалена", HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("Валюта с таким id не найдена", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> deleteItem(long itemId) {
        for (Player player : players) {
            for (Map.Entry<Long, Item> entry : player.getItems().entrySet()) {
                if (entry.getValue().getId() == itemId) {
                    player.getItems().remove(itemId);
                    saveMethod.accept(players);
                    return new ResponseEntity<>("Предмет успешно удалён", HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("Предмет с таким id не найден", HttpStatus.BAD_REQUEST);
    }
}
