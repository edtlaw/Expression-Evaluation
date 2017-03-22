package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.stream.events.Characters;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    		/** COMPLETE THIS METHOD **/
    	scalars = new ArrayList<ScalarSymbol>();
    	arrays = new ArrayList<ArraySymbol>();
    	StringTokenizer st = new StringTokenizer(expr,delims,true);
    	Stack <String> sym = new Stack <String>();
    	String tokens = "";
    	while(st.hasMoreElements()){
    		tokens = st.nextToken();
    		if(Character.isLetter(tokens.charAt(0))){
    			sym.push(tokens);
    		}
    		if(tokens.charAt(0) == '['){
    			sym.push("[");
    		}
    		else{
    			continue;
    		}
    	}
    	while(!sym.isEmpty()){
    		String popped = sym.pop();
    		if(popped.equals("[")){
    			ArraySymbol arrSym = new ArraySymbol(sym.pop());
    			if(!arrays.contains(arrSym)){
    				arrays.add(arrSym);
    			}
    			else continue;
    		}
    		else{
    			ScalarSymbol scalSym = new ScalarSymbol(popped);
    			if(!scalars.contains(scalSym)){
    				scalars.add(scalSym);
    			}
    			else continue;
    		}
    	}
    	
    
    	
    	
    	
    	
    	
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
		/** COMPLETE THIS METHOD **/
		// following line just a placeholder for compilation
    	return evaluate(expr,expr.length()-1);
    }
    private float evaluate(String expression, int exprEnd){
    	
    	Stack<Float>nums = new Stack<Float>();
    	Stack<Character>operators = new Stack<Character>();
    	StringTokenizer st = new StringTokenizer(expression,delims,true);
    	
    	int indexCt = 0;
    	int scalVal = 0;
    	int arrVal = 0;
		float nestedNum = 0;
		int enDex = 0;
		
    	String arrayTok ="";
    	String tokens ="";
    	
    
    	
    	
    	while(st.hasMoreTokens()){
    		tokens = st.nextToken();
    		
    		
    		if(tokens.equals("*")
    				||tokens.equals("+")
    				||tokens.equals("-")
    				||tokens.equals("/")
    		){
    			while(!operators.isEmpty() && hasPriority(tokens.charAt(0), operators.peek())){
    				nums.push(arithmetic(operators.pop(),nums.pop(),nums.pop()));
    			}
    			operators.push(tokens.charAt(0));
    		}
    		if(isNum(tokens)){
    			nums.push(Float.parseFloat(tokens));
    		}
    		if(tokens.equals("(") ){
    			enDex = indexBr(expression,indexCt," ");
    			nestedNum = evaluate(nestEval(expression,indexCt), enDex);
    			indexCt++;
    			nums.push(nestedNum);
    			
    			
    			/*
    			int beGex = indexBr(expression,indexCt, "open");	System.out.println("Begex: "+beGex);
    			int enDex = indexBr(expression,indexCt, "close");	System.out.println("Endex: "+enDex);
    			indexCt++;
    			String recString = expression.substring(beGex+1,enDex);		System.out.println("String: "+ recString);
    			nestedNums = evaluate(recString,enDex);		System.out.println("nestednums: "+ nestedNums);
    			
    			if(tokens.equals("[")){
    				//System.out.println(expression);
    					
    					ArraySymbol arrSym = new ArraySymbol(arrayTok);
    					int arrInd = arrays.indexOf(arrSym);
    					int [] values = arrays.get(arrInd).values;
    					
    					arrVal = values[(int)nestedNums];
    					
    					System.out.println("value in array "+arrVal);
    					nums.push((float)arrVal);
    					
    				
    			*/	
    			if(enDex == exprEnd){
    				break;
    			}
    			else{
    				
    				st = new StringTokenizer(expr.substring(enDex+1, exprEnd+1),delims,true);
    				
    			}
    			
    				
    		}
    		if(tokens.contains("[")){
    			enDex = indexBr(expression,indexCt," ");
    			nestedNum = evaluate(nestEval(expression,indexCt), enDex+1);
    			indexCt++;
				ArraySymbol arrSym = new ArraySymbol(arrayTok);
				int arrInd = arrays.indexOf(arrSym);
				int [] values = arrays.get(arrInd).values;
				arrVal = values[(int)nestedNum];
				
				nums.push((float)arrVal);
				
				if(enDex == exprEnd){
    				break;
    			}else{
    				
    				st = new StringTokenizer(expr.substring(enDex+1, exprEnd+1),delims,true);
    				
    			}
    		}
    		if((tokens.charAt(0) >= 'a' && tokens.charAt(0) <= 'z') || (tokens.charAt(0) >='A' && tokens.charAt(0) <= 'Z')){
    			if(!isArray(tokens)){
    			
    				ScalarSymbol scSym = new ScalarSymbol(tokens);
    				int scalInd = scalars.indexOf(scSym);
    				scalVal = scalars.get(scalInd).value;
    				
    				nums.push((float) scalVal);
    			}
    			if(isArray(tokens)){
    				arrayTok = tokens;
    				
    			}
    			System.out.println("array token" + arrayTok);
    		}
    		
    		
    		
    		
    		else{
    			continue;
    		}
    
    		
    		
    		
    	
    	}
    	while(!operators.isEmpty()){
    		nums.push(arithmetic(operators.pop(),nums.pop(),nums.pop()));
    	}
    	return(nums.pop());
    	}
    		
    
    

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }
    private boolean isNum(String s){
    	boolean isValidNum = false;
    	try{
    		Float.parseFloat(s);
    		isValidNum = true;
    	}
    	catch(NumberFormatException ex){
    		
    	}
    	return isValidNum;
    	
    }
    private boolean hasPriority(char opRand1, char opRand2){
    	if((opRand1 == '*' || opRand1 == '/') && (opRand2 == '+' || opRand2 == '-')){
    		return  false;
    	}
    	else{ return true;}
    	
    }
    private float arithmetic(char opRand, float numPop1, float numPop2)
    {
        switch (opRand)
        {
       
        case '+': return numPop2 + numPop1;
            
       
        case '-': return numPop2 - numPop1;
            
        
        case '*':  return numPop2 * numPop1;
           
       
        case '/':
            if (numPop2 == 0)
                throw new
                UnsupportedOperationException("dividing by 0 is illegal");
            
            return numPop2/numPop1;
        }
        
        return 0;
    }
    private int indexBr(String expr, int count, String type){
    	ArrayList<Integer> opIndex = new ArrayList<Integer>();
    	ArrayList<Integer> closeIndex = new ArrayList<Integer>();
    	Stack<Integer>location = new Stack<Integer>();
    	int indexCt = 0;
		
		
    	for(int x = 0; x<expr.length(); x++){
    		if(expr.charAt(x) == '(' || expr.charAt(x) == '['){
    			opIndex.add(0);
    			closeIndex.add(0);
    			
    		}
    	}
    	for(int i = 0; i<expr.length(); i++){
    		if(expr.charAt(i) == '(' || expr.charAt(i) == '['){
    			location.push(indexCt);
    			opIndex.set(indexCt,i);
    			indexCt++;
    		}
    		if(expr.charAt(i) == ')' || expr.charAt(i) == ']'){
    			closeIndex.set(location.pop(), i);
    		}
    	}
    	if(type == "open"){
    		return opIndex.get(count);
    		
    	}
    	else return closeIndex.get(count);
    	
    }
    private String nestEval(String expr, int indexCt){
    	
    	int beGex = indexBr(expr,indexCt, "open");	//System.out.println("Begex: "+beGex);
		int enDex = indexBr(expr,indexCt, "close");	//System.out.println("Endex: "+enDex);
		
		String recString = expr.substring(beGex+1,enDex);		//System.out.println("String: "+ recString);
		return recString;
		
		
		
		
    	
    }
    private boolean isArray(String letters){
    	boolean isArr = false;
    	for(int i = 0; i < arrays.size(); i++)
    	{
    		if(arrays.get(i).name.equals(letters)){
        			isArr = true;
    				break;
    		}
    	}
    	return isArr;
    }
   
    

}