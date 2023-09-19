# Android `strings.xml` Converter

This Java class, `Converter`, allows you to convert Android `strings.xml` resource files to CSV and vice versa. It provides an easy way to export your Android string resources to a spreadsheet-like format (CSV) and then import them back into a `strings.xml` file.

## Prerequisites

1. Java Development Kit (JDK) installed on your computer.
2. A text editor or integrated development environment (IDE) to run Java code.

## Usage

### Export Android `strings.xml` to CSV

1. Save your Android `strings.xml` file in the same directory as your Java program.

2. Create a Java program (e.g., `Main.java`) to use the `Converter` class. Import the `Converter` class as follows:

   ```java
   import java.io.IOException;

   public class Main {
       public static void main(String[] args) {
           String inputFileName = "strings.xml";
           String outputFileName = "output.csv";

           try {
               Converter.xmlToCsv(inputFileName, outputFileName);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   }
### Import CSV to Android strings.xml

1. Edit the output.csv file to add or modify the string resources you want to import.

2. Ensure that plurals are marked with `_plural` at the end of the key (e.g., apples_plural).

3. Ensure that string arrays are marked with `_string-array` at the end of the key (e.g., colors_string-array).

4. Save the modified output.csv file.

5. Create another Java program (e.g., Main.java) to use the Converter class. Import the Converter class and use the csvToXml method as follows:

   ```java
   import java.io.IOException;
   
   public class Main {
       public static void main(String[] args) {
           String inputFileName = "output.csv";
           String outputFileName = "imported_strings.xml";
   
           try {
               Converter.csvToXml(inputFileName, outputFileName);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   }


### Author
CodeKardio

