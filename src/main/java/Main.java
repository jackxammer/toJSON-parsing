import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCSV = parseCSV(columnMapping, fileName);

        String json = listToJson(listCSV);
        toJsonFileWriter(json,"data.json");

        List <Employee> listXML = parseXML("data.xml");
        String jsonXML = listToJson(listXML);
        toJsonFileWriter(jsonXML, "data2.json");






    }

    public static void toJsonFileWriter (String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseCSV(String[] mapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(mapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson (List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        return gson.toJson(list, listType);
    }

    public static List<Employee> parseXML (String name) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> parsedList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory. newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(name);
        NodeList nodeList = doc.getChildNodes();
        for (int i =0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                NamedNodeMap map = element.getAttributes();
                Employee employee = new Employee();

                for (int a = 0; a < map.getLength(); a++) {
                    switch (a) {
                        case 1: long id = Long.parseLong(map.item(a).getNodeValue());
                        break;
                        case 2: String firstName = map.item(a).getNodeValue();
                        break;
                        case 3: String lastName = map.item(a).getNodeValue();
                        break;
                        case 4: String country = map.item(a).getNodeValue();
                        break;
                        case 5: int age = Integer.parseInt(map.item(a).getNodeValue());
                    }


                }
            }
        }

        return parsedList;
    }

}
