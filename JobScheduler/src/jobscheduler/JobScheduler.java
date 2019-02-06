/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jobscheduler;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Program utilizes a priority queue to decide which jobs should be worked on in which order
 * @author natha
 */
public class JobScheduler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        start(args[0]);
    }
    
    /**
     * Main application and logic go in this method
     */
    public static void start(String input_file_name){
        Job[] input = new Job[10];                                              // array to store input as Job objects
        int input_size = 0;                                                     // keeps track of the number of values in input[]
        
        System.out.println("Second by second output for a Job Scheduler. "
                + "If a job is completed, the wait time and total execution "
                + "time for the job are also printed.\n");
        
        try{
            Scanner fin = new Scanner(new File(input_file_name));
            
            while (fin.hasNextLine()){
                String[] line = fin.nextLine().split(" ");                      // line of input file
                Job new_job = new Job(Integer.parseInt(line[0]),
                        Integer.parseInt(line[1]), Integer.parseInt(line[2]),
                        Integer.parseInt(line[3]));                             // convert line to Job object
                
                if (input_size == input.length){                                // if array is too short, double the size
                    Job[] new_array = new Job[2*input_size];
                    input = doubleArraySize(input, new_array);
                }
                
                input[input_size] = new_job;                                    // insert job obect into array
                input_size++;                                                   // increase array size
            }
            
            quicksort(input, 0, input_size-1);                                  // sort input by arrival time

            JobPriorityQueue job_heap = new JobPriorityQueue(input_size);       // create job heap
            
            int i = 0;
            for (int current_second = 0; !(job_heap.isEmpty() && i == input_size); current_second++){
                while (i != input_size && input[i].getArrivalTime() == current_second){                         // insert new job if it has arrived
                    job_heap.insert(input[i]);
                    i++;
                }

                if (!job_heap.isEmpty()){                                                                       // only run output if heap has values
                    Job current_job = job_heap.peekMax();
                    if (current_job.getStartTime() == 0){                                                       // record start time if job has not begun
                        current_job.setStartTime(current_second);
                    }

                    current_job.runJob();                                                                       // run job for one second
                    String output = String.format("Current second: %-4d | " + current_job, current_second);     // begin output string
                    if (current_job.getDuration() == current_job.getProgress()){                                // remove job from heap if it is finished
                        output += String.format("%d", current_second-current_job.getStartTime()+1);
                        job_heap.remove();
                    }
                    System.out.println(output);                                                                 // print job information
                }
                else{
                    System.out.println(String.format("Current second: %-4d | IDLE", current_second));           // if no value is in queue, print IDLE
                }
            }
        }
        catch (FileNotFoundException e){
            System.err.println("Opening file error");
        }
    }
    
    /**
     * Method to copy old_array into new_array, which is twice the size
     * @param old_array
     * @param new_array 
     * @return 
     */
    public static Job[] doubleArraySize(Job[] old_array, Job[] new_array){
        for (int i = 0; i < old_array.length; i++){             // loop to iterate through arrays and copy, value by value
            new_array[i] = old_array[i];
        }
        
        return new_array;
    }
    
    /**
     * Method to sort input by arrival time using quick sort
     * @param array
     * @param start
     * @param end 
     */
    public static void quicksort(Job[] array, int start, int end){
        if (start < end){                                           // if start >= end, partition is sorted
            Job end_value = array[end];
            int pivot = end_value.getArrivalTime();                 // pivot value
            int unsorted_start = 0, unsorted_end = end;             // keep track of unsorted index
            
            Job next_compare = array[0];
            while (unsorted_start < unsorted_end){                  // places values on appropiate side of pivot
                if (next_compare.getArrivalTime() <= pivot){        // place value to the left of pivot
                    array[unsorted_start] = next_compare;
                    unsorted_start++;
                    next_compare = array[unsorted_start];
                }
                else{                                               // place value to the right of the pivot
                    array[unsorted_end] = next_compare;
                    unsorted_end--;
                    next_compare = array[unsorted_end];
                }
            }
            
            array[unsorted_start] = end_value;
            quicksort(array, 0, unsorted_start-1);                  // recursive call for partition with values smaller than the pivot
            quicksort(array, unsorted_start+1, end);                // recursive call for partition with values larger than the pivot
        }
    }
    
}