import javax.swing.*;
import java.awt.event.*;

public class Calculator extends JFrame {

    // these are variables for the gui component object references
    private JPanel mainPanel;
    private JButton sevenBtn;
    private JButton eightBtn;
    private JButton fourBtn;
    private JButton fiveBtn;
    private JButton nineBtn;
    private JButton sixBtn;
    private JButton mulBtn;
    private JButton minusBtn;
    private JButton oneBtn;
    private JButton twoBtn;
    private JButton threeBtn;
    private JButton addBtn;
    private JButton clearBtn;
    private JButton signBtn;
    private JButton percentageBtn;
    private JButton divideBtn;
    private JButton mClearBtn;
    private JButton logBtn;
    private JButton powerBtn;
    private JButton sqrBtn;
    private JButton mSubBtn;
    private JButton mAddBtn;
    private JButton mRecallBtn;
    private JButton squareBtn;
    private JButton zeroBtn;
    private JButton digitBtn;
    private JButton equalBtn;
    private JPanel buttonsPanel;
    private JTextField textField;

    // these are variables for the underlying logic
    // define enum for the binary operators
    // needed for when the "=" button is clicked, to determine what operation needs to be done at that point
    enum binaryOperationType {
        NOT_SET, POWER, DIVIDE, MULTIPLY, SUBTRACT, ADD
    }

    enum unaryOperationType {
        PERCENTAGE, SQUARE, SQUARE_ROOT, SIGN, LOG
    }

    enum operandType {
        OPERAND_1, OPERAND_2
    }

    Double operand1Double;
    binaryOperationType currentBinaryOperation;
    Double operand2Double;
    operandType currentOperand;
    Double resultDouble;
    String resultString;
    String currentOperandString;
    Double memoryDouble;
    boolean currentOperandRdyToBeReset;

    // now for defining the classes derived from actionListener
    static class actionListeners {

        // btnClicked subclass
        static abstract class btnClicked implements ActionListener {

            /*
                basically all the buttons need access to the calculator for something (e.g., getting certain values or setting certain values)
            */
            Calculator calculator;

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             */
            btnClicked(Calculator calculator)
            {
                this.calculator = calculator;
            }
        }

        static class mAddBtnClicked extends btnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             */
            mAddBtnClicked(Calculator calculator)
            {
                super(calculator);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.memoryDouble += Double.parseDouble(calculator.currentOperandString);
                calculator.currentOperandRdyToBeReset = true;
            }
        }

        static class mSubBtnClicked extends btnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             */
            mSubBtnClicked(Calculator calculator)
            {
                super(calculator);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.memoryDouble -= Double.parseDouble(calculator.currentOperandString);
                calculator.currentOperandRdyToBeReset = true;
            }
        }

        static class mClearBtnClicked extends btnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             */
            mClearBtnClicked(Calculator calculator)
            {
                super(calculator);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.memoryDouble = 0.0;
            }
        }

        static class mRecallBtnClicked extends btnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             */
            mRecallBtnClicked(Calculator calculator)
            {
                super(calculator);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.updateOperandStringAndTextFieldOfCalculatorWithString(calculator.memoryDouble.toString());
                calculator.parseAndStoreCurrentOperandStringInOperand1();
                calculator.replaceTextFieldWithIntIfApplicable();
                calculator.currentOperandRdyToBeReset = true;
            }
        }

        // numberBtnClicked subclasses
        static class numberBtnClicked extends btnClicked {
            // we need this so that the callback function can update the GUI

            String number;

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * @param number a string object representing the number of the corresponding number button
             *               (e.g., "1", "2", "3", etc.). Don't have to manually specify this argument. Simply
             *               instantiate one of the subclasses of numberBtnClicked (e.g., zeroBtnClicked, oneBtnClicked, etc.)
             *               and the argument will be automatically supplied.
             */
            public numberBtnClicked(Calculator calculator, String number)
            {
                super(calculator);
                this.number = number;
            }
            
            @Override
            public void actionPerformed(ActionEvent e) {
                numberBtnCallBack();
            }

            void numberBtnCallBack()
            {
                resetCurrentOperandAndTextFieldIfRdyAndResetFlag();
                calculator.clearCurrentOperandStringIfJustZeroAndUpdateTextField();
                calculator.appendToCurrentOperandStringAndUpdateTextField(number);
            }

            void resetCurrentOperandAndTextFieldIfRdyAndResetFlag()
            {
                if(calculator.currentOperandRdyToBeReset)
                {
                    calculator.currentOperandString = "0";
                    calculator.updateTextFieldOfCalculatorWithOperandString();
                    calculator.currentOperandRdyToBeReset = false;
                }
            }
        }

        static class clearBtnClicked extends btnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             */
            clearBtnClicked(Calculator calculator) {
                super(calculator);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                calculator.currentBinaryOperation = binaryOperationType.NOT_SET;
                calculator.currentOperand = operandType.OPERAND_1;
                calculator.updateOperandStringAndTextFieldOfCalculatorWithString("0");
                calculator.operand1Double = 0.0;
                calculator.operand2Double = 0.0;
            }
        }

        static class binaryOperationBtnClicked extends btnClicked {

            binaryOperationType binaryOperation;


            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * @param binaryOperation an object of enumeration binaryOperationType. The possible enumerations are: NOT_SET, POWER, DIVIDE, MULTIPLY, SUBTRACT, ADD.
             *                        Don't have to supply this argument manually. Subclasses of this class (which are for specific binary operations) will automatically supply this argument.
             *                        Used by internal method to evaluate what binary operation to perform since all call the same method
             */
            binaryOperationBtnClicked(Calculator calculator, binaryOperationType binaryOperation)
            {
                super(calculator);
                this.binaryOperation = binaryOperation;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrentBinaryOperation();
                finalizeOperand1AndPrepareOperand2();
            }

            void setCurrentBinaryOperation()
            {
                switch(binaryOperation){
                    case MULTIPLY:
                        calculator.currentBinaryOperation = binaryOperationType.MULTIPLY;
                        break;
                    case DIVIDE:
                        calculator.currentBinaryOperation = binaryOperationType.DIVIDE;
                        break;
                    case ADD:
                        calculator.currentBinaryOperation = binaryOperationType.ADD;
                        break;
                    case SUBTRACT:
                        calculator.currentBinaryOperation = binaryOperationType.SUBTRACT;
                        break;
                    case POWER:
                        calculator.currentBinaryOperation = binaryOperationType.POWER;
                        break;
                }
            }

            void finalizeOperand1AndPrepareOperand2()
            {
                calculator.parseAndStoreCurrentOperandStringInOperand1();
                /* currentOperandString will be reset but text field won't be */
                calculator.currentOperandString = "";
                calculator.currentOperand = operandType.OPERAND_2;
            }
        }

        static class unaryOperationBtnClicked extends btnClicked {

            unaryOperationType unaryOperation;

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * @param unaryOperation an object of enumeration unaryOperationType. The possible enumerations are: PERCENTAGE, SQUARE, SQUARE_ROOT, SIGN, LOG
             *                       Don't have to supply this argument manually. Subclasses of this class (which are for specific unary operations) will automatically supply this argument.
             *                       Used by internal method to evaluate what unary operation to perform on operand1 since all call the same method
             */
            unaryOperationBtnClicked(Calculator calculator, unaryOperationType unaryOperation) {
                super(calculator);
                this.unaryOperation = unaryOperation;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if(calculator.currentOperand == operandType.OPERAND_1)
                {
                    performUnaryOperationStoreIntoOperand1AndUpdateCurrentOperandStringAndTextField();
                }
                else // when currentOperand is OPERAND_2
                {
                    Double currentValueInTextFieldAsDouble = Double.parseDouble(calculator.textField.getText());
                    Double currentValueInTextFieldAsDoubleTimesNegativeOne = currentValueInTextFieldAsDouble * -1;
                    calculator.currentOperandString = currentValueInTextFieldAsDoubleTimesNegativeOne.toString();
                    calculator.textField.setText(currentValueInTextFieldAsDoubleTimesNegativeOne.toString());
                    calculator.replaceTextFieldWithIntIfApplicable();
                }
            }

            void performUnaryOperationStoreIntoOperand1AndUpdateCurrentOperandStringAndTextField()
            {
                calculator.parseAndStoreCurrentOperandStringInOperand1();

                switch(unaryOperation)
                {
                    case SIGN:
                        calculator.resultDouble = calculator.operand1Double * -1;
                        break;
                    case PERCENTAGE:
                        calculator.resultDouble = calculator.operand1Double / 100;
                        break;
                    case LOG:
                        calculator.resultDouble = Math.log(calculator.operand1Double);
                        break;
                    case SQUARE:
                        calculator.resultDouble = calculator.operand1Double * calculator.operand1Double;
                        break;
                    case SQUARE_ROOT:
                        calculator.resultDouble = Math.sqrt(calculator.operand1Double);
                        break;
                }

                // System.out.printf("Unary Operation (%s) done. Result is: %f\n", unaryOperation.toString(), calculator.resultDouble);
                calculator.resultString = calculator.resultDouble.toString();
                calculator.updateOperandStringAndTextFieldOfCalculatorWithString(calculator.resultString);
                calculator.replaceTextFieldWithIntIfApplicable();
                calculator.storeResultInOperand1();
                calculator.currentOperandRdyToBeReset = true;
            }
        }

        static class zeroBtnClicked extends numberBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * "0" is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            public zeroBtnClicked(Calculator calculator) {
                super(calculator, "0");
            }
        }
        static class oneBtnClicked extends numberBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * "1" is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            public oneBtnClicked(Calculator calculator) {
                super(calculator, "1");
            }
        }
        static class twoBtnClicked extends numberBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * "2" is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            public twoBtnClicked(Calculator calculator) {
                super(calculator, "2");
            }
        }
        static class threeBtnClicked extends numberBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * "3" is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            public threeBtnClicked(Calculator calculator) {
                super(calculator, "3");
            }
        }
        static class fourBtnClicked extends numberBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * "4" is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            public fourBtnClicked(Calculator calculator) {
                super(calculator, "4");
            }
        }
        static class fiveBtnClicked extends numberBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * "5" is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            public fiveBtnClicked(Calculator calculator) {
                super(calculator, "5");
            }
        }
        static class sixBtnClicked extends numberBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * "6" is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            public sixBtnClicked(Calculator calculator) {
                super(calculator, "6");
            }
        }
        static class sevenBtnClicked extends numberBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * "7" is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            public sevenBtnClicked(Calculator calculator) {
                super(calculator, "7");
            }
        }
        static class eightBtnClicked extends numberBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * "8" is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            public eightBtnClicked(Calculator calculator) {
                super(calculator, "8");
            }
        }
        static class nineBtnClicked extends numberBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * "9" is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            public nineBtnClicked(Calculator calculator) {
                super(calculator, "9");
            }
        }

        // other actionListener subclasses
        static class digitBtnClicked implements ActionListener {

            Calculator calculator;

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             */
            public digitBtnClicked(Calculator calculator)
            {
                this.calculator = calculator;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if(calculator.currentOperandRdyToBeReset)
                {
                    resetCurrentOperandAndTextFieldIfRdyAndResetFlag();
                    calculator.appendToCurrentOperandStringAndUpdateTextField(".");
                }
                else if(!(calculator.decimalIsInCurrentOperandString()))
                {
                    calculator.appendToCurrentOperandStringAndUpdateTextField(".");
                    // calculator.updateTextFieldOfCalculatorWithOperandString();
                }
            }

            void resetCurrentOperandAndTextFieldIfRdyAndResetFlag()
            {
                if(calculator.currentOperandRdyToBeReset)
                {
                    calculator.currentOperandString = "0";
                    calculator.updateTextFieldOfCalculatorWithOperandString();
                    calculator.currentOperandRdyToBeReset = false;
                }
            }
        }

        static class signBtnClicked extends unaryOperationBtnClicked {
            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * enumeration SIGN is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            signBtnClicked(Calculator calculator)
            {
                super(calculator, unaryOperationType.SIGN);
            }
        }

        static class percentageBtnClicked extends unaryOperationBtnClicked {
            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * enumeration PERCENTAGE is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            percentageBtnClicked(Calculator calculator)
            {
                super(calculator, unaryOperationType.PERCENTAGE);
            }
        }
        
        static class squareRootBtnClicked extends unaryOperationBtnClicked {
            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * enumeration SQUARE_ROOT is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            squareRootBtnClicked(Calculator calculator)
            {
                super(calculator, unaryOperationType.SQUARE_ROOT);
            }
        }

        static class squareBtnClicked extends unaryOperationBtnClicked {
            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * enumeration SQUARE is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            squareBtnClicked(Calculator calculator)
            {
                super(calculator, unaryOperationType.SQUARE);
            }
        }

        static class logBtnClicked extends unaryOperationBtnClicked {
            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * enumeration LOG is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            logBtnClicked(Calculator calculator)
            {
                super(calculator, unaryOperationType.LOG);
            }
        }

        static class multiplyBtnClicked extends binaryOperationBtnClicked {
            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * enumeration MULTIPLY is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            multiplyBtnClicked(Calculator calculator){
                super(calculator, binaryOperationType.MULTIPLY);
            }
        }

        static class divideBtnClicked extends binaryOperationBtnClicked {
            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * enumeration DIVIDE is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            divideBtnClicked(Calculator calculator)
            {
                super(calculator, binaryOperationType.DIVIDE);
            }
        }

        static class addBtnClicked extends binaryOperationBtnClicked {
            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * enumeration ADD is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            addBtnClicked(Calculator calculator)
            {
                super(calculator, binaryOperationType.ADD);
            }
        }

        static class subBtnClicked extends binaryOperationBtnClicked {
            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * enumeration SUBTRACT is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            subBtnClicked(Calculator calculator)
            {
                super(calculator, binaryOperationType.SUBTRACT);
            }
        }

        static class powerBtnClicked extends binaryOperationBtnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variables
             * enumeration POWER is automatically supplied to the super constructor/constructor of numberBtnClicked
             */
            powerBtnClicked(Calculator calculator) {
                super(calculator, binaryOperationType.POWER);
            }
        }

        static class equalBtnClicked extends btnClicked {

            /**
             * Initializes a btnClicked object which is an implementation of an ActionListener
             * @param calculator an object of type Calculator, is an attribute for easy access
             *                   of Calculator methods and variable
             */
            equalBtnClicked(Calculator calculator)
            {
                super(calculator);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                performBinaryOperationStoreIntoOperand1AndUpdateCurrentOperandStringAndTextField();
            }

            void performBinaryOperationStoreIntoOperand1AndUpdateCurrentOperandStringAndTextField()
            {
                calculator.parseAndStoreCurrentOperandStringInOperand2();

                switch(calculator.currentBinaryOperation)
                {
                    case MULTIPLY:
                        calculator.resultDouble = calculator.operand1Double * calculator.operand2Double;
                        break;
                    case DIVIDE:
                        if(calculator.operand2Double == 0.0)
                        {
                            calculator.updateOperandStringAndTextFieldOfCalculatorWithString("ERROR");
                            calculator.currentBinaryOperation = binaryOperationType.NOT_SET;
                            calculator.currentOperand = operandType.OPERAND_1;
                            calculator.currentOperandRdyToBeReset = true;
                            return;
                        }
                        else
                        {
                            calculator.resultDouble = calculator.operand1Double / calculator.operand2Double;
                        }
                        break;
                    case ADD:
                        calculator.resultDouble = calculator.operand1Double + calculator.operand2Double;
                        break;
                    case SUBTRACT:
                        calculator.resultDouble = calculator.operand1Double - calculator.operand2Double;
                        break;
                    case POWER:
                        calculator.resultDouble = Math.pow(calculator.operand1Double, calculator.operand2Double);
                        break;
                    case NOT_SET:
                        calculator.resultDouble = calculator.operand1Double;
                        break;
                }
                calculator.updateOperandStringAndTextFieldOfCalculatorWithString(calculator.resultDouble.toString());
                calculator.storeResultInOperand1();
                calculator.currentOperand = operandType.OPERAND_1;
                calculator.updateOperandStringAndTextFieldOfCalculatorWithString(calculator.resultDouble.toString());
                calculator.replaceTextFieldWithIntIfApplicable();
                calculator.currentBinaryOperation = binaryOperationType.NOT_SET;
                calculator.currentOperandRdyToBeReset = true;
            }
        }
    }

    /**
     * After a Calculator object is created in memory, the code here does a part of the initialization
     * JComponents are instantiated and packed by IntelliJ while the instantiation of event listeners are done here
     * We will also configure initial properties of the gui window here (e.g., setting visibility to true, setting size, etc.)
     * */
    public Calculator() {
        zeroBtn.addActionListener(new actionListeners.zeroBtnClicked(this));
        oneBtn.addActionListener(new actionListeners.oneBtnClicked(this));
        twoBtn.addActionListener(new actionListeners.twoBtnClicked(this));
        threeBtn.addActionListener(new actionListeners.threeBtnClicked(this));
        fourBtn.addActionListener(new actionListeners.fourBtnClicked(this));
        fiveBtn.addActionListener(new actionListeners.fiveBtnClicked(this));
        sixBtn.addActionListener(new actionListeners.sixBtnClicked(this));
        sevenBtn.addActionListener(new actionListeners.sevenBtnClicked(this));
        eightBtn.addActionListener(new actionListeners.eightBtnClicked(this));
        nineBtn.addActionListener(new actionListeners.nineBtnClicked(this));
        signBtn.addActionListener(new actionListeners.signBtnClicked(this));
        digitBtn.addActionListener(new actionListeners.digitBtnClicked(this));
        percentageBtn.addActionListener(new actionListeners.percentageBtnClicked(this));
        sqrBtn.addActionListener(new actionListeners.squareRootBtnClicked(this));
        squareBtn.addActionListener(new actionListeners.squareBtnClicked(this));
        mulBtn.addActionListener(new actionListeners.multiplyBtnClicked(this));
        divideBtn.addActionListener(new actionListeners.divideBtnClicked(this));
        addBtn.addActionListener(new actionListeners.addBtnClicked(this));
        minusBtn.addActionListener(new actionListeners.subBtnClicked(this));
        equalBtn.addActionListener(new actionListeners.equalBtnClicked(this));
        logBtn.addActionListener(new actionListeners.logBtnClicked(this));
        mRecallBtn.addActionListener(new actionListeners.mRecallBtnClicked(this));
        mAddBtn.addActionListener(new actionListeners.mAddBtnClicked(this));
        mSubBtn.addActionListener(new actionListeners.mSubBtnClicked(this));
        mClearBtn.addActionListener(new actionListeners.mClearBtnClicked(this));
        clearBtn.addActionListener(new actionListeners.clearBtnClicked(this));
        powerBtn.addActionListener(new actionListeners.powerBtnClicked(this));

        this.setTitle("Calculator");
        this.setSize(700, 300);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(this.mainPanel);
        this.setVisible(true);

        this.initializeProgramVariables();
        this.updateTextFieldOfCalculatorWithOperandString();
    }

    // utility methods
    void updateTextFieldOfCalculatorWithOperandString()
    {
        this.textField.setText(currentOperandString);
    }

    void updateOperandStringAndTextFieldOfCalculatorWithString(String str)
    {
        this.currentOperandString = str;
        this.textField.setText(str);
    }
    
    void appendToCurrentOperandStringAndUpdateTextField(String numberStr)
    {
        currentOperandString = currentOperandString + numberStr;
        this.updateTextFieldOfCalculatorWithOperandString();
    }

    boolean decimalIsInCurrentOperandString()
    {
        // assume decimal not in currentOperandString initially
        boolean decimalPresent = false;

        char[] currentOperandStringAsCharArray = currentOperandString.toCharArray();
        for(char charVal : currentOperandStringAsCharArray)
        {
            if(charVal == '.')
            {
                decimalPresent = true;
                break;
            }
        }

        return decimalPresent;
    }

    void initializeProgramVariables()
    {
        operand1Double = null;
        operand2Double = null;
        currentBinaryOperation = binaryOperationType.NOT_SET;
        currentOperandString = "0";
        currentOperand = operandType.OPERAND_1;
        memoryDouble = 0.0;
        currentOperandRdyToBeReset = false;
    }

    String getCurrentOperandString()
    {
        return this.currentOperandString;
    }

    void clearCurrentOperandStringIfJustZeroAndUpdateTextField()
    {
        if(currentOperandString.equalsIgnoreCase("0"))
        {
            currentOperandString = "";
            updateTextFieldOfCalculatorWithOperandString();
        }
    }

    boolean currentOperandStringIsPositive()
    {
        // the way we determine this is by seeing whether '-' is present in currentOperandString
        boolean minusSignPresent = false;

        char[] currentOperandStringAsCharArray = currentOperandString.toCharArray();
        for(char charVal : currentOperandStringAsCharArray)
        {
            if(charVal == '-')
            {
                minusSignPresent = true;
                break;
            }
        }

        return !minusSignPresent;
    }

    // used after a calculation is completed, so can do the next operation
    void storeResultInOperand1()
    {
        operand1Double = resultDouble;
    }

    void parseAndStoreCurrentOperandStringInOperand1()
    {
        operand1Double = Double.parseDouble(currentOperandString);
    }

    void parseAndStoreCurrentOperandStringInOperand2()
    {
        operand2Double = Double.parseDouble(currentOperandString);
    }

    // this method will only be used after certain buttons are pressed (e.g., unary operator and binary operator buttons)
    void replaceTextFieldWithIntIfApplicable()
    {
        if(textField.getText().compareTo("0.") != 0)
        {
            Double currentOperandDouble = Double.parseDouble(textField.getText());

            if(currentOperandDouble == Math.floor(currentOperandDouble))
            {
                Integer currentOperandDoubleAsInt = (int) ((double) currentOperandDouble);
                this.textField.setText(currentOperandDoubleAsInt.toString());
            }
        }
    }

    public void test( String button){
        switch (button){
            case "0": zeroBtn.doClick();break;
            case "1": oneBtn.doClick();break;
            case "2": twoBtn.doClick();break;
            case "3": threeBtn.doClick();break;
            case "4": fourBtn.doClick();break;
            case "5": fiveBtn.doClick();break;
            case "6": sixBtn.doClick();break;
            case "7": sevenBtn.doClick();break;
            case "8": eightBtn.doClick();break;
            case "9": nineBtn.doClick();break;
            case "%": percentageBtn.doClick();break;
            case "-/+": signBtn.doClick();break;
            case "AC": clearBtn.doClick();break;
            case "*2": squareBtn.doClick();break;
            case "sqr": sqrBtn.doClick();break;
            case "log": logBtn.doClick();break;
            case ".": digitBtn.doClick();break;
            case "+": addBtn.doClick();break;
            case "-": minusBtn.doClick();break;
            case "*": mulBtn.doClick();break;
            case "/": divideBtn.doClick();break;
            case "**": powerBtn.doClick();break;
            case "M+": mAddBtn.doClick();break;
            case "M-": mSubBtn.doClick();break;
            case "MR": mRecallBtn.doClick();break;
            case "MC": mClearBtn.doClick();break;
            case "=": equalBtn.doClick();break;
            case "txt": System.out.println("The result is: " +
                    textField.getText());break;
            default:System.out.println("invalid input");break;
        }
    }
    /**
     * The main function of the Calculator class instantiates the gui application.
     * You can call this class-level method in another class to run the gui application.
     * */
    public static void main(String[] args) {
        Calculator calculatorInst = new Calculator();
    }
}
