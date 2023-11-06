import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Main {
    public static void main(String[] args) {
        // put your code here
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String input = in.readLine();
            String[] parts = input.split("\\s");
            String inputString = parts[0];
            int inputNumber = Integer.parseInt(parts[1]);
            if (inputNumber > inputString.length()) {
                System.out.println(inputString);
                return;
            }
            String beginning = inputString.substring(0, inputNumber);
            String end = inputString.substring(inputNumber);
            System.out.println(end + beginning);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}