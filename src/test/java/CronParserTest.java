import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Test;

public class CronParserTest {

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private static final String COMMAND = "/usr/bin/find";

  @Before
  public void setup() {

    System.setOut(new PrintStream(outputStream));
  }

  @Test
  public void simpleCase() {
    whenCronParserIsCalledWith("15 1 2 6 4 "+COMMAND);
    thenTheCronFieldsAre("15", "1", "2", "6", "4", COMMAND);
  }

  @Test
  public void everyMinute() {
    whenCronParserIsCalledWith("* 1 2 3 4 "+COMMAND);
    thenTheCronFieldsAre("0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59",
        "1", "2", "3", "4", COMMAND);
  }

  @Test
  public void everyHour() {
    whenCronParserIsCalledWith("1 * 2 3 4 "+COMMAND);
    thenTheCronFieldsAre("1" , "0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23", "2", "3", "4", COMMAND);
  }

  @Test
  public void everyDayOfMonth() {
    whenCronParserIsCalledWith("1 2 * 3 4 "+COMMAND);
    thenTheCronFieldsAre("1", "2", "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31",  "3", "4", COMMAND);
  }

  @Test
  public void everyMonth() {
    whenCronParserIsCalledWith("1 2 3 * 4 "+COMMAND);
    thenTheCronFieldsAre("1", "2", "3", "1 2 3 4 5 6 7 8 9 10 11 12", "4", COMMAND);
  }

  @Test
  public void everyDayOfWeek() {
    whenCronParserIsCalledWith("1 2 3 4 * "+COMMAND);
    thenTheCronFieldsAre("1", "2", "3", "4", "0 1 2 3 4 5 6",  COMMAND);
  }

  @Test
  public void lists() {
    whenCronParserIsCalledWith("1,4,3,4,5 13,12,15 18,1,4,30 2,3,8,9 5,6 "+COMMAND);
    thenTheCronFieldsAre("1 3 4 5", "12 13 15", "1 4 18 30", "2 3 8 9", "5 6", COMMAND);
  }

  @Test
  public void ranges() {
    whenCronParserIsCalledWith("1-10 4-5 7-8 7-12 0-4 "+COMMAND);
    thenTheCronFieldsAre("1 2 3 4 5 6 7 8 9 10", "4 5", "7 8", "7 8 9 10 11 12", "0 1 2 3 4", COMMAND);
  }

  @Test
  public void wildCardIntervals() {
    whenCronParserIsCalledWith("*/10 */3 */5 */4 */2 "+COMMAND);
    thenTheCronFieldsAre("0 10 20 30 40 50", "0 3 6 9 12 15 18 21", "1 6 11 16 21 26 31", "1 5 9", "0 2 4 6", COMMAND);
  }

  @Test
  public void startDefinedIntervals() {
    whenCronParserIsCalledWith("5/10 15/3 12/5 3/4 4/2 "+COMMAND);
    thenTheCronFieldsAre("5 15 25 35 45 55", "15 18 21", "12 17 22 27", "3 7 11", "4 6", COMMAND);
  }

  @Test
  public void monthsByWords() {
    whenCronParserIsCalledWith("1 2 3 JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC 4 "+COMMAND);
    thenTheCronFieldsAre("1", "2", "3", "1 2 3 4 5 6 7 8 9 10 11 12", "4", COMMAND);
  }

  @Test
  public void monthWordsRange() {
    whenCronParserIsCalledWith("1 2 3 JAN-APR 4 "+COMMAND);
    thenTheCronFieldsAre("1", "2", "3", "1 2 3 4", "4", COMMAND);
  }

  @Test
  public void monthWordsInterval() {
    whenCronParserIsCalledWith("1 2 3 MAR/4 4 "+COMMAND);
    thenTheCronFieldsAre("1", "2", "3", "3 7 11", "4", COMMAND);
  }

  @Test
  public void daysByWords() {
    whenCronParserIsCalledWith("1 2 3 4 SUN,MON,TUE,WED,THU,FRI,SAT "+COMMAND);
    thenTheCronFieldsAre("1", "2", "3", "4", "0 1 2 3 4 5 6", COMMAND);
  }

  @Test
  public void dayWordsRange() {
    whenCronParserIsCalledWith("1 2 3 4 WED-SAT "+COMMAND);
    thenTheCronFieldsAre("1", "2", "3", "4", "3 4 5 6", COMMAND);
  }

  @Test
  public void dayWordsInterval() {
    whenCronParserIsCalledWith("1 2 3 4 TUE/3 "+COMMAND);
    thenTheCronFieldsAre("1", "2", "3", "4", "2 5", COMMAND);
  }

  private void whenCronParserIsCalledWith(String expression) {
    var args = new String[]{expression};
    CronParser.main(args);
  }

  private void thenTheCronFieldsAre(String minute, String hour, String dayOfMonth, String month, String dayOfWeek, String command) {
    var expected = String.format("minute         %s\nhour           %s\nday of month   %s\nmonth          %s\nday of week    %s\ncommand        %s\n"
      ,minute, hour, dayOfMonth, month, dayOfWeek, command);
    var output = outputStream.toString().replaceAll("\r", "");
    assertThat(output, is(expected));
  }
}
