package org.example.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BinaryMatrix {

    private String filename;
    private CompressMatrix disc_m;
    private CompressMatrix indisc_m;
    private CompressMatrix middle_m;

    private int rows_number;
    private int cols_number;

    public BinaryMatrix(File binary_matrix) {
           String dis_file_path = binary_matrix.getAbsolutePath();
           disc_m = init_m_from_file(dis_file_path);
    }

    public String getFilename() {
        return filename;
    }

    public CompressMatrix getDisc_m() {
        return disc_m;
    }

    public CompressMatrix getIndisc_m() {
        return indisc_m;
    }

    public CompressMatrix getMiddle_m() {
        return middle_m;
    }

    public int getRows_number() {
        return rows_number;
    }

    public int getCols_number() {
        return cols_number;
    }

    public BinaryMatrix(File disc_file, File indisc_file, boolean sort_per_type) {
        this.filename = getName(disc_file, indisc_file);
        String dis_file_path = disc_file.getAbsolutePath();
        String indis_file_path = indisc_file.getAbsolutePath();
        disc_m = init_m_from_file(dis_file_path);
        indisc_m = init_m_from_file(indis_file_path);
        gen_combined_matrix(true);
    }
    
//    public BinaryMatrix(File dataset_file,boolean sort_per_type) {
//        this.filename = baseName(dataset_file);
//        try {
////            genMatricesFromDataset(dataset_file);
//            gen_combined_matrix(sort_per_type);
//        } catch (IOException ex) {
//            Logger.getLogger(BinaryMatrix.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    public static boolean[][] loadFromFile(String filename) throws IOException {
//        boolean[][] bm;
//        try ( BufferedReader br = new BufferedReader(new FileReader(filename))) {
//            int rows = Integer.parseInt(br.readLine().trim());
//            int cols = Integer.parseInt(br.readLine().trim());
//            bm = new boolean[rows][cols];
//            String line = br.readLine();
//            int r = 0;
//            while (line != null) {
//                String[] columns = line.trim().split("\\s+");
//                for (int c = 0; c < columns.length; c++) {
//                    bm[r][c] = columns[c].trim().charAt(0) == '1';
//                }
//                line = br.readLine();
//                r++;
//            }
//        }
//        return bm;
//    }
//
//    private CompresseRow[] init_matrix_from_file(String filename) throws IOException {
//        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
//            rows_number = Integer.parseInt(br.readLine().trim());
//            cols_number = Integer.parseInt(br.readLine().trim());
//            CompresseRow[] m = new CompresseRow[rows_number];
//            String line = br.readLine();
//            int r_id = 0;
//            String curren_row;
//            while (line != null) {
//                curren_row = line.replaceAll("\\s", "");
//                m[r_id] = new CompresseRow(curren_row, r_id);
//                line = br.readLine();
//                r_id++;
//            }
//            return m;
//        }
//    }

    private CompressMatrix init_m_from_file(String filename) {
        CompressMatrix m = new CompressMatrix(filename);
        return m;
    }

    private void gen_combined_matrix(boolean sort_per_type) {
        if (indisc_m != null && disc_m != null) {
            String _filename_ = this.filename;
            if (sort_per_type) {
                middle_m = new CompressMatrix();
                _filename_ += "comb_sort.bol";
            } else {
                _filename_ += "comb.bol";
            }
            indisc_m.combine_matriz(disc_m, middle_m);
            try {
                save_combined_matrix(_filename_);
            } catch (IOException ex) {
                Logger.getLogger(BinaryMatrix.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void save_combined_matrix(String filename) throws IOException {
        try ( FileWriter fw = new FileWriter(filename, false)) {
            int rows = indisc_m.size() + disc_m.size() + middle_m.size();
            String info = "";
            if (middle_m != null) {
                info += indisc_m.size() + ", " + middle_m.size() + ", " + disc_m.size() + "\n";
            }
            info += rows + "\n" + indisc_m.getCols_number() + "\n";
            fw.write(info);
            indisc_m.write_matrix(fw);
            if (middle_m != null) {
                middle_m.write_matrix(fw);
            }
            disc_m.write_matrix(fw);
        }
    }

//    private void genMatricesFromDataset(File dataset_file) throws IOException {
//        String file_path = dataset_file.getAbsolutePath();
//        dataset = new DataSet(file_path);
//        dataset.print_info();
//        System.out.println("attributes: " + dataset.getAttrs()
//                + ", intances: " + dataset.getInstances()
//                + ", classes: " + dataset.getNum_classes());
//        disc_m = new CompressMatrix(dataset, true, true);
//        indisc_m = new CompressMatrix(dataset, false, true);
////        disc_m.simplify_matrix();
////        indisc_m.simplify_matrix();
////        disc_m.simplify_matrix();
//        filename = baseName(dataset_file);
//        try ( FileWriter fw = new FileWriter(filename + "_disc.bol", false)) {
//            int rows = disc_m.getRowsNumber();
//            int cols= disc_m.getCols_number();
//            String info = rows + "\n" + cols + "\n";
//            fw.write(info);
//            disc_m.write_matrix(fw);
//        }
//        try ( FileWriter fw = new FileWriter(filename + "_indisc.bol", false)) {
//            int rows = indisc_m.getRowsNumber();
//            int cols= indisc_m.getCols_number();
//            String info = rows + "\n" + cols + "\n";
//            fw.write(info);
//            indisc_m.write_matrix(fw);
//        }
//    }

    private static String getName(File filename1, File filename2) {
        var subsequence = "";
        String bmName1 = baseName(filename1);
        String bmName2 = baseName(filename2);
        int size = (bmName1.length() > bmName2.length()) ? bmName2.length() : bmName1.length();
        for (int i = 0; i < size; i++) {
            char a = bmName1.charAt(i);
            char b = bmName2.charAt(i);
            if (a == b) {
                subsequence += a;
            } else {
                return subsequence;
            }
        }
        return subsequence;
    }

    public static String baseName(File file_name) {
        return file_name.getName().replaceFirst("[.][^.]+$", "");
    }

}
