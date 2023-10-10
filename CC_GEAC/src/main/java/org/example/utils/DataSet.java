/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.utils;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Maximus
 */
public class DataSet {

    public static Column[] columns;
    // Create an empty hash map

    private Row[] rows;
    private int num_classes = 0;
    private HashMap<String, Integer> instances_per_class = new HashMap<>();
    private int attrs;
    private int instances;

    private void parse(String filename) {
        File file = new File(filename);
        String file_type = getExtensionByStringHandling(file.getName()).get();
        switch (file_type) {
            case "csv" ->
                parseCsvFile(file);
            case "arff" ->
                parseArffFile(file);
            case "data" ->
                parseDataFile();
            default ->
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    Path getFileURIFromResources(File file) {
//        ClassLoader classLoader = getClass().getClassLoader();
        return Paths.get(file.getAbsolutePath());
    }

    public DataSet(String filename) {
        parse(filename);
        setInstancesPerClass();
//        print_dataset();
    }

    private void parseCsvFile(File file) {
        String delimiter = ",";
        CharBuffer charBuffer = null;
        Path pathToRead = getFileURIFromResources(file);

        try ( FileChannel fileChannel = (FileChannel) Files.newByteChannel(
                pathToRead, EnumSet.of(StandardOpenOption.READ))) {
            MappedByteBuffer mappedByteBuffer = fileChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            if (mappedByteBuffer != null) {
                charBuffer = StandardCharsets.UTF_8.decode(mappedByteBuffer);
            }
        } catch (IOException ex) {
            Logger.getLogger(DataSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (charBuffer != null) {
            String file_as_String = charBuffer.toString();
            String[] lines = file_as_String.split("\n");

            String[] attrs_name = lines[0].split(delimiter);
            attrs = attrs_name.length;
            instances = lines.length - 1;
            columns = new Column[attrs];
            this.rows = new Row[instances];
            int index = 0;
            Column.CLASS_INDEX = attrs - 1;//default class in the last possition
            for (String name : attrs_name) {
                Column c = new Column();
                c.setName(name);
                c.setIndex(index);
                columns[index++] = c;
            }

            setDataType(lines);
            for (int i = 0; i < instances; i++) {
                Row r = new Row();
                r.parse(lines[i + 1]);
                this.rows[i] = r;
            }
        } else {
            System.out.println("The file can't be read");
        }
    }

    private void parseArffFile(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArffReader arff = null;
        try {
            assert reader != null;
            arff = new ArffReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Instances data = arff.getData();
        data.setClassIndex(data.numAttributes() - 1);
        for (int instance_idx = 0; instance_idx <= data.numInstances() - 1; instance_idx++) {
            Instance instance = data.instance(instance_idx);
            System.out.println(instance.stringValue(0)); //get Attribute 0 as String
        }
    }

    private void parseDataFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public enum DataType {
        NONE, String, Integer, Double, Date
    }

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public void quickSort(Row arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, end);
        }
    }

    private int partition(Row arr[], int begin, int end) {
        Row pivot = arr[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            int comp_value = arr[j].compareTo(pivot);
            if (comp_value == 0 || comp_value == -1) {
                i++;
                Row swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        Row swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;

        return i + 1;
    }

    private void setInstancesPerClass() {
        instances_per_class = new HashMap<>();
        quickSort(this.rows, 0, this.instances - 1);
        int num_instances = 1;
        Row current_row;
        Row r;
        current_row = this.rows[0];
        for (int i = 1; i < this.rows.length; i++) {
            r = this.rows[i];
            if (current_row.equals(r)) {
                ++num_instances;
            } else {
                this.instances_per_class.put(current_row.getClassName(), num_instances);
                current_row = r;
                num_instances = 1;
            }
        }
        this.instances_per_class.put(current_row.getClassName(), num_instances);
        this.num_classes = instances_per_class.size();
    }

    private void setDataType(String[] lines) {
        LinkedList<Integer> indexTypeUnassigned = new LinkedList<>();
        LinkedList<Integer> indexTypeAssigned = new LinkedList<>();
        for (int i = 0; i < attrs; i++) {
            indexTypeUnassigned.add(i);
        }
        for (int i = 1; i < instances; i++) {
            String[] instance = lines[i].split(",");
            for (int j = 0; j < indexTypeUnassigned.size(); j++) {
                if (!instance[j].equals("?")) {
                    DataType new_type = getDatatype(instance[j]);
                    DataType current_type = columns[j].getType();
                    if (current_type == DataType.NONE) {
                        columns[j].setType(new_type);
                        indexTypeAssigned.add(j);
                    }
                }
            }
            if (indexTypeAssigned.size() == indexTypeUnassigned.size()) {
                return;
            }
        }
    }

    private DataType getDatatype(String input) {
        // checking for Integer
        // checking for floating point numbers
        if (input.matches("\\d+")) return DataType.Integer;
        else
            if (input.matches("\\d*[.]\\d+")) return DataType.Double;
        else
            if (input.matches("\\d{2}[/]\\d{2}[/]\\d{4}")) return DataType.Date;
        else
            if (input.matches("\\d{2}[-]\\w{3}[-]\\d{2}")) return DataType.Date;
        // checking for date format dd-mon-yyyy
        else
            if (input.matches("\\d{2}[-]\\w{3}[-]\\d{4}")) return DataType.Date;
        // checking for date format dd-month-yy
        else // checking for date format dd-month-yyyy
                if (input.matches("\\d{2}[-]\\w+[-]\\d{2}")) return DataType.Date;
        else // checking for date format yyyy-mm-dd
                if (input.matches("\\d{2}[-]\\w+[-]\\d{4}")) return DataType.Date;
        else // checking for String
                if (input.matches("\\d{4}[-]\\d{2}[-]\\d{2}")) return DataType.Date;
        else return DataType.String;
    }

    public Column[] getColumns() {
        return columns;
    }

    public Row[] getRows() {
        return rows;
    }

    public int getNum_classes() {
        return num_classes;
    }

    public int getAttrs() {
        return attrs;
    }

    public int getInstances() {
        return instances;
    }

    public HashMap<String, Integer> getInstances_per_class() {
        return instances_per_class;
    }

    public int[] getNumbInstancesPerClass() {
        int[] _instances_ = new int[num_classes];
        int i = 0;
        for (Map.Entry<String, Integer> entry : instances_per_class.entrySet()) {
            _instances_[i++] = entry.getValue();

        }
        return _instances_;
    }

    public void print_info() {
        for (Map.Entry<String, Integer> entry : instances_per_class.entrySet()) {
            String _class_ = entry.getKey();
            int _instances_ = entry.getValue();
            System.out.println(_class_ + " : " + _instances_);
        }
    }
     
    public void print_dataset(){
        for (Row r : rows) r.print();
    }
}
