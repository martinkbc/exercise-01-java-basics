package org.example;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        final String INPUT_PATH = "src/main/resources/measurements.txt";
        final String OUTPUT_PATH = "src/main/resources/output.txt";

        FileWriter myWriter = new FileWriter(OUTPUT_PATH);
        String output;

        // #1 Read and store the data of file measurements.txt.
        FileInputStream fstream = new FileInputStream(INPUT_PATH);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        List<SpeedControlRecord> speedControlRecords = new ArrayList<>();

        while ((strLine = br.readLine()) != null) {
            speedControlRecords.add(new SpeedControlRecord(strLine));
        }

        fstream.close();

        // #2 Display the number of vehicles whose data were recorded in the measurement on the screen.
        output = "Exercise 2.\nThe data of " + speedControlRecords.size() +
                " vehicles were recorded in the measurement.\n";

        System.out.print(output);
        myWriter.write(output);

        /*
         *   #3 From the available data, determine the number of vehicles that passed the exit point of
         *   the section before 9 o’clock. Display the number on the screen.
         */
        int result = SpeedControlRecord.getNumberOfRecordsWithExitBeforeTime(
                speedControlRecords, LocalTime.of(9,0,0));

        output = "\nExercise 3.\nBefore 9 o'clock " + result +
                " vehicles passed the exit point recorder.\n";

        System.out.print(output);
        myWriter.write(output);

        // #4 Request a time given in hour minute form from the user.
        System.out.println("\nExercise 4.");
        myWriter.write("\nExercise 4.\n");
        System.out.print("Enter an hour and minute value: ");

        Scanner in = new Scanner(System.in);
        int a = in.nextInt();
        int b = in.nextInt();

        output = "a. The number of vehicle that passed the entry point recorder: " +
                SpeedControlRecord.getNumberOfRecordsWithHourMinute(speedControlRecords, a, b) +
                "\nb. The traffic intensity: " +
                SpeedControlRecord.getTrafficIntensity(speedControlRecords, a, b);

        System.out.print(output);
        myWriter.write(output);

        /*
         *   #5 Find the speed of the vehicle with the highest average speed and the number of vehicles
         *      overtaken by it in the measured section. If there are several highest average speeds,
         *      it is enough to display only one of them. Display the license plate number of the vehicle,
         *      the average speed as an integer and the number of overtaken vehicles.
         */
        output = "\n\nExercise 5.";
        SpeedControlRecord fastest = SpeedControlRecord.getFastestRecord(speedControlRecords);
        output += "\nThe data of the vehicle with the highest speed are";
        output += "\nlicense plate number: " + fastest.licensePlate;
        output += "\naverage speed: " + String.format("%.0f", fastest.speed) + " km/h";
        output += "\nnumber of overtaken vehicles: " +
                SpeedControlRecord.getNumberOfOvertakenVehicles(speedControlRecords, fastest);

        output += "\n\nExercise 6.\n";
        output += String.format("%.2f", 100.0 * SpeedControlRecord.getNumberOfVehiclesExceedingSpeedLimit(speedControlRecords) / speedControlRecords.size())
                + "% percent of the vehicles were speeding.";

        System.out.print(output);
        myWriter.write(output);
        myWriter.close();
    }

    public static class SpeedControlRecord {

        static final double SPEED_LIMIT = 90.0;

        private String licensePlate;
        private LocalTime entryTime;
        private LocalTime exitTime;
        private LocalTime travelTime;
        private double speed;

        public SpeedControlRecord(String input) {
            String[] splited = input.split(" ");
            licensePlate = splited[0];

            entryTime = LocalTime.of(
                    Integer.parseInt(splited[1]),
                    Integer.parseInt(splited[2]),
                    Integer.parseInt(splited[3]),
                    Integer.parseInt(splited[4]) * 1000000);

            exitTime = LocalTime.of(
                    Integer.parseInt(splited[5]),
                    Integer.parseInt(splited[6]),
                    Integer.parseInt(splited[7]),
                    Integer.parseInt(splited[8]) * 1000000);


            speed = 10 / ((double) Duration.between(entryTime, exitTime).toNanos() / (3.6 * 10e11));
        }

        public static int getNumberOfRecordsWithExitBeforeTime(List<SpeedControlRecord> records, LocalTime time) {
            int ret = 0;
            for (SpeedControlRecord speedRecord: records) {
                if (speedRecord.exitTime.isBefore(time)) {
                    ++ret;
                }
            }
            return ret;
        }

        public static int getNumberOfRecordsWithHourMinute(List<SpeedControlRecord> records, int hour, int minute) {
            int ret = 0;
            for (SpeedControlRecord speedRecord: records) {
                if (speedRecord.entryTime.getHour() == hour && speedRecord.entryTime.getMinute() == minute) {
                    ++ret;
                }
            }
            return ret;
        }

        public static double getTrafficIntensity(List<SpeedControlRecord> records, int hour, int minute) {
            double count = 0;
            LocalTime start = LocalTime.of(hour, minute);
            LocalTime end = LocalTime.of(hour, minute + 1);

            for (SpeedControlRecord speedRecord: records) {
                if (speedRecord.entryTime.isBefore(start) && speedRecord.exitTime.isAfter(end)) {
                    ++count;
                }
            }
            return count / 10;
        }

        public static SpeedControlRecord getFastestRecord(List<SpeedControlRecord> records)  {
            SpeedControlRecord ret = records.get(0);
            for (SpeedControlRecord speedRecord: records) {
                if (speedRecord.speed > ret.speed) {
                    ret = speedRecord;
                }
            }
            return ret;
        }

        public static int getNumberOfOvertakenVehicles(List<SpeedControlRecord> records, SpeedControlRecord overtaker)  {
            int ret = 0;
            for (SpeedControlRecord speedRecord: records) {
                if (speedRecord.entryTime.isBefore(overtaker.entryTime)
                        && speedRecord.exitTime.isAfter(overtaker.exitTime)) {
                    ++ret;
                }
            }
            return ret;
        }

        public static int getNumberOfVehiclesExceedingSpeedLimit(List<SpeedControlRecord> records) {
            int ret = 0;

            for (SpeedControlRecord speedRecord: records) {
                if (speedRecord.speed > SPEED_LIMIT) {
                    ++ret;
                }
            }

            return ret;
        }
    }
}