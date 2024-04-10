package GKJava;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Student {
    private int id;
    private String name;
    private String address;
    private String dateOfBirth;
    private int age;
    private boolean isPrime;
    private String encodedAge;

    public Student(int id, String name, String address, String dateOfBirth) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isPrime() {
        return isPrime;
    }

    public void setIsPrime(boolean isPrime) {
        this.isPrime = isPrime;
    }

    public String getEncodedAge() {
        return encodedAge;
    }

    public void setEncodedAge(String encodedAge) {
        this.encodedAge = encodedAge;
    }
    private int calculateSumOfDigits(String dateOfBirth) {
        int sum = 0;
        for (int i = 0; i < dateOfBirth.length(); i++) {
            char digitChar = dateOfBirth.charAt(i);
            if (Character.isDigit(digitChar)) {
                int digit = Character.getNumericValue(digitChar);
                sum += digit;
            }
        }
        return sum;
    }
    public Element toXmlElement(Document document) {
        Element studentElement = document.createElement("Student");

        
        Element idElement = document.createElement("Id");
        idElement.setTextContent(Integer.toString(this.id));
        studentElement.appendChild(idElement);

        
        Element nameElement = document.createElement("Name");
        nameElement.setTextContent(this.name);
        studentElement.appendChild(nameElement);

       
        Element addressElement = document.createElement("Address");
        addressElement.setTextContent(this.address);
        studentElement.appendChild(addressElement);

      
        Element ageElement = document.createElement("Age");
        ageElement.setTextContent(Integer.toString(this.age));
        studentElement.appendChild(ageElement);

       
        Element sumElement = document.createElement("Sum");
        sumElement.setTextContent(Integer.toString(calculateSumOfDigits(this.dateOfBirth)));
        studentElement.appendChild(sumElement);

        
        Element isPrimeElement = document.createElement("IsPrime");
        isPrimeElement.setTextContent(Boolean.toString(this.isPrime));
        studentElement.appendChild(isPrimeElement);

        
        Element encodedAgeElement = document.createElement("EncodedAge");
        encodedAgeElement.setTextContent(this.encodedAge);
        studentElement.appendChild(encodedAgeElement);

        return studentElement;
    }
}


