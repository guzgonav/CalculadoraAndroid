package com.example.practica1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScientificActivity extends AppCompatActivity {

    private double input_double;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientific);

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
        button_calculator = findViewById(R.id.button_calculator);

        //Gets the value from MainActivity
        String input_string = getIntent().getStringExtra("input");

        if (input_string != null && !input_string.isEmpty()){
            try{
                //Converts the value to a double
                input_double = Double.parseDouble(input_string);
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
    }

    TextView input_text, output_text;

    private String input, output, new_output;

    private Button button_zero, button_one, button_two, button_three, button_four, button_five, button_six, button_seven, button_eight,
            button_nine, button_suma, button_resta, button_multi, button_div, button_ce, button_c, button_equal;

    //Buttons for the activities
    private Button button_NIA, button_calculator;


    public void onNIAClicked(View view){
        Intent intent = new Intent(ScientificActivity.this, NIAActivity.class);
        startActivity(intent);
    }

    public void onCalculatorClicked(View view){
        if(input != null && !input.isEmpty()){
            //Passes the input value to the scientific calculator
            Intent intent = new Intent(ScientificActivity.this, MainActivity.class);
            startActivity(intent);

        }
    }

    public void onButtonClicked(View view){

        Button button = (Button) view;
        String data = button.getText().toString();
        switch (data){
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
                solve();
                break;

            default:
                if (input == null) {
                    input = "";
                }
                if (data.equals("+") || data.equals("/") || data.equals("-")) {
                    solve();
                }
                input += data;

        }
        input_text.setText(input);
    }

    private void solve() {
        // Busca la posición de los operadores en la cadena de entrada
        int index_of_plus = input.indexOf("+");
        int index_of_minus = input.indexOf("-");
        int index_of_multiply = input.indexOf("*");
        int index_of_divide = input.indexOf("/");

        // Verifica si se encontró algún operador y si no está al principio o al final de la cadena
        if ((index_of_plus != -1 || index_of_minus != -1 || index_of_multiply != -1 || index_of_divide != -1)
                && isOperatorPositionValid(index_of_plus, index_of_minus, index_of_multiply, index_of_divide)) {

            // Verifica si hay solo un operador y no hay dos operadores consecutivos
            if (hasSingleOperator(index_of_plus, index_of_minus, index_of_multiply, index_of_divide)) {
                try {
                    // Encuentra el operador y calcula el resultado
                    char operator = findOperator(index_of_plus, index_of_minus, index_of_multiply, index_of_divide);
                    double[] numbers = extractNumbers(input, operator);
                    double result = performOperation(numbers[0], numbers[1], operator);

                    // Muestra el resultado en la interfaz de usuario
                    output_text.setText(roundNumber(Double.toString(result)));

                    // Actualiza la entrada con el resultado
                    input = roundNumber(Double.toString(result));

                } catch (NumberFormatException e) {
                    // Manejo de errores durante la conversión
                    output_text.setText("Error: Formato de número no válido");
                } catch (ArithmeticException e) {
                    // Manejo de errores durante la operación matemática (por ejemplo, división por cero)
                    output_text.setText("Error: " + e.getMessage());
                }
            } else {
                // Muestra un mensaje de error si hay dos operadores consecutivos
                output_text.setText("Error: Formato de entrada no válido");
            }
        }
    }

    private void clearEntry(){
        input = null;
        output = null;
        output_text.setText("");
    }

    private void clearLast(){
        if (input != null && input.length() > 0){
            input = input.substring(0, input.length() -1);
        }
    }

    // Verifica si la posición del operador es válida (no al principio o al final de la cadena)
    private boolean isOperatorPositionValid(int index_of_plus, int index_of_minus, int index_of_multiply, int index_of_divide) {
        return (index_of_plus > 0 && index_of_plus < input.length() - 1)
                || (index_of_minus > 0 && index_of_minus < input.length() - 1)
                || (index_of_multiply > 0 && index_of_multiply < input.length() - 1)
                || (index_of_divide > 0 && index_of_divide < input.length() - 1);
    }

    // Verifica si hay solo un operador y no hay dos operadores consecutivos
    private boolean hasSingleOperator(int index_of_plus, int index_of_minus, int index_of_multiply, int index_of_divide) {
        int count = 0;
        if (index_of_plus != -1) count++;
        if (index_of_minus != -1) count++;
        if (index_of_multiply != -1) count++;
        if (index_of_divide != -1) count++;
        return count == 1;
    }

    // Encuentra el operador en la cadena
    private char findOperator(int index_of_plus, int index_of_minus, int index_of_multiply, int index_of_divide) {
        if (index_of_plus != -1) return '+';
        if (index_of_minus != -1) return '-';
        if (index_of_multiply != -1) return '*';
        if (index_of_divide != -1) return '/';
        throw new IllegalStateException("No se pudo encontrar un operador válido.");
    }

    // Extrae y convierte los dos números de la cadena
    private double[] extractNumbers(String input, char operator) {
        int operator_index = input.indexOf(operator);
        double firstNumber = Double.parseDouble(input.substring(0, operator_index).trim());
        double secondNumber = Double.parseDouble(input.substring(operator_index + 1).trim());
        return new double[]{firstNumber, secondNumber};
    }

    // Realiza la operación matemática según el operador
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

    private String roundNumber(String number) {
        try {
            double parsedNumber = Double.parseDouble(number);
            double roundedNumber = Math.round(parsedNumber * 10000.0) / 10000.0;
            return Double.toString(roundedNumber);
        } catch (NumberFormatException e) {
            // Si la conversión no es posible, muestra un mensaje de errorwe
            return "Error: Formato de número no válido";
        }
    }


}