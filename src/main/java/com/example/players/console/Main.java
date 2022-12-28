package com.example.players.console;

import com.example.players.CrudService;
import com.example.players.db.Database;
import com.example.players.dto.Item;
import com.example.players.dto.Player;
import com.example.players.dto.Progress;
import com.example.players.dto.Currency;
import com.example.players.json.CashManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class Main {
    private Scanner scanner;
    private String jsonFileName;

    @Autowired
    private Database database;

    @Bean
    public void start() {
        scanner = new Scanner(System.in);
        new Thread() {
            @Override
            public void run() {
                super.run();
                label:
                while (true) {
                    System.out.println("Выберите действие:");
                    System.out.println("1) загрузить кэш в базу данных");
                    System.out.println("2) выгрузить кэш из базы данных");
                    System.out.println("3) провести CRUD операции");
                    System.out.println("4) выйти");
                    switch ((int) requestNumber(i -> i > 0 && i < 5)) {
                        case 1 -> {
                            System.out.println("Введите название json-файла (например, cash.json):");
                            String path = scanner.nextLine();
                            List<Player> players = CashManager.read(path);
                            if (players == null) {
                                continue;
                            }
                            System.out.println("Сколько игроков загрузить (1-" + players.size() + ")?");
                            database.clearTables();
                            database.insertPlayers(players.subList(0, (int) requestNumber(i -> i > 0 && i <= players.size())));
                            System.out.println("Кэш загружен в базу данных из cash/" + path);
                        }
                        case 2 -> {
                            requestJsonFileName();
                            CashManager.write(jsonFileName, database.select());
                            System.out.println("Кэш выгружен из базы данных в cash/" + jsonFileName);
                        }
                        case 3 -> {
                            System.out.println("Провести CRUD операции в базе данных (1) или в кэше (2)?");
                            new ConsoleCrudService().run(requestNumber(i -> i == 1 || i == 2) == 1);
                        }
                        case 4 -> {
                            break label;
                        }
                    }
                    System.out.println();
                }
                try {
                    this.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    private class ConsoleCrudService extends CrudService {
        private boolean forDB;
        private boolean toSave;

        private void run(boolean _forDB) {
            forDB = _forDB;
            if (forDB) {
                players = database.select();
            } else {
                requestJsonFileName();
                players = CashManager.read(jsonFileName);
            }
            if (players != null) {
                toSave = false;
                while (true) {
                    System.out.println();
                    System.out.println("Выберите действие:");
                    System.out.println("1) прочитать");
                    System.out.println("2) добавить");
                    System.out.println("3) изменить");
                    System.out.println("4) удалить");
                    System.out.println("5) сохранить");
                    System.out.println("6) закончить редактирование");
                    switch ((int) requestNumber(i -> i > 0 && i < 7)) {
                        case 1 -> read();
                        case 2 -> add();
                        case 3 -> change();
                        case 4 -> delete();
                        case 5 -> save();
                        case 6 -> {
                            if (toSave) {
                                System.out.println("Вы совершили некоторые изменения. Хотите их сохранить? (1 - да, 2 - нет)");
                                if (requestNumber(i -> i == 1 || i == 2) == 1) save();
                            }
                            return;
                        }
                    }
                }
            }
        }

        private void read() {
            int from = 0;
            int to = 10;
            int curPage = 1;
            int maxPage = players.size() / 10 + (players.size() % 10 == 0 ? 0 : 1);
            outputPlayersSlice(players, from, to, curPage, maxPage);
            label:
            while (true) {
                System.out.println();
                System.out.println("+: следующая страница");
                System.out.println("-: предыдущая страница");
                System.out.println("<id>: подробнее об игроке");
                System.out.println("exit: закончить просмотр");
                System.out.println();
                String input = scanner.nextLine();
                switch (input) {
                    case "+" -> {
                        if (curPage == maxPage) {
                            System.out.println("Нет страниц для показа");
                        } else {
                            from += 10;
                            to = Math.min(players.size(), from + 10);
                            curPage += 1;
                            outputPlayersSlice(players, from, to, curPage, maxPage);
                        }
                    }
                    case "-" -> {
                        if (curPage == 1) {
                            System.out.println("Нет страниц для показа");
                        } else {
                            from -= 10;
                            to = from + 10;
                            curPage -= 1;
                            outputPlayersSlice(players, from, to, curPage, maxPage);
                        }
                    }
                    case "exit" -> {
                        break label;
                    }
                    default -> {
                        try {
                            long id = Long.parseLong(input);
                            Player player = players.stream().filter(p -> p.getPlayerId() == id).findFirst().get();
                            System.out.print("Progresses: ");
                            outputArray(player.getProgresses(), Progress::toString);
                            System.out.print("Currencies: ");
                            outputArray(player.getCurrencies().values(), Currency::toString);
                            System.out.print("Items: ");
                            outputArray(player.getItems().values(), Item::toString);
                        } catch (NumberFormatException | NoSuchElementException e) {
                            System.out.println("Игрок с таким id не найден");
                        }
                    }
                }
            }
        }

        private void add() {
            System.out.println("Что вы хотите добавить?");
            System.out.println("1) игрока");
            System.out.println("2) прогресс");
            System.out.println("3) валюту");
            System.out.println("4) предмет");
            switch ((int) requestNumber(i -> i > 0 && i < 5)) {
                case 1 -> {
                    System.out.println("Введите id игрока:");
                    long id = requestNumber(i -> true);
                    if (findPlayerWithId(id) != null) {
                        System.out.println("Игрок с таким id уже существует!");
                    } else {
                        System.out.println("Введите имя игрока:");
                        String name = scanner.nextLine();
                        players.add(new Player(id, name, new ArrayList<>(), new HashMap<>(), new HashMap<>()));
                        toSave = true;
                        System.out.println("Игрок добавлен!");
                    }
                }
                case 2 -> {
                    System.out.println("Введите id прогресса:");
                    long id = requestNumber(i -> true);
                    if (findProgressWithId(id) != null) {
                        System.out.println("Прогресс с таким id уже существует!");
                    } else {
                        System.out.println("Введите id игрока:");
                        long playerId = requestNumber(i -> true);
                        Player player = findPlayerWithId(playerId);
                        if (player == null) {
                            System.out.println("Игрока с таким id не существует!");
                        } else {
                            System.out.println("Введите id ресурса:");
                            long resourceId = requestNumber(i -> true);
                            System.out.println("Введите счёт:");
                            int score = (int) requestNumber(i -> true);
                            System.out.println("Введите максимальный счёт:");
                            int maxScore = (int) requestNumber(i -> true);
                            player.getProgresses().add(new Progress(id, playerId, resourceId, score, maxScore));
                            toSave = true;
                            System.out.println("Прогресс добавлен!");
                        }
                    }
                }
                case 3 -> {
                    System.out.println("Введите id валюты:");
                    long id = requestNumber(i -> true);
                    if (findCurrencyWithId(id) != null) {
                        System.out.println("Валюта с таким id уже существует!");
                    } else {
                        System.out.println("Введите id игрока:");
                        long playerId = requestNumber(i -> true);
                        Player player = findPlayerWithId(playerId);
                        if (player == null) {
                            System.out.println("Игрока с таким id не существует!");
                        } else {
                            System.out.println("Введите id ресурса:");
                            long resourceId = requestNumber(i -> true);
                            System.out.println("Введите название:");
                            String name = scanner.nextLine();
                            System.out.println("Введите количество:");
                            int count = (int) requestNumber(i -> true);
                            player.getCurrencies().put(id, new Currency(id, resourceId, name, count));
                            toSave = true;
                            System.out.println("Валюта добавлена!");
                        }
                    }
                }
                case 4 -> {
                    System.out.println("Введите id предмета:");
                    long id = requestNumber(i -> true);
                    if (findItemWithId(id) != null) {
                        System.out.println("Предмет с таким id уже существует!");
                    } else {
                        System.out.println("Введите id игрока:");
                        long playerId = requestNumber(i -> true);
                        Player player = findPlayerWithId(playerId);
                        if (player == null) {
                            System.out.println("Игрока с таким id не существует!");
                        } else {
                            System.out.println("Введите id ресурса:");
                            long resourceId = requestNumber(i -> true);
                            System.out.println("Введите количество:");
                            int count = (int) requestNumber(i -> true);
                            System.out.println("Введите уровень:");
                            int level = (int) requestNumber(i -> true);
                            player.getItems().put(id, new Item(id, resourceId, count, level));
                            toSave = true;
                            System.out.println("Предмет добавлен!");
                        }
                    }
                }
            }
        }

        private void change() {
            System.out.println("Что вы хотите изменить?");
            System.out.println("1) имя игрока");
            System.out.println("2) прогресс");
            System.out.println("3) валюту");
            System.out.println("4) предмет");
            switch ((int) requestNumber(i -> i > 0 && i < 5)) {
                case 1 -> {
                    System.out.println("Введите id игрока:");
                    long id = requestNumber(i -> true);
                    System.out.println("Введите новое имя игрока:");
                    String newName = scanner.nextLine();
                    Player player = findPlayerWithId(id);
                    if (player == null) {
                        System.out.println("Игрок с данным id не найден");
                    } else {
                        player.setNickname(newName);
                        System.out.println("Имя игрока изменено!");
                        toSave = true;
                    }
                }
                case 2 -> {
                    System.out.println("Введите id прогресса:");
                    long id = requestNumber(i -> true);
                    System.out.println("Что вы хотите изменить?");
                    System.out.println("1) id ресурса");
                    System.out.println("2) счёт");
                    System.out.println("3) максимальный счёт");
                    long inputNumber = requestNumber(i -> i > 0 && i < 4);
                    System.out.println("Введите новое значение:");
                    long inputData = requestNumber(i -> true);
                    Progress progress = findProgressWithId(id);
                    if (progress == null) {
                        System.out.println("Прогресс с данным id не найден");
                    } else {
                        switch ((int) inputNumber) {
                            case 1 -> progress.setResourceId(inputData);
                            case 2 -> progress.setScore((int) inputData);
                            case 3 -> progress.setMaxScore((int) inputData);
                        }
                        System.out.println("Прогресс изменён!");
                        toSave = true;
                    }
                }
                case 3 -> {
                    System.out.println("Введите id валюты:");
                    long id = requestNumber(i -> true);
                    System.out.println("Что вы хотите изменить?");
                    System.out.println("1) id ресурса");
                    System.out.println("2) название");
                    System.out.println("3) количество");
                    long inputNumber = requestNumber(i -> i > 0 && i < 4);
                    System.out.println("Введите новое значение:");
                    String inputData = scanner.nextLine();
                    Currency currency = findCurrencyWithId(id);
                    if (currency == null) {
                        System.out.println("Валюта с данным id не найдена");
                    } else {
                        try {
                            switch ((int) inputNumber) {
                                case 1 -> currency.setResourceId(Long.parseLong(inputData));
                                case 2 -> currency.setName(inputData);
                                case 3 -> currency.setCount(Integer.parseInt(inputData));
                            }
                            System.out.println("Валюта изменена!");
                            toSave = true;
                        } catch (NumberFormatException e) {
                            System.out.println("Неверный формат данных");
                        }
                    }
                }
                case 4 -> {
                    System.out.println("Введите id предмета:");
                    long id = requestNumber(i -> true);
                    System.out.println("Что вы хотите изменить?");
                    System.out.println("1) id ресурса");
                    System.out.println("2) количество");
                    System.out.println("3) уровень");
                    long inputNumber = requestNumber(i -> i > 0 && i < 4);
                    System.out.println("Введите новое значение:");
                    long inputData = requestNumber(i -> true);
                    Item item = findItemWithId(id);
                    if (item == null) {
                        System.out.println("Предмет с данным id не найден");
                    } else {
                        switch ((int) inputNumber) {
                            case 1 -> item.setResourceId(inputData);
                            case 2 -> item.setCount((int) inputData);
                            case 3 -> item.setLevel((int) inputData);
                        }
                        System.out.println("Предмет изменён!");
                        toSave = true;
                    }
                }
            }
        }

        private void delete() {
            System.out.println("Что вы хотите удалить?");
            System.out.println("1) игрока");
            System.out.println("2) прогресс");
            System.out.println("3) валюту");
            System.out.println("4) предмет");
            int inputNumber = (int) requestNumber(i -> i > 0 && i < 5);
            System.out.println("Введите id:");
            long id = requestNumber(i -> true);
            boolean success = false;
            switch (inputNumber) {
                case 1 -> {
                    try {
                        players.remove(findPlayerWithId(id));
                        success = true;
                        System.out.println("Игрок успешно удалён!");
                    } catch (NullPointerException e) {
                        System.out.println("Игрок с таким id не найден!");
                    }
                }
                case 2 -> {
                    for (Player player : players) {
                        if (player.getProgresses().removeIf(progress -> progress.getId() == id)) {
                            success = true;
                            break;
                        }
                    }
                    if (success) System.out.println("Прогресс успешно удалён!");
                    else System.out.println("Прогресс с таким id не найден!");
                }
                case 3 -> {
                    label:
                    for (Player player : players) {
                        for (Map.Entry<Long, Currency> entry : player.getCurrencies().entrySet()) {
                            if (entry.getValue().getId() == id) {
                                player.getCurrencies().remove(id);
                                success = true;
                                break label;
                            }
                        }
                    }
                    if (success) System.out.println("Валюта успешно удалена!");
                    else System.out.println("Валюта с таким id не найдена!");
                }
                case 4 -> {
                    label:
                    for (Player player : players) {
                        for (Map.Entry<Long, Item> entry : player.getItems().entrySet()) {
                            if (entry.getValue().getId() == id) {
                                player.getItems().remove(id);
                                success = true;
                                break label;
                            }
                        }
                    }
                    if (success) System.out.println("Предмет успешно удалён!");
                    else System.out.println("Предмет с таким id не найден!");
                }
            }
            if (success) toSave = true;
        }

        private void save() {
            if (forDB) {
                database.clearTables();
                database.insertPlayers(players);
            } else {
                CashManager.write(jsonFileName, players);
            }
            System.out.println("Изменения сохранены!");
            toSave = false;
        }
    }

    private void outputPlayersSlice(List<Player> players, int from, int to, int curPage, int maxPage) {
        System.out.println("Страница: " + curPage + "/" + maxPage);
        for (Player player : players.subList(from, to)) {
            System.out.println("ID " + player.getPlayerId() + " - " + player.getNickname());
        }
    }

    private void requestJsonFileName() {
        System.out.println("Введите название json-файла (например, cash.json):");
        jsonFileName = scanner.nextLine();
    }

    private long requestNumber(Predicate<Long> predicate) {
        long inputNumber = 0;
        boolean first = true;
        while (!predicate.test(inputNumber) || first) {
            try {
                inputNumber = Long.parseLong(scanner.nextLine());
                first = false;
            } catch (NumberFormatException ignored) {}
        }
        return inputNumber;
    }

    private <T> void outputArray(Collection<T> list, Function<T, String> mapFunc) {
        String[] array = list.stream().map(mapFunc).toArray(String[]::new);
        if (array.length == 0) System.out.println("-");
        else System.out.println(String.join(", ", array));
    }
}
