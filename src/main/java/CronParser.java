import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class CronParser {

  private static final int MIN_MINUTE = 0;
  private static final int MIN_HOUR = 0;
  private static final int MIN_DAY_OF_MONTH = 1;
  private static final int MIN_MONTH = 1;
  private static final int MIN_DAY_OF_WEEK = 0;
  private static final int MAX_MINUTE = 59;
  private static final int MAX_HOUR = 23;
  private static final int MAX_DAY_OF_MONTH = 31;
  private static final int MAX_MONTH = 12;
  private static final int MAX_DAY_OF_WEEK = 6;

  private static Map<String, Integer> MONTH_MAP = Map.ofEntries(entry("JAN", 1), entry("FEB", 2), entry("MAR", 3), entry("APR", 4), entry("MAY", 5),
      entry("JUN", 6), entry("JUL", 7), entry("AUG", 8), entry("SEP", 9), entry("OCT", 10), entry("NOV", 11), entry("DEC", 12));

  private static Map<String, Integer> DAY_MAP = Map.of("SUN", 0, "MON", 1, "TUE", 2, "WED", 3, "THU", 4, "FRI", 5, "SAT", 6);


  public static void main(String[] args) {
    var cronValues = getCronValues(args[0]);
    printFormattedLine("minute", cronValues[0]);
    printFormattedLine("hour", cronValues[1]);
    printFormattedLine("day of month", cronValues[2]);
    printFormattedLine("month", cronValues[3]);
    printFormattedLine("day of week", cronValues[4]);
    printFormattedLine("command", cronValues[5]);
  }

  private static String[] getCronValues(String cronCommand) {
    var returnValues = new String[6];
    var cronElements = cronCommand.split(" ");
    returnValues[0] = parseCronElement(cronElements[0], MIN_MINUTE, MAX_MINUTE);
    returnValues[1] = parseCronElement(cronElements[1], MIN_HOUR, MAX_HOUR);
    returnValues[2] = parseCronElement(cronElements[2], MIN_DAY_OF_MONTH, MAX_DAY_OF_MONTH);
    returnValues[3] = parseMonths(cronElements[3]);
    returnValues[4] = parseDays(cronElements[4]);
    returnValues[5] = cronElements[5];
    return returnValues;
  }

  private static String parseMonths(String cronElement) {
    var wordsReplacedWithInts = cronElement;
    for(var entry : MONTH_MAP.entrySet()) {
      wordsReplacedWithInts = wordsReplacedWithInts.replaceAll(entry.getKey(), entry.getValue().toString());
    }
    return parseCronElement(wordsReplacedWithInts, MIN_MONTH, MAX_MONTH);
  }

  private static String parseDays(String cronElement) {
    var wordsReplacedWithInts = cronElement;
    for(var entry : DAY_MAP.entrySet()) {
      wordsReplacedWithInts = wordsReplacedWithInts.replaceAll(entry.getKey(), entry.getValue().toString());
    }
    return parseCronElement(wordsReplacedWithInts, MIN_DAY_OF_WEEK, MAX_DAY_OF_WEEK);
  }

  private static String parseCronElement(String cronElement, int minValue, int maxValue) {
    if(cronElement.equals("*")) {
      return getPossibleValues(minValue, maxValue);
    } else if(cronElement.contains(",")) {
      return getValueListString(cronElement);
    } else if(cronElement.contains("-")) {
      return getRangeString(cronElement);
    } else if(cronElement.contains("/")){
      return getIntervalString(cronElement, minValue, maxValue);
    } else {
      return cronElement;
    }
  }

  private static String getIntervalString(String cronElement, int minValue, int maxValue) {
    var intervalConfig = cronElement.split("/");
    var startValue = intervalConfig[0].equals("*") ? minValue : Integer.parseInt(intervalConfig[0]);
    var builder = new StringBuilder();
    for(int i = startValue; i <= maxValue; i = i+Integer.parseInt(intervalConfig[1])) {
      builder.append(i).append(" ");
    }
    return builder.deleteCharAt(builder.length()-1).toString();
  }

  private static String getRangeString(String cronElement) {
    var range = cronElement.split("-");
    return getPossibleValues(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
  }

  private static String getValueListString(String cronElement) {
    var values = cronElement.split(",");
    var list = new ArrayList<Integer>();
    for(var intString : values) {
      list.add(Integer.parseInt(intString));
    }
    return list.stream()
        .sorted()
        .distinct()
        .map(Object::toString)
        .collect(Collectors.joining(" "));
  }

  private static String getPossibleValues(int startValue, int endValue) {
    var builder = new StringBuilder();
    for(int i = startValue; i<= endValue; i++) {
      builder.append(i).append(" ");
    }
    return builder.deleteCharAt(builder.length()-1).toString();
  }

  private static void printFormattedLine(String key, String value) {
    System.out.printf("%-14s %s\n", key, value);
  }
}
