import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        for (int i = 0; i < input.length(); i++) {
            System.out.print(String.valueOf(input.charAt(i)) + String.valueOf(input.charAt(i)));
        }
    }
}
