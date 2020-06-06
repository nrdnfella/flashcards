package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ArrayList<String> consoleLog = new ArrayList<>();

    private static void consolePrint(String text) {
        System.out.println(text);
        consoleLog.add(text);
    }

    public static void main(String[] args) {
        LinkedHashMap<String, String> cards = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> statistics = new LinkedHashMap<>();

        HashMap<String, String> arguments = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (i % 2 == 0) {
                arguments.put(args[i], "");
            } else {
                arguments.put(args[i - 1], args[i]);
            }
        }

        if (arguments.containsKey("-import") && !arguments.get("-import").isEmpty()) {
            importCards(cards, statistics, arguments.get("-import"));
        }

        String command = "";
        while (!"exit".equals(command)) {
            consolePrint("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            command = scanner.nextLine();
            switch (command) {
                case "add":
                    consoleLog.add("add");
                    add(cards);
                    break;
                case "remove":
                    consoleLog.add("remove");
                    remove(cards, statistics);
                    break;
                case "import":
                    consoleLog.add("import");
                    importCards(cards, statistics, "");
                    break;
                case "export":
                    consoleLog.add("export");
                    exportCards(cards, statistics, "");
                    break;
                case "ask":
                    consoleLog.add("ask");
                    ask(cards, statistics);
                    break;
                case "exit":
                    consoleLog.add("exit");
                    consolePrint("exit");
                    if (arguments.containsKey("-export") && !arguments.get("-export").isEmpty()) {
                        exportCards(cards, statistics, arguments.get("-export"));
                    }
                    consoleLog.clear();
                    break;
                case "log":
                    consoleLog.add("log");
                    log();
                    break;
                case "hardest card":
                    consoleLog.add("hardest card");
                    hardsetCard(statistics);
                    break;
                case "reset stats":
                    consoleLog.add("reset stats");
                    resetStats(statistics);
                    break;
                default:
                    break;
            }
        }
    }

    public static void add(LinkedHashMap<String, String> cards) {
        consolePrint("The card");
        String question;
        while (true) {
            question = scanner.nextLine();
            consoleLog.add(question);
            if (question.isEmpty()) {
                continue;
            } else if (cards.containsKey(question)) {
                consolePrint("The card \"" + question + "\" already exists.");
                return;
            } else {
                break;
            }
        }
        consolePrint("The definition of the card:");
        String answer;
        while (true) {
            answer = scanner.nextLine();
            consoleLog.add(answer);
            if (answer.isEmpty()) {
                consolePrint("The definition of the card:");
            } else if (cards.containsValue(answer)) {
                consolePrint("The definition \"" + answer + "\" already exists.");
                return;
            } else {
                break;
            }
        }
        cards.put(question, answer);
        consolePrint("The pair (\"" + question + "\":\"" + question + "\") has been added.");
    }

    public static void remove(LinkedHashMap<String, String> cards,
                              LinkedHashMap<String, Integer> statistics) {
        consolePrint("The card");
        String cardName = scanner.nextLine();
        consoleLog.add(cardName);
        if (cards.containsKey(cardName)) {
            cards.remove(cardName);
            statistics.remove(cardName);
            consolePrint("The card has been removed.");
        } else {
            consolePrint("Can't remove \"" + cardName + "\": there is no such card.");
        }
    }

    public static void ask(LinkedHashMap<String, String> cards,
                           LinkedHashMap<String, Integer> statistics) {
        consolePrint("How many times to ask?");
        int times = Integer.parseInt(scanner.nextLine());
        consoleLog.add(String.valueOf(times));
        List<String> list = new ArrayList<>(cards.keySet());
        Random random = new Random();
        int size = cards.size();
        for (int i = 0; i < times; i++) {
            int index = random.nextInt(size);
            String card = list.get(index);
            String cardDefinition = cards.get(card);
            consolePrint("Print the definition of \"" + card + "\":");
            String answer = scanner.nextLine();
            consoleLog.add(answer);
            if (!answer.equals(cardDefinition)) {
                if (!cards.containsValue(answer)) {
                    statistics.put(card, statistics.getOrDefault(card, 0) + 1);
                    consolePrint("Wrong answer. The correct one is \"" + cardDefinition + "\":");
                } else {
                    cards.forEach((key, value) -> {
                        statistics.put(card, statistics.getOrDefault(card, 0) + 1);
                        if (value.equals(answer)) {
                            consolePrint("Wrong answer. The correct one is \"" + cardDefinition + "\"," +
                                    " you've just written the definition of \"" + key + "\".");
                        }
                    });
                }
            } else {
                consolePrint("Correct answer.");
            }
        }
    }

    public static void importCards(LinkedHashMap<String, String> cards,
                                   LinkedHashMap<String, Integer> statistics,
                                   String pathToFile) {
        if (pathToFile.isEmpty()) {
            consolePrint("File name:");
            pathToFile = scanner.nextLine();
        }
        consoleLog.add(pathToFile);
        File file = new File(pathToFile);
        int count = 0;
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNext()) {
                String[] line = fileScanner.nextLine().split(":");
                cards.put(line[0], line[1]);
                statistics.put(line[0], Integer.parseInt(line[2]));
                count++;
            }
        } catch (FileNotFoundException e) {
            consolePrint("not found.");
            return;
        }
        consolePrint(count + " cards have been loaded.");
    }

    public static void exportCards(LinkedHashMap<String, String> cards,
                                   LinkedHashMap<String, Integer> statistics,
                                   String pathToFile) {
        if (pathToFile.isEmpty()) {
            consolePrint("File name:");
            pathToFile = scanner.nextLine();
        }
        consoleLog.add(pathToFile);
        File file = new File(pathToFile);
        try (PrintWriter printWriter  = new PrintWriter (file)) {
            for (var card : cards.entrySet()) {
                printWriter.println(card.getKey() + ":" + card.getValue() + ":" + statistics.getOrDefault(card.getKey(), 0));
            }
        } catch (IOException e) {
            consolePrint("An exception occurs " + e.getMessage());
            return;
        }
        consolePrint(cards.size() + " cards have been saved.");
    }

    public static void log() {
        consolePrint("File name:");
        String pathToFile = scanner.nextLine();
        consoleLog.add(pathToFile);
        File file = new File(pathToFile);
        try (PrintWriter printWriter  = new PrintWriter (file)) {
            for (String log : consoleLog) {
                printWriter.println(log);
            }
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
            return;
        }
        consolePrint("The log has been saved.");
    }

    public static void hardsetCard(LinkedHashMap<String, Integer> statistics) {
        if (statistics.isEmpty()) {
            consolePrint("There are no cards with errors.");
            return;
        }
        int maxValue = statistics.values().stream().max(Integer::compare).get();
        StringBuilder sb = new StringBuilder();
        Map<String, Integer> result = statistics.entrySet().stream()
                .filter(map -> map.getValue() == maxValue)
                .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
        if (result.size() > 1) {
            sb.append("The hardest cards are ");
            int i = 0;
            for (var r : result.entrySet()) {
                sb.append("\"" + r.getKey() + "\"");
                if (i < result.size() - 1) {
                    sb.append(", ");
                } else {
                    sb.append(". ");
                }
                i++;
            }
        } else {
            sb.append("The hardest card is ");
            result.forEach((key, value) -> sb.append("\"" + key + "\". "));
        }

        sb.append(maxValue > 1 ? "You have " + maxValue + " errors answering it."
                : result.size() > 1 ? "You have 1 errors answering them." : "");

        consolePrint(sb.toString());
    }

    public static void resetStats(LinkedHashMap<String, Integer> statistics) {
        statistics.clear();
        consolePrint("Card statistics has been reset.");
    }
}
