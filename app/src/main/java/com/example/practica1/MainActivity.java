package com.example.practica1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //Text views for the displays
    TextView input_text, output_text;

    //String to receive the imput from ScientificActivity
    private String received_input;

    //Strings to store the input of the operation and the output(result)
    private String input = "", output = "";

    //MainActivity calculator buttons
    private Button button_zero, button_one, button_two, button_three, button_four, button_five, button_six, button_seven, button_eight,
            button_nine, button_suma, button_resta, button_multi, button_div, button_ce, button_c, button_equal;

    //Buttons for the activities
    private Button button_NIA, button_scientific;

    //Function called when the MainActivity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input_text = findViewById(R.id.input_text);
        output_text = findViewById(R.id.output_text);

        button_zero = findViewById(R.id.button_zero);
        button_one = findViewById(R.id.button_one);
        button_two = findViewById(R.id.button_two);
        button_three = findViewById(R.id.button_three);
        button_four = findViewById(R.id.button_four);
        button_five = findViewById(R.id.button_five);
        button_six = findViewById(R.id.button_six);
        button_seven = findViewById(R.id.button_seven);
        button_eight = findViewById(R.id.button_eight);
        button_nine = findViewById(R.id.button_nine);
        button_suma = findViewById(R.id.button_suma);
        button_resta = findViewById(R.id.button_resta);
        button_multi = findViewById(R.id.button_multi);
        button_div = findViewById(R.id.button_div);
        button_ce = findViewById(R.id.button_ce);
        button_c = findViewById(R.id.button_c);
        button_equal = findViewById(R.id.button_equal);

        button_NIA = findViewById(R.id.button_NIA);
        button_scientific = findViewById(R.id.button_scientific);

        //When the activity is created, clear the display
        clearEntry();

        //Gets the value from ScientificActivity intent
        Intent intent = getIntent();
        if (intent != null){
            //Check if the intent has "input"
            if (intent.hasExtra("input")){
                received_input = intent.getStringExtra("input");
                input_text.setText(received_input);
            }
        }
    }

    //Function called when the NIA button is clicked
    public void onNIAClicked(View view){
        Intent intent = new Intent(MainActivity.this, NIAActivity.class);
        startActivity(intent);
    }

    //Function called when the Scientific button is clicked
    public void onScientificClicked(View view){
        //Passes the input value to the scientific calculator
        Intent intent = new Intent(MainActivity.this, ScientificActivity.class);
        intent.putExtra("input", input);
        startActivity(intent);
        //Log message to check the value that is being passed to the ScientificActivity
        Log.d("ScientificActivity", "Starting ScientificActivity with input: " + input);
    }

    public void onButtonClicked(View view){

        //If there is a received input, set it as the input
        if (received_input != null){
            input = received_input;
            received_input = null;
        }

        Button button = (Button) view;
        String data = button.getText().toString();

        //Switch depending on the data (string received by the button)
        switch (data) {
            case "CE":
                clearEntry();
                break;

            case "C":
                clearLast();
                break;

            case "*":
                input += "*";
                solve();
                break;

            case "=":
                //When equal button is pressed, call the solve function
                solve();
                break;

            default:
                //If there is no input, show nothing
                if (input == null) {
                    input = "";
                }
                //If input is one of the following symbols, call solve function
                if (data.equals("+") || data.equals("/") || data.equals("-")) {
                    solve();
                }
                //Update the input
                input += data;
        }
        //Show the input
        input_text.setText(input);
    }

    //Function that handles the solve depending on the operator passed
    private void solve() {
        // Finds the index of the character in the input string.
        int index_of_plus = input.indexOf("+");
        //Starts searching at the second character to allow negative numbers to work
        int index_of_minus = input.indexOf("-", 1);
        int index_of_multiply = input.indexOf("*");
        int index_of_divide = input.indexOf("/");

        //Checks if any operator is found and it is not at the end or at the beginning of the string
        if ((index_of_plus != -1 || index_of_minus != -1 || index_of_multiply != -1 || index_of_divide != -1)
                && isOperatorPositionValid(index_of_plus, index_of_minus, index_of_multiply, index_of_divide)) {

            //Verifies if there is only an operator and there are no two consecutive operators
            if (hasSingleOperator(index_of_plus, index_of_minus, index_of_multiply, index_of_divide)) {
                try {
                    // Finds the operator
                    char operator = findOperator(index_of_plus, index_of_minus, index_of_multiply, index_of_divide);
                    //Extracts the numbers from the input string
                    double[] numbers = extractNumbers(input, operator);
                    //Performs the operation with both numbers and the operator
                    double result = performOperation(numbers[0], numbers[1], operator);

                    //Shows the result on the display (previously rounded)
                    output_text.setText(roundNumber(Double.toString(result)));

                    //Updates the input with the result from the operation
                    input = roundNumber(Double.toString(result));
                    
                } catch (NumberFormatException e) {
                    //Handles the errors during the conversions
                    output_text.setText("Error: Formato no válido");
                } catch (ArithmeticException e) {
                    //Handles the errors during the mathematical operation (ej. divide by zero)
                    output_text.setText("Error: " + e.getMessage());
                }
            } else {
                //Shows an error message if there are two consecutive operators
                output_text.setText("Error: Formato no válido");
            }
        }
    }

    //Function used to clear all the messages in the display
    private void clearEntry(){
        input = null;
        output = null;
        input_text.setText("");
        output_text.setText("");
    }

    //Function used to clear the last number/character on the input string
    private void clearLast(){
        if (input != null && input.length() > 0){
            input = input.substring(0, input.length() -1);
        }
    }
    
    //Check if the position of the operator is valid (not at the beginning or the end of the string) 
    private boolean isOperatorPositionValid(int index_of_plus, int index_of_minus, int index_of_multiply, int index_of_divide) {
        return (index_of_plus > 0 && index_of_plus < input.length() - 1)
                || (index_of_minus > 0 && index_of_minus < input.length() - 1)
                || (index_of_multiply > 0 && index_of_multiply < input.length() - 1)
                || (index_of_divide > 0 && index_of_divide < input.length() - 1);
    }

    //Checks if there is only one operator and not two consecutive operators
    private boolean hasSingleOperator(int index_of_plus, int index_of_minus, int index_of_multiply, int index_of_divide) {
        int count = 0;
        if (index_of_plus != -1) count++;
        if (index_of_minus != -1) count++;
        if (index_of_multiply != -1) count++;
        if (index_of_divide != -1) count++;
        return count == 1;
    }

    //Finds the operator in the string
    private char findOperator(int index_of_plus, int index_of_minus, int index_of_multiply, int index_of_divide) {
        if (index_of_plus != -1) return '+';
        if (index_of_minus != -1) return '-';
        if (index_of_multiply != -1) return '*';
        if (index_of_divide != -1) return '/';
        throw new IllegalStateException("No se pudo encontrar un operador válido.");
    }

    //Extracts and converts the two numbers from the string
    private double[] extractNumbers(String input, char operator) {
        int operator_index = input.indexOf(operator);
        //Get the first number (from the beginning of the string to the operator position)
        double firstNumber = Double.parseDouble(input.substring(0, operator_index).trim());
        //Get the first number (from the operator position+1 to the end of the string)
        double secondNumber = Double.parseDouble(input.substring(operator_index + 1).trim());
        return new double[]{firstNumber, secondNumber};
    }

    //Performs the mathematical operation depending on the operator
    private double performOperation(double firstNumber, double secondNumber, char operator) {
        switch (operator) {
            case '+':
                return firstNumber + secondNumber;
            case '-':
                return firstNumber - secondNumber;
            case '*':
                return firstNumber * secondNumber;
            case '/':
                if (secondNumber == 0) {
                    throw new ArithmeticException("División por cero");
                }
                return firstNumber / secondNumber;
            default:
                throw new IllegalArgumentException("Operador no válido: " + operator);
        }
    }

    //Function to take away the decimals if it is a natural number
    private String correctDecimals(double number){
        if (number == (int)number){
            return String.format("%.0f", number);
        } else {
            return Double.toString(number);
        }
    }

    //Function to round the decimal numbers to 4 decimals
    private String roundNumber(String number) {
        try {
            double parsed_number = Double.parseDouble(number);
            double rounded_number = Math.round(parsed_number * 10000.0) / 10000.0;
            return correctDecimals(rounded_number);
        } catch (NumberFormatException e) {
            //If the conversion is not possible, show an error
            return "Error: Formato no válido";
        }
    }
}