import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        String substring = sc.nextLine();
        Pattern pattern = Pattern.compile(substring);
        Matcher matcher = pattern.matcher(input);
        int counter = 0;
        while (matcher.find()) {
            counter++;
        }
        System.out.println(counter);
    }
}
