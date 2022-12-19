import database.DBWorker;

import java.util.Scanner;
import java.util.Stack;

public class TestApp {

    private static DBWorker db = DBWorker.getInstance();
    private static Double result;

    public static void main(String[] args) {
        Integer choice1;
        String expression;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose what action you want to do:\n1-Input expression, calculate it and save to database\n2-Get all expressions\n3- Get expressions by result value\n4- Get expressions by range of result value");
        choice1 = scanner.nextInt();
        scanner.nextLine();
        switch (choice1) {
            case 1:
                System.out.println("Please, enter expression, which you want to calculate:");
                expression = scanner.nextLine();
                System.out.println("You wrote: " + expression);
                if (expressionIsCorrect(expression)) {
                    result = calculate(expression);
                    System.out.println("Result of calculation: " + result);
                    db.saveExpression(expression, result);
                } else {
                    System.out.println("Your expression is incorrect");
                }

                break;
            case 2:
                db.getExpressions();
                break;
            case 3:
                System.out.println("Please, enter result value of expressions, which you want to find:");
                Double resultFind1 = Double.parseDouble(scanner.nextLine());
                db.getExpressionsByResult(resultFind1);
                System.out.println("1-Edit expression\n2-Exit");
                Integer choice3 = scanner.nextInt();
                if (choice3 == 1) {
                    System.out.println("Write number of expression, which you want to edit");
                    Integer numberOfExp = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Write edited expression:");
                    String newExp = scanner.nextLine();
                    if (expressionIsCorrect(newExp)) {
                        result = calculate(newExp);
                        System.out.println("Result of calculation: " + result);
                        db.editExpression(numberOfExp, newExp, result);
                    }
                }
                else{
                    System.exit(0);
                }
                break;
            case 4:
                System.out.println("Please, enter result value of expressions, which you want to find:");
                Double resultFind2 = Double.parseDouble(scanner.nextLine());
                System.out.println("1-Find all expressions, which result is bigger than value, you wrote\n2-Find all expressions, which result is less than value, you wrote");
                Integer choice2 = scanner.nextInt();
                if (choice2 == 1) {
                    db.getExpressionsByResultBiggerThan(resultFind2);
                    return;
                }
                if (choice2 == 2) {
                    db.getExpressionsByResultLessThan(resultFind2);
                    return;
                } else {
                    System.out.println("Press 1 or 2 button!");
                }
                break;
            default:
                break;
        }
    }

    public static boolean expressionIsCorrect(String expression) {
        if (hasOperator(expression) && correctOperatorOrder(expression) && containsOnlyNumbers(expression) && isBracketsBalanced(expression)&&correctBracketsOrder(expression)) {
            return true;
        } else {
            return false;
        }
    }


    public static Double calculate(String exp) {
        String minus = "";
        exp.replaceAll(" ", "");

        Stack<Double> numbers = new Stack<Double>();

        Stack<Character> operators = new Stack<Character>();

        for (int i = 0; i < exp.length(); i++) {

            if (exp.charAt(i) >= '0' && exp.charAt(i) <= '9') {
                StringBuffer sbuf = new StringBuffer();
                while (i < exp.length() && exp.charAt(i) >= '0' && exp.charAt(i) <= '9') {
                    if (minus.equals("-")) {
                        sbuf.append(minus + exp.charAt(i++));
                        minus = "";
                    } else {
                        sbuf.append(exp.charAt(i++));
                    }
                }
                numbers.push(Double.parseDouble(sbuf.toString()));
                i--;
            } else if (exp.charAt(i) == '(') operators.push(exp.charAt(i));


            else if (exp.charAt(i) == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            } else if (exp.charAt(i) == '+' || exp.charAt(i) == '-' || exp.charAt(i) == '*' || exp.charAt(i) == '/') {
                if (exp.charAt(i + 1) != '-' && i > 0) {
                    while (!operators.empty() && hasPrecedence(exp.charAt(i), operators.peek())) {
                        numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                    }
                    operators.push(exp.charAt(i));
                } else if (i == 0 && exp.charAt(i + 1) != '(') {
                    minus = "-";
                } else {
                    while (!operators.empty() && hasPrecedence(exp.charAt(i), operators.peek())) {
                        numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
                    }
                    operators.push(exp.charAt(i));
                    i++;
                    minus = "-";
                }
            }
        }

        while (!operators.empty()) {
            numbers.push(applyOp(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    public static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        else return true;
    }

    public static Double applyOp(Character op, Double b, Double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0.0;
    }

    public static boolean hasOperator(String exp) {
        boolean hasOperator = false;
        int operatorCount = 0;
        for (int i = 0; i < exp.length(); i++) {
            if (exp.charAt(i) == '*' || exp.charAt(i) == '/' || exp.charAt(i) == '+' || exp.charAt(i) == '-') {
                operatorCount++;
            }
        }
        if (operatorCount > 0) {
            return true;
        } else {
            System.out.println("Expression must have at least one operator!");
            return false;
        }
    }

    public static boolean correctOperatorOrder(String exp) {
        boolean correctOrder = false;
        for (int i = 0; i < exp.length(); i++) {
            if (exp.charAt(i) == '*' || exp.charAt(i) == '/' || exp.charAt(i) == '+' || exp.charAt(i) == '-') {
                if (exp.charAt(i + 1) == '*' || exp.charAt(i + 1) == '/' || (exp.charAt(i + 1) == '+')) {
                    System.out.println("Expression must have correct operator order!");
                    return false;
                } else {
                    correctOrder = true;
                }
            }
        }
        return correctOrder;
    }

    public static boolean correctBracketsOrder(String exp) {
        boolean correctOrder = true;
        for (int i = 0; i < exp.length() - 1; i++) {
            if (exp.charAt(i) == '(' || exp.charAt(i) == ')') {
                if (Character.isDigit(exp.charAt(i + 1)) || exp.charAt(i + 1) == '-') {
                    correctOrder = true;
                } else {
                    System.out.println("Expression must have correct brackets order!");
                    return false;
                }
            }
        }
        return correctOrder;
    }

    public static boolean containsOnlyNumbers(String exp) {
        boolean isCorrect = true;
        for (int i = 0; i < exp.length(); i++) {
            if (Character.isLetter(exp.charAt(i))) {
                System.out.println("Expression must not contain letters!");
                isCorrect = false;
            }
        }
        return isCorrect;
    }

    public static boolean isBracketsBalanced(String exp) {
        boolean flag = true;
        int count = 0;

        for (int i = 0; i < exp.length(); i++) {
            if (exp.charAt(i) == '(') {
                count++;
            } else if (exp.charAt(i) == ')') {
                count--;
            }
        }

        if (count != 0) {
            flag = false;
            System.out.println("Check if brackets balance in your expression is correct!");
        }
        return flag;
    }
}
