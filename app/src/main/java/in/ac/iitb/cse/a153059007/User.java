package in.ac.iitb.cse.a153059007;

/**
 * Created by ajay on 25/2/18.
 */

public class User {
    public static String firstname;
    public static String lastname;
    public static String mobile;
    public static String email;
    public static String gender = "male";
    public static String age;
    public static boolean isComplete = false;

    public static String getFirstname() {

        return firstname;
    }

    public static void setFirstname(String firstname) {
        User.firstname = firstname;
    }

    public static String getLastname() {
        return lastname;
    }

    public static void setLastname(String lastname) {
        User.lastname = lastname;
    }

    public static String getMobile() {
        return mobile;
    }

    public static void setMobile(String mobile) {
        User.mobile = mobile;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static String getGender() {
        return gender;
    }

    public static void setGender(String gender) {
        User.gender = gender;
    }

    public static String getAge() {
        return age;
    }

    public static void setAge(String age) {
        User.age = age;
    }
}
