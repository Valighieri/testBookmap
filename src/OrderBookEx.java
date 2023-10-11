import java.io.*;
import java.util.Iterator;
import java.util.TreeMap;


import static java.lang.Integer.parseInt;

public class OrderBookEx {
    static TreeMap<Integer, Integer> tableBid = new TreeMap<>();
    static TreeMap<Integer, Integer> tableAsk = new TreeMap<>();

    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))
        ) {
            String line;
            String output;
            while ((line = reader.readLine()) != null) {
                switch (line.charAt(0)) {
                    case 'u':
                        doUpdate(line);
                        break;
                    case 'q':
                        output = doQuery(line);
                        if (!output.equals("")) writer.write(output);
                        break;
                    case 'o':
                        doOrder(line);
                        break;
                }
            }
        }
    }

    static void doUpdate(String line) {
        int price = 0;
        int size = 0;
        String type;

        int counter = 1;
        int begin = 2;
        for (int i = 2; i < line.length(); i++) {
            if (line.charAt(i) == ',') {
                int temp = parseInt(line.substring(begin, i));
                begin = i + 1;
                if (counter == 1) price = temp;
                if (counter == 2) {
                    size = temp;
                    break;
                }
                counter++;
            }
        }
        type = line.substring(begin);

        if (type.equals("bid")) {
            if (size != 0) tableBid.put(price, size);
            else tableBid.remove(price);
        } else if (type.equals("ask")) {
            if (size != 0) tableAsk.put(price, size);
            else tableAsk.remove(price);
        }
    }

    static String doQuery(String line) throws IOException {
        String command;
        if (line.charAt(2) == 'b') {
            command = line.substring(2);
        } else if (line.charAt(2) == 's') {
            command = line.substring(2, 6);
        } else return "";

        int price;
        int size;
        switch (command) {
            case "best_bid":
                price = tableBid.lastKey();
                size = tableBid.get(price);
                return price + "," + size + '\n';
            case "best_ask":
                price = tableAsk.firstKey();
                size = tableAsk.get(price);
                return price + "," + size + '\n';
            case "size":
                size = findSize(parseInt(line.substring(7)));
                return String.valueOf(size) + '\n';
        }
        return "";
    }

    static void doOrder(String line) {
        String command;
        if (line.charAt(2) == 'b') {
            command = line.substring(2, 5);
        } else if (line.charAt(2) == 's') {
            command = line.substring(2, 6);
        } else return;

        switch (command) {
            case "buy":
                buy(parseInt(line.substring(6)));
                break;
            case "sell":
                sell(parseInt(line.substring(7)));
                break;
        }
    }

    static int findSize(int price) {
        int size = 0;
        if (tableBid.get(price) != null) size = tableBid.get(price);
        else if (tableAsk.get(price) != null) size = tableAsk.get(price);
        return size;
    }

    static void sell(int size) {
        Iterator<Integer> iterator = tableBid.descendingMap().keySet().iterator();
        int price;
        int tableBidSize;

        while (iterator.hasNext()) {
            price = iterator.next();
            tableBidSize = tableBid.get(price);
            if (tableBidSize > size) {
                tableBid.put(price, tableBidSize - size);
                break;
            } else if (tableBidSize == size) {
                iterator.remove();
                break;
            } else {
                size -= tableBidSize;
                iterator.remove();
            }
        }
    }

    static void buy(int size) { //asks table
        Iterator<Integer> iterator = tableAsk.keySet().iterator();
        int price;
        int tableAskSize;

        while (iterator.hasNext()) {
            price = iterator.next();
            tableAskSize = tableAsk.get(price);
            if (tableAskSize > size) {
                tableAsk.put(price, tableAskSize - size);
                break;
            } else if (tableAskSize == size) {
                iterator.remove();
                break;
            } else {
                size -= tableAskSize;
                iterator.remove();
            }
        }
    }
}