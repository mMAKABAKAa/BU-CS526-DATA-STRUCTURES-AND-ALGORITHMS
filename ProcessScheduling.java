package net.datastructures;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Scanner;

public class ProcessScheduling {
    public static void main(String[] args) throws IOException {
        SortedPriorityQueue D = new SortedPriorityQueue();//use SortedPriorityQueue to store the data
        UnsortedTableMap<Integer, Integer> wait = new UnsortedTableMap();//create a map to store the wait time of each process
        UnsortedTableMap<Integer, Integer> remaining = new UnsortedTableMap();////create a map to store the remaining time of each process

        //create the output file and chane the print stream to it
        File filename=new File("process_scheduling_output.txt");
        if (!filename.exists()) //create the file if it is not existed
            filename.createNewFile();
        PrintStream ps=new PrintStream("process_scheduling_output.txt");
        System.setOut(ps);

        //read the data from input file and store into D
        File file = new File("process_scheduling_input.txt");
        Scanner sc = new Scanner(file);//Read all processes from an input file
        while (sc.hasNextLine()) {
            String process = sc.nextLine();
            String[] str = process.split("\\s+");//split the line with space
            int[] arr = new int[str.length];
            for (int i = 0; i < str.length; i++) { //convert the string array into an int array
                arr[i] = Integer.parseInt(str[i]);
            }
            System.out.println("Id = " + arr[0] + ", priority = " + arr[1] + ", duration = " + arr[2] + ", arrival time = " + arr[3]);
            D.insert(arr[3], arr);//store the data in D with arrival time as the sorting key
            wait.put(arr[0], 0);//initialize the wait time 0 of each process
            remaining.put(arr[0], arr[2]);//initialize the remaining time of duration for each process
        }

        int id = 0;
        int currentTime = 0;//Initialize currentTime
        final int MAX_WAIT = 30;//set maximum waiting time
        int totalWait = 0;//initialize the total waiting time
        int waitNumber = D.size();//for calculating the average waitimg time
        System.out.println("\n" + "Maximum wait time = " + MAX_WAIT+"\n");
        HeapAdaptablePriorityQueue Q = new HeapAdaptablePriorityQueue<>();//create an empty priority queue Q
        //one time unit
        while (!D.isEmpty() || !Q.isEmpty()) {
            currentTime += 1;
            if (!D.isEmpty()) {
                Entry<Integer, int[]> min_entry = D.min();
                if (min_entry.getKey() <= currentTime) {
                    Entry<Integer, int[]> remove = D.removeMin();//remove the process from D
                    Q.insert(remove.getValue()[1], remove.getValue());
                }//insert it into Q, sorted by priority
            }

            if (!Q.isEmpty()) {
                Entry<Integer, int[]> execute = Q.min();//Execute the top process in Q for one time step
                int[] ext_val = execute.getValue();
                int ext_id = execute.getValue()[0];
                int duration = ext_val[2];
                int leftTime = remaining.get(ext_id);
                if (ext_id != id) { //print the new executing entry information
                    id = ext_id;//update the id to the executing process's id
                    System.out.println("Now running  Process id = " + ext_id + "\n" +
                            "Arrival = " + ext_val[3] + "\n" +
                            "Duration = " + duration + "\n" +
                            "Run time left = " + leftTime + "\n" +
                            " at time " + currentTime);
                }
                leftTime -= 1;
                remaining.put(ext_id, leftTime);
                System.out.println("Executed process ID:" + ext_id + " at time " + currentTime + " Remaining: " + leftTime);

                if (leftTime == 0) {//remaining time is 0,remove the process from Q
                    System.out.println("Finished running Process id = " + ext_id + "\n" +
                            "Arrival = " + ext_val[3] + "\n" +
                            "Duration = " + duration + "\n" +
                            "Run time left = " + leftTime + "\n" +
                            "at time " + currentTime);
                    totalWait += (currentTime - ext_val[3] + 1 - duration);//add the waiting time of executing process to the total waiting time
                    Q.remove(execute);
                }
                //Update the wait times of all processes in Q
                Iterator<Entry<Integer, int[]>> iter = Q.iterator();
                while (iter.hasNext()) {
                    Entry<Integer, int[]> process = iter.next();
                    int process_id = process.getValue()[0];
                    if (process_id != ext_id) {//the waiting process in Q
                        int waitTime = wait.get(process_id) + 1;
                        wait.put(process_id, waitTime);
                        //Update priorities of processes that have been waiting longer than max wait time
                        if (waitTime >= MAX_WAIT) { //when the waiting time of a process is longer than the maximum waiting time
                            Q.replaceKey(process, process.getKey() - 1);//Update priorities of the processes
                            wait.put(process_id, 0);//update their waiting time after decreasing the priority
                            System.out.println("Process " + process_id + " reached maximum wait time... decreasing priority to " + process.getKey());
                        }
                    }
                }
            }
        }
        System.out.println("Finished running all processes at time " + (currentTime));
        System.out.println("Average wait time: " + (float) totalWait / waitNumber);
        ps.close();
    }
}

