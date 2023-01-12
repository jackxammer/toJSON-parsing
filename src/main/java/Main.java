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

    public static List<String> forClass = new ArrayList<>();

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCSV = parseCSV(columnMapping, fileName);

        String json = listToJson(listCSV);
        toJsonFileWriter(json,"data.json");

        List<Employee> listXML = parseXML("data.xml");
        String jsonXML = listToJson(listXML);
        toJsonFileWriter(jsonXML, "data2.json");

    }

    public static void toJsonFileWriter(String json, String fileName) {
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

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        return gson.toJson(list, listType);
    }

    public static List<Employee> parseXML(String name) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> parsedList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(name);
        Node rootNode = doc.getDocumentElement();
        read(rootNode);

        for (int i = 0; i < forClass.size(); i = i + 5) {
            int j = i;
            Employee employee = new Employee();
            employee.id = Long.parseLong(forClass.get(j++));
            employee.firstName = forClass.get(j++);
            employee.lastName = forClass.get(j++);
            employee.country = forClass.get(j++);
            employee.age = Integer.parseInt(forClass.get(j++));
            parsedList.add(employee);
        }

        return parsedList;
    }

    public static void read(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                if (!node_.getNodeName().equals("employee")) {
                    Element element = (Element) node_;
                    forClass.add(element.getTextContent());
                }
                read(node_);
            }
        }
    }
}
