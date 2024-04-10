package GKJava;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(() -> {
            try {
                Student[] students = readStudentsFromXML("student.xml");

                executor.execute(() -> {
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

                        int age;
                        for (Student student : students) {
                            age = calculateAge(student.getDateOfBirth());
                            student.setAge(age);
                            byte[] ageBytes = Integer.toString(student.getAge()).getBytes();

                            byte[] hashedBytes = messageDigest.digest(ageBytes);

                            StringBuilder encodedAge = new StringBuilder();
                            for (byte b : hashedBytes) {
                                encodedAge.append(String.format("%02x", b));
                            }

                            student.setEncodedAge(encodedAge.toString());
                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                });

                executor.submit(() -> {
                    for (Student student : students) {
                        boolean isPrime = checkIfSumOfDigitsIsPrime(student.getDateOfBirth());
                        student.setIsPrime(isPrime);
                    }
                });

                executor.submit(() -> {
                    writeToXMLFile(students);
                    writeResultsToFile(students);
                });

            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
        });

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Chương trình đã hoàn thành.");
    }

    private static Student[] readStudentsFromXML(String filePath) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(filePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        NodeList studentNodes = document.getElementsByTagName("Student");
        Student[] students = new Student[studentNodes.getLength()];

        for (int i = 0; i < studentNodes.getLength(); i++) {
            Element studentElement = (Element) studentNodes.item(i);
            int id = Integer.parseInt(studentElement.getAttribute("id"));
            String name = getElementTextByTagName(studentElement, "Name");
            String address = getElementTextByTagName(studentElement, "Address");
            String dateOfBirth = getElementTextByTagName(studentElement, "DateOfBirth");

            students[i] = new Student(id, name, address, dateOfBirth);
        }

        return students;
    }

    private static String getElementTextByTagName(Element element, String tagName) {
        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }

    private static int calculateAge(String dateOfBirth) {
        LocalDate birthDate = LocalDate.parse(dateOfBirth);
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);
        return period.getYears();
    }

    private static String encodeDigits(int number) {
        String digits = String.valueOf(number);
        StringBuilder encodedDigits = new StringBuilder();
        for (char digit : digits.toCharArray()) {
            int encodedDigit = Character.getNumericValue(digit) + 1;
            encodedDigits.append(encodedDigit);
        }
        return encodedDigits.toString();
    }

    private static boolean checkIfSumOfDigitsIsPrime(String dateOfBirth) {
        int sumOfDigits = calculateSumOfDigits(dateOfBirth);
        return isPrime(sumOfDigits);
    }

    private static int calculateSumOfDigits(String numberString) {
        int sum = 0;
        for (char digit : numberString.toCharArray()) {
            sum += Character.getNumericValue(digit);
        }
        return sum;
    }

    private static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    private static void writeToXMLFile(Student[] students) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element rootElement = document.createElement("Students");
            document.appendChild(rootElement);

            for (Student student : students) {
                Element studentElement = student.toXmlElement(document);
                rootElement.appendChild(studentElement);
            }

            // Ghi tệp XML
            File file = new File("students.xml");
            FileWriter writer = new FileWriter(file);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            writer.close();
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            e.printStackTrace();
        }
    }
    private static void writeResultsToFile(Student[] students) {
        try {
            FileWriter writer = new FileWriter("results.txt");
            for (Student student : students) {
                writer.write("Student ID: " + student.getId() + "\n");
                writer.write("Encoded Age: " + student.getEncodedAge() + "\n");
                writer.write("Is Prime: " + student.isPrime() + "\n");
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
