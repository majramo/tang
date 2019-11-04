package utils

import org.apache.log4j.Logger
import org.testng.Reporter

import java.text.Format
import java.text.SimpleDateFormat

import static org.apache.log4j.Logger.getLogger

public class Person implements Serializable {
    static  Calendar date = Calendar.getInstance();
    static Format f = new SimpleDateFormat("S");
    int no
    int age
    String firstName
    String firstNameLc
    String firstNameShort
    String lastName
    String lastNameLc
    String lastNameShort
    String socialSecurityNumberLong
    String socialSecurityNumberLongDashLess
    String gender
    String address
    String zip
    String city
    String emailDomain
    String tel
    String mobile
    String user
    String pwd
    String email
    String url
    static random = new Random()
    String ip
    String pin
    String delimiter

    public Person(int i, age, firstName, lastName, socialSecurityNumberLong, socialSecurityNumberLongDashLess, gender, address, zip, city, emailDomain, String delimiter = ";"){
        no = i
        this.age = age
        this.firstName = firstName
        this.firstNameLc = firstName.toLowerCase().replaceAll(/[^a-z ]/,'')
        this.firstNameShort =  firstNameLc + "abc"
        this.lastName = lastName
        this.lastNameLc = lastName.toLowerCase().replaceAll(/[^a-z ]/,'')
        this.lastNameShort =  lastNameLc + "abc"
        this.socialSecurityNumberLong = socialSecurityNumberLong
        this.socialSecurityNumberLongDashLess = socialSecurityNumberLongDashLess
        this.gender = gender
        this.address = address
        this.zip = zip
        this.city = city
        this.emailDomain = emailDomain
        this.delimiter = delimiter
        tel = '0' + zip[2] + socialSecurityNumberLongDashLess[2..9]
        mobile = '076' + socialSecurityNumberLongDashLess[3..8]
        user = "${firstNameShort}abc"[0..2] + "_" +  "${lastNameShort}abc"[0..2] + "_$no"
        pwd  = "${lastNameShort}abc"[0..2] + "_" + "${firstNameShort}abc"[0..2] + "_$no"
        email = firstNameLc + "."  + lastNameLc + ".$no@$emailDomain"
        url = "www." + "${firstNameShort}abc"[0..2]  + "."  + "${lastNameShort}abc"[0..2]  + ".$no.$emailDomain"
        ip = (0..3).collect { random.nextInt(255) }.join('.')
        pin = socialSecurityNumberLongDashLess[8..11]
     }

    public String toString(){
        return [no, age, firstName, lastName, socialSecurityNumberLong, socialSecurityNumberLongDashLess, gender, address, zip, city, tel, mobile, user, pwd, email, url, ip, pin].join(delimiter)
    }

    public String getHeader(){
        return ['no', 'age', 'firstName', 'lastName', 'socialSecurityNumberLong', 'socialSecurityNumberLongDashLess', 'gender', 'address', 'zip', 'city', 'tel', 'mobile', 'user', 'pwd', 'email', 'url', 'ip', 'pin'].join(delimiter)
    }

    public getJsonMap(){
        return [
                no                              : no,
                age                             : age,
                firstName                       : firstName,
                lastName                        : lastName,
                socialSecurityNumberLong        : socialSecurityNumberLong,
                socialSecurityNumberLongDashLess: socialSecurityNumberLongDashLess,
                gender                          : gender,
                address                         : address,
                zip                             : zip,
                city                            : city,
                tel                             : tel,
                mobile                          : mobile,
                user                            : user,
                pwd                             : pwd,
                email                           : email,
                url                             : url,
                ip                              : ip,
                pin                             : pin
        ]
    }



}