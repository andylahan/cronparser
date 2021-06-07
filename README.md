# cronparser

CronParser is an application which takes a cron command as it's only argument and outputs a summary of when the cron will run to the command line  
  
for example   
cronParser.sh "*/15 0 1,15 * 1-5 /usr/bin/find"  

will output  

minute 0 15 30 45  
hour 0  
day of month 1 15  
month 1 2 3 4 5 6 7 8 9 10 11 12  
day of week 1 2 3 4 5  
command /usr/bin/find  
  
CronParser will also accept MONTH and DAY OF WEEK values in three letter format, e.g. JAN,FEB,MON,TUE  
  
CronParser currently only accepts valid cron expressions and will fail or produce incorrect results if an incorrect expression is supplied.  
  
the following instructions are for building and running cronParser in a Linux environment  
you will need git and Java 11 or later installed to run cronParser  
you can do that manually or use the yum commands below  
  
yum install git  
#you need java 11, so see which packages are available  
yum search jdk  
yum install {chosen java package}  
  
git clone https://github.com/andylahan/cronparser.git  
cd cronparser  
./gradlew build  
./cronParser.sh "{your cron expression}"  