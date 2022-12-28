package com.example.players.web;

import com.example.players.db.Database;
import com.example.players.dto.Currency;
import com.example.players.dto.Item;
import com.example.players.dto.Player;
import com.example.players.dto.Progress;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class WebController {
    @Autowired
    Database database;

    @GetMapping
    public List<Player> read(@RequestParam(name="path", required = false) String path) {
        return getCrudService(path).read();
    }

    @PostMapping("player")
    public ResponseEntity<String> addPlayer(@RequestParam(required = false) String path,
                                            @Valid @RequestBody Player player) {
        return getCrudService(path).addPlayer(player);
    }

    @PostMapping("progress")
    public ResponseEntity<String> addProgress(@RequestParam(required = false) String path,
                                              @Valid @RequestBody Progress progress) {
        return getCrudService(path).addProgress(progress);
    }

    @PostMapping("currency")
    public ResponseEntity<String> addCurrency(@RequestParam(required = false) String path,
                                              @RequestParam long playerId,
                                              @Valid @RequestBody Currency currency) {
        return getCrudService(path).addCurrency(currency, playerId);
    }

    @PostMapping("item")
    public ResponseEntity<String> addItem(@RequestParam(required = false) String path,
                                          @RequestParam long playerId,
                                          @Valid @RequestBody Item item) {
        return getCrudService(path).addItem(item, playerId);
    }

    @PatchMapping("player")
    public ResponseEntity<String> updatePlayer(@RequestParam(required = false) String path,
                                               @RequestParam long playerId,
                                               @RequestBody Player player) {
        return getCrudService(path).updatePlayer(player, playerId);
    }

    @PatchMapping("progress")
    public ResponseEntity<String> updateProgress(@RequestParam(required = false) String path,
                                                 @RequestParam long progressId,
                                                 @RequestBody Progress progress) {
        return getCrudService(path).updateProgress(progress, progressId);
    }

    @PatchMapping("currency")
    public ResponseEntity<String> updateCurrency(@RequestParam(required = false) String path,
                                                 @RequestParam long currencyId,
                                                 @RequestBody Currency currency) {
        return getCrudService(path).updateCurrency(currency, currencyId);
    }

    @PatchMapping("item")
    public ResponseEntity<String> updateItem(@RequestParam(required = false) String path,
                                             @RequestParam long itemId,
                                             @RequestBody Item item) {
        return getCrudService(path).updateItem(item, itemId);
    }

    @DeleteMapping("player")
    public ResponseEntity<String> deletePlayer(@RequestParam(required = false) String path,
                                               @RequestParam long playerId) {
        return getCrudService(path).deletePlayer(playerId);
    }

    @DeleteMapping("progress")
    public ResponseEntity<String> deleteProgress(@RequestParam(required = false) String path,
                                                 @RequestParam long progressId) {
        return getCrudService(path).deleteProgress(progressId);
    }

    @DeleteMapping("currency")
    public ResponseEntity<String> deleteCurrency(@RequestParam(required = false) String path,
                                                 @RequestParam long currencyId) {
        return getCrudService(path).deleteCurrency(currencyId);
    }

    @DeleteMapping("item")
    public ResponseEntity<String> deleteItem(@RequestParam(required = false) String path,
                                             @RequestParam long itemId) {
        return getCrudService(path).deleteItem(itemId);
    }

    private WebCrudService getCrudService(String path) {
        if (path != null)
            return new WebCrudService(path);
        return new WebCrudService(database);
    }
}
